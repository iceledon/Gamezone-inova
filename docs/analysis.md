# Analysis — GameZone Inova

Answers to the orienting questions of the workshop. Every decision recorded here is the one
actually implemented in the source code, and it matches the diagrams under `docs/`.

## People

### Q1: What attributes are common to all people who interact with the store, and which are specific to each type of person? How is this distinction reflected in a class hierarchy?

Every person who interacts with the store shares `id`, `name` and `phone`, because each of them
must be identified and contacted regardless of the role they play. `Customer` adds `email`, which
only makes sense for someone who buys products, while `Seller` adds `employeeCode` and `shift`,
which only make sense for an employee of the store. The distinction is reflected by placing the
shared attributes in a base `Person` class and the role-specific attributes in `Customer` and
`Seller`, which extend it.

### Q2: Should there be a class representing a "generic person" without specifying a role? Why or why not? What implication does this decision have on the possibility of instantiating this class?

A `Person` class is needed to hold the state and behavior common to both roles and avoid
duplicating them, but it must never represent a real actor of the store on its own: everyone who
interacts with GameZone is either a customer or a seller, never an unqualified "person". For that
reason `Person` is declared `abstract`, which means it cannot be instantiated directly; the
compiler enforces that only its concrete subclasses `Customer` and `Seller` can be created.
`Person` also declares the abstract method `getRoleDescription()`, so every subclass is forced to
state which role it plays inside the store.

## Products

### Q3: What characteristics do all products sold by the store share, regardless of type? Which characteristics are specific to each product type?

Every product, whether a video game or a console, shares `id`, `title`, `price` and `quantity`,
since those attributes are required to catalog an item, put a price on it and track how many units
are left in the inventory. `VideoGame` adds `platform`, `genre` and `ageRating`; `Console` adds
`brand`, `model` and `generation`. Each of those extra attributes only makes sense for its own
product type, so they belong in the subclass and not in the shared parent.

### Q4: Each type of product must be able to present a description that integrates its particular characteristics. How should this behavior be declared in the base class to guarantee that all subclasses implement it in their own way? What object-oriented programming mechanism enables this?

The base `Product` class declares `getDescription()` as an abstract method, with no body. Because
the method is abstract, the compiler forces every concrete subclass to provide its own
implementation that combines the inherited attributes with its own ones. The mechanism that makes
this useful at runtime is **polymorphism with dynamic binding**: the user interface can iterate
over a `List<Product>` and call `getDescription()` on each element without knowing whether it is
holding a `VideoGame` or a `Console`, and the JVM resolves the correct implementation at execution
time.

## Sales and relationships

### Q5: A sale involves a customer, a seller, and one or more products. What kinds of relationships exist between the class representing the sale and the other classes of the system? Are these relationships of inheritance, association, composition, or another type? Justify.

`Sale` is related to `Customer` and `Seller` through **association**: a sale holds a reference to
exactly one customer and one seller, but neither of them depends on the sale to exist, and the same
person takes part in many independent sales. `Sale` is related to `Product` through **aggregation**
(multiplicity `1..*`): the sale contains a list of products, but those products already exist in
the store inventory before the sale and keep existing after it, so their life cycle is not owned by
the sale. It is not composition, because deleting a sale must not delete the products it sold, and
none of these are inheritance relationships, since a sale is not a kind of person or product.

### Q6: Should the sale be responsible for calculating its own total, or should this responsibility fall on another class? Justify your decision.

The sale calculates its own total through `calculateTotal()`. The total is derived exclusively from
data the `Sale` object already owns — its own list of products — so keeping the calculation inside
the class respects cohesion and the single responsibility principle: the object that holds the data
is the one that computes over it. `SaleService` still orchestrates the registration process
(validating the request, checking stock and updating the inventory), but the arithmetic of adding up
the prices belongs to `Sale` itself.

## Business constraints

### Q7: How does the design guarantee that a sale cannot be registered without at least one product? At what point in the system should this rule be validated?

The rule is validated inside `SaleService.registerSale(...)`, before any `Sale` object is built or
persisted: if the received list of product ids is null or empty, the operation is rejected with an
`IllegalArgumentException` carrying a message for the user. The validation lives in the service
layer, not in the user interface and not in the model, because it is a business rule that must hold
no matter who invokes the operation; putting it in the console menu would make it impossible to
reuse, and putting it in the model would spread business decisions across the domain classes.

### Q8: How does the design reflect the automatic update of inventory when a sale is registered? Which classes are involved in this operation?

When `SaleService.registerSale(...)` runs, it first resolves the customer, the seller and every
requested product through `PersonService` and `ProductService`. It then counts how many units of
each product the sale requests — a product id can appear more than once — and compares that number
against the available `quantity` of the product. Only if every check passes does it call
`ProductService.updateStock(...)`, which internally invokes `Product.decreaseStock(int)` on the
affected product and persists the new inventory state through `ProductRepository`. The classes
involved are therefore `SaleService` as the orchestrator, `ProductService` and `Product` for the
stock update itself, and `ProductRepository` and `SaleRepository` to persist the resulting state.

## Layered organization

### Q9: The system must be organized into four layers: model, persistence, services, and user interface. What type of classes belong in each layer? What criterion allows one to decide in which layer a class should be placed?

The `model` layer holds the pure domain entities — `Person`, `Customer`, `Seller`, `Product`,
`VideoGame`, `Console` and `Sale` — which know their own state and behavior but perform no
input/output. The `persistence` layer holds the classes that read and write those entities to the
plain-text files under `data/`: `PersonRepository`, `ProductRepository` and `SaleRepository`. The
`service` layer holds the classes that enforce the business rules and coordinate persistence:
`PersonService`, `ProductService` and `SaleService`. The `ui` layer holds `ConsoleUI`, the only
class that talks to the end user. The criterion for placing a class is its responsibility: does it
represent business data, does it move data to and from storage, does it decide what is allowed, or
does it interact with the person sitting at the keyboard?

### Q10: Why should the logic for saving and retrieving data from files not be inside the domain classes? What problems arise when these responsibilities are mixed?

Domain classes must model business concepts, not storage formats. If `Product` knew how to write
itself to a file, three problems would appear at once: changing the persistence format would force
edits to the domain classes, which have nothing to do with that decision; testing the business
logic would require a real file system instead of plain objects in memory; and the class would have
two reasons to change — a business one and a technical one — which is exactly what the single
responsibility principle forbids. Isolating that logic in the `persistence` layer means the storage
mechanism can be replaced without a single change to the model.

### Q11: What dependencies are allowed between the layers, and which are forbidden? Justify the meaning of the allowed dependencies.

The allowed dependencies are `ui → service`, `service → persistence`, `service → model` and
`persistence → model`. The `model` layer depends on nothing. Every dependency in the opposite
direction is forbidden: the model must never reference a repository, a service or the console, and
the persistence layer must never call a service. Skipping a layer is forbidden as well — `ConsoleUI`
never instantiates a repository directly, it always goes through a service. This inward flow keeps
the domain at the stable core of the application while the outer layers, which are the ones most
likely to change, depend on it and not the reverse; it also prevents circular dependencies and
allows each layer to be tested or replaced on its own.
