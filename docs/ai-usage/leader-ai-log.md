# AI Usage Log — Technical Lead

Record of the AI-assisted work done by the Technical Lead during the development of GameZone
Inova.

### Entry 1

**Date:** 2026-09-06
**Tool used:** Claude Code

**Reason for use:**
Compare the workshop statement against a public reference implementation of the same exercise
before writing a single line of code, to avoid copying decisions that do not match our own
statement.

**Problem faced:**
A public repository solving the same exercise was available, but it was built by a single person
simulating a three-member team, and it was not obvious which of its decisions applied to us and
which ones contradicted the workshop we were given.

**Prompt used:**
Analiza el taller y el repositorio de referencia, y dime en qué puntos difieren y cuál debemos
seguir.

**Solution obtained and decision taken:**
Ten differences were identified. The team decided to follow the workshop statement in every one of
them, since that is what is being graded: plain-text files with `;` instead of CSV with commas, a
single `name` field in `Person` instead of first and last name, an abstract method in `Person`
(missing in the reference implementation), no purchase history stored inside `Customer`, and
`generation` as an `int`. The reference implementation was used only as a guide for the commit and
Pull Request workflow.

### Entry 2

**Date:** 2026-09-07
**Tool used:** Claude Code

**Reason for use:**
Decide how `SaleRepository` should rebuild a sale from the file, given that the file only stores
the ids of the customer, the seller and the products.

**Problem faced:**
Rebuilding a sale requires looking up entities that the services already hold in memory. The
straightforward approach is to inject the services into the repository, but that inverts the
`persistence → model` direction we had just documented in `docs/layers-diagram.md`, and the
reference implementation had to document exactly that as a known exception.

**Prompt used:**
¿Cómo cargo las ventas resolviendo los ids sin que el repositorio dependa de la capa de servicios?

**Solution obtained and decision taken:**
`SaleRepository.load(...)` now receives the lists of products, customers and sellers as parameters,
and `SaleService` — which is already above both layers — is the one that passes them in. The
repository keeps depending only on `model`, the layer rule holds with no exceptions, and the class
diagram was updated to show the real signature.

### Entry 3

**Date:** 2026-09-07
**Tool used:** Claude Code

**Reason for use:**
Define the order of the validations inside `registerSale` and how the console should report a
rejected sale.

**Problem faced:**
A sale can fail for several different reasons — no products, unknown customer, unknown seller,
unknown product, not enough stock — and each one needs its own message. Repeated product ids also
had to be counted together before comparing against the available stock, otherwise selling two
units of a product with one unit left would pass the check.

**Prompt used:**
Implementa registerSale validando en orden y contando las unidades repetidas por producto.

**Solution obtained and decision taken:**
The validations run in order and the first failure throws `IllegalArgumentException` with a message
already written in Spanish. Repeated ids are counted into a `HashMap` before comparing against
`getQuantity()`, so the check is per product and not per line. Every operation of `ConsoleUI`
wraps its service call in its own `try/catch` and prints the message, so the user never sees a
stack trace.

### Entry 4

**Date:** 2026-09-07
**Tool used:** Claude Code

**Reason for use:**
Run the end-to-end test of the ten operations and confirm that the data really survives a restart.

**Problem faced:**
Testing ten operations by hand on every change is slow and easy to get wrong, and the persistence
requirement can only be verified by closing the application and opening it again.

**Prompt used:**
Prueba las 10 operaciones en orden y luego reinicia la aplicación para verificar la persistencia.

**Solution obtained and decision taken:**
The ten operations were executed in the order defined by the workshop, feeding the menu from a
file. The sale discounted exactly one unit of each product sold (5→4 and 3→2), and a second run
confirmed that products, customers, sellers and the sale history were rebuilt correctly from
`data/`. No defect was found in this round, so no fix commit was needed.
