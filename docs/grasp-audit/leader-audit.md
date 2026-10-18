# Technical Lead GRASP Audit

## Author

Isabel Sofía Celedon Martinez — Technical Lead.

## Baseline

Audited develop commit: 701255d4209e070146c32ee5a663b08d3b4b0bb4

Audit branch: feature/grasp-audit.

Status: in progress. Assessments and peer review are pending.

ConsoleUI implements the ConsoleMenu responsibility described
in the assignment.

## Scope

The initial scope follows TEAM.md and the R7 work plan.
Method ownership must also be checked against Git history.

| Area | Class or method | Review status |
|---|---|---|
| Sales model | Sale | Pending |
| Sales persistence | SaleRepository | Pending |
| Sales coordination | SaleService | Pending |
| Console interface | ConsoleUI | Pending |
| Application startup | Main | Pending |
| Stock restoration | ProductService.restoreStock | Pending |

Methods contributed by other team members will be identified
separately before assigning audit responsibility.

### Working allocation for shared code

The technical lead confirmed the following working allocation.
Peer review of this allocation remains pending.

| Contributor | Responsibility |
|---|---|
| Isabel | Assess her contributions to Sale, SaleRepository, SaleService, ConsoleUI, Main and ProductService.restoreStock |
| Jaime | Assess his model, product, promotion, warranty, exception and validator contributions |
| Nicoll | Assess her service and repository contributions, including typed validation and exception integration |

Shared methods are reviewed by contribution rather than attributed
exclusively to the owner of the containing class.

For Sale calculations, Isabel and Jaime will designate one primary
assessment and reference it from the other report. The existing
authorship evidence is retained.

Cross-owner findings will identify each affected contribution and
will be consolidated into a single finding when they describe
the same design problem.

Reference commit:
701255d4209e070146c32ee5a663b08d3b4b0bb4

Reports based on earlier revisions must identify and review relevant
differences before their conclusions are consolidated.

This allocation does not represent approval of the individual
reports or confirmation of their pending verdicts.

### Ownership evidence from Git history

The following observations come from recent file history.
They identify contributions, not exclusive ownership of each class.

| Class | Contributions observed |
|---|---|
| Sale | Isabel contributed extraCost and preservation of canBeReturned; Jaime contributed receipt generation and promotion-related changes |
| SaleRepository | Isabel implemented persistence and promotion storage; Nicoll migrated persistence exceptions |
| SaleService | Isabel contributed promotion/warranty orchestration; Nicoll integrated typed validations |
| ConsoleUI | Isabel contributed error handling, input validation and message corrections; Jaime and Nicoll also contributed specific changes |
| Main | Isabel contributed startup protection and integration changes; Nicoll contributed warranty and return wiring |
| ProductService | Jaime contributed product management; Isabel added restoreStock and corrected registration persistence handling; Nicoll integrated typed validations |

Recent commit history is not sufficient to assign every method.
Method-level attribution will be checked at the audited commit.

The leader will distinguish her own contributions from shared
responsibilities and report cross-owner dependencies separately.

### Confirmed method ownership: ProductService.restoreStock

At audited commit `701255d4209e070146c32ee5a663b08d3b4b0bb4`,
git blame attributes the executable lines of
ProductService.restoreStock to Isabel Sofía Celedon Martinez,
commit `71a76d4c`.

The method occupies lines 179–185.

Evidence:
https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/service/ProductService.java#L179-L185

This confirms method ownership; it does not establish a GRASP verdict.

## Descriptive observations: Sale

Sale stores its identifier, date, customer, seller, product list,
additional warranty cost, promotion name and discount amount.

| Method | Responsibility | Information used |
|---|---|---|
| calculateSubtotal | Sum the price of each listed unit | Sale's product list and Product.getPrice |
| calculateTotal | Add additional costs and subtract the discount | Subtotal, extraCost and discountAmount |
| addExtraCost | Accumulate an additional amount | extraCost and the supplied amount |
| canBeReturned | Check the 30-day return deadline | Sale date and current date |
| generateReceipt | Build the receipt text | Sale data and customer, seller and product information |

Sale depends on model objects, Java collections and LocalDate.
It does not access files or invoke services or repositories.

The constructor copies the supplied product list. getProducts returns
an unmodifiable view, but the contained Product objects remain mutable.

These observations describe the code. Pattern verdicts and method
ownership require separate assessment.

## Descriptive observations: SaleRepository

SaleRepository stores and reconstructs sales using data/sales.txt.

| Method | Responsibility | Information used |
|---|---|---|
| save | Write the sales collection using UTF-8 | Supplied sales and repository file configuration |
| load | Read stored records and reconstruct sales | File contents and supplied product, customer and seller collections |
| toLine | Serialize a sale into the existing text format | Sale data and identifiers of associated objects |
| fromLine | Parse a record and create a Sale | Stored fields and supplied collections used to resolve references |

The repository depends on model classes, PersistenceException and
Java file-handling utilities. It does not invoke services or the UI.

Missing customer, seller or product references produce
PersistenceException. File-access failures and selected parsing
failures are also translated into PersistenceException with a cause.

Older records without promotion fields retain the default values
for those fields.

The repository does not select promotions, calculate new discounts
or update inventory.

These observations describe the implementation. They do not, by
themselves, establish verdicts for all nine GRASP patterns.

## Descriptive observations: SaleService

SaleService receives sale operations and coordinates their execution.
It does not read console input, print receipts or directly access files.

The main registerSale method:

1. Validates the requested identifiers.
2. Resolves the customer, seller and sold items.
3. Validates sale data and stock availability.
4. Creates the Sale.
5. Applies the selected promotion before warranty costs.
6. Assigns console warranties.
7. Updates inventory through the corresponding services.
8. Adds the sale to its collection and invokes repository.save.

Delegated responsibilities include:

| Responsibility | Collaborator |
|---|---|
| Person lookup | PersonService |
| Sale validation | SaleValidator |
| Stock validation | ProductValidator |
| Promotion selection | PromotionService |
| Discount calculation | Promotion |
| Warranty assignment | WarrantyService |
| Inventory updates | ProductService and AccessoryService |
| File persistence | SaleRepository |

SaleService directly counts requested units, generates sale identifiers,
filters its sales collection and coordinates the operation sequence.

It explicitly checks whether a sold product is a Console before
requesting warranties.

Item lookup and stock updates first use ProductService and fall back
to AccessoryService when a ResourceNotFoundException is raised.

The service receives five collaborators through its constructor and
receives WarrantyService later through a setter.

These are descriptive observations. Final GRASP verdicts require
justification and coordination with the authors of shared methods.

### Shared ownership of Sale calculations

At audited commit `701255d4209e070146c32ee5a663b08d3b4b0bb4`:

- calculateSubtotal, lines 138–144, contains contributions from
  Isabel's commit f6a7832f and Jaime's commit a77d0688.
- calculateTotal, lines 152–154, contains contributions from
  both developers. Its current expression is attributed by blame
  to merge commit 7edc23a4.

Git blame identifies the last changes to individual lines.
It does not establish exclusive conceptual ownership of a method.

The calculations are therefore recorded as shared contributions.
The team must agree who documents their assessment and reference
that assessment from the other report to avoid duplicate findings.

Evidence:

- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/model/Sale.java#L138-L154



## Descriptive observations: ConsoleUI

ConsoleUI reads keyboard input, displays menus, invokes services
and presents results or error messages.

It receives seven service collaborators through its main constructor.
It does not directly depend on repository classes.

The input helpers parse integers, decimal numbers and dates.
showError translates exception categories into Spanish messages.

The class contains interaction methods for products, people, sales,
returns, warranties, promotions and accessories.

These observations do not establish a High Cohesion or Low Coupling
verdict solely from the number of methods or collaborators.

## Descriptive observations: Main

Main constructs repositories, then services and finally ConsoleUI.

It supplies the required collaborators and connects WarrantyService
to SaleService through setWarrantyService.

It starts the interface and handles failures that escape to the
application entry point, including persistence failures during loading.

Main does not implement sale calculations or file serialization.
Its construction responsibilities must be assessed separately from
the creation of domain objects during business operations.
## 1. Information Expert

### Sale calculations

Assessment: correct for the reviewed sale calculations.
The assessment of the remaining responsibilities is pending.

Sale knows which products belong to the sale and stores its
discountAmount and extraCost.

calculateSubtotal iterates over the sale's product list and obtains
each unit price from Product. Product owns its price; Sale knows
which units must be included in this particular subtotal.

calculateTotal combines that subtotal with the sale's additional
cost and discount:

calculateSubtotal() + extraCost - discountAmount

These calculations use information available to Sale through
its own state and associated products. Reading a product price
does not, by itself, demonstrate misplaced responsibility.

Evidence:
https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/model/Sale.java#L138-L154

The methods contain contributions from Isabel and Jaime.
Their assessment must be coordinated and referenced between
the individual reports to avoid duplicate findings.

This assessment does not cover receipt formatting, stock restoration
or all responsibilities in SaleService.

## 2. Creator

### Creation of Sale objects

Assessment: correct for the reviewed Sale creation paths.
Other creation responsibilities remain pending.

SaleService.registerSale obtains the customer, seller and sold
products, generates the identifier and supplies the current date.
It therefore has the initialization data required to create Sale.

After creating the sale, SaleService coordinates its registration
and adds it to the sales collection managed by the service.

SaleRepository.fromLine creates Sale while reconstructing a stored
record. It has the parsed identifier and date and resolves the
customer, seller and products using the supplied collections.

These are different creation contexts: registering a new sale
and reconstructing an existing sale.

Evidence:

- SaleService.registerSale: new Sale followed by sales.add.
- SaleRepository.fromLine: parsed fields and resolved references
  supplied to the Sale constructor.

Source files at the audited commit:

- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/service/SaleService.java
- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/persistence/SaleRepository.java

The assessment does not claim that every creation responsibility
in the leader's scope has been reviewed.

## 3. Controller

### Sale registration flow

ConsoleUI receives the user's input and invokes SaleService.

SaleService.registerSale coordinates the operation: it resolves
participants and items, invokes validators, requests promotion
selection and warranty assignment, updates inventory through
services and delegates sale persistence to SaleRepository.

The service does not read keyboard input or print console messages.
ConsoleUI does not directly write the sales file.

This describes the separation between user interaction and
sale-operation coordination.

Evidence:

- ConsoleUI.registerSale
- SaleService.registerSale
- SaleRepository.save

Audited commit:
701255d4209e070146c32ee5a663b08d3b4b0bb4

The final Controller verdict remains pending review of the
remaining responsibilities and agreement on shared ownership.

## 4. Low Coupling

### Persistence dependencies

Assessment: correct for the reviewed dependency direction in
SaleRepository. The remaining dependencies require separate assessment.

SaleRepository depends on model classes, PersistenceException and
Java file-handling utilities.

Its load method receives product, customer and seller collections
as parameters. It does not invoke services to obtain these objects.

This keeps sale reconstruction independent of service implementations
and avoids a persistence-to-service dependency in this repository.

Evidence:

- SaleRepository imports.
- SaleRepository.load(products, customers, sellers).
- SaleRepository.fromLine.

Source:
https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/persistence/SaleRepository.java

### Dependencies requiring further discussion

SaleService depends on concrete services for products, people,
promotions, accessories and warranties.

Its findItemById and updateStockOf methods explicitly coordinate
the product and accessory catalogs.

ConsoleUI also knows several concrete service and model types.

The number of dependencies alone does not prove a violation.
The team must assess which dependencies are necessary and whether
a foreseeable change propagates unnecessarily across classes.

No definitive violation ID is assigned to these observations yet.

## 5. High Cohesion

### Sale persistence

Assessment: correct for the reviewed SaleRepository responsibilities.
The final verdict for the complete scope remains pending.

SaleRepository.save, load, toLine and fromLine all support the same
purpose: storing and reconstructing sales in the existing text format.

File access, record conversion and stored-reference resolution
contribute to this persistence responsibility.

The repository does not read user input, select promotions,
assign warranties or update stock.

Evidence:

- SaleRepository.save.
- SaleRepository.load.
- SaleRepository.toLine.
- SaleRepository.fromLine.

Source:
https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/persistence/SaleRepository.java

### Other responsibilities to assess

SaleService coordinates several steps within sale registration.
The review must distinguish necessary use-case orchestration from
specialized responsibilities that could belong to its collaborators.

ConsoleUI contains interaction methods for seven functional areas.
Its size alone does not establish a violation; the review must
identify concrete responsibilities and reasons for change.

Sale.generateReceipt combines sale data with receipt presentation.
The team must consider the original requirement and actual change
scenarios before deciding whether this is an accepted responsibility
or a design finding.

These observations are not confirmed violations and do not yet
authorize refactoring.

## 6. Polymorphism

### Delegation to model behavior

Assessment: correct for the reviewed polymorphic calls.
The final verdict for the complete scope remains pending.

SaleService.registerSale receives the selected promotion as a
Promotion reference and invokes calculateDiscount(sale).

The service does not implement a separate discount formula for
each promotion subclass.

ConsoleUI.printAccessories invokes getDescription() through
Accessory references. The concrete description is provided by
the corresponding product subtype.

Evidence:

- SaleService.registerSale: bestPromotion.calculateDiscount(sale).
- ConsoleUI.printAccessories: accessory.getDescription().

Sources:

- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/service/SaleService.java
- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/ui/ConsoleUI.java

### Type-dependent behavior requiring discussion

SaleService.registerSale explicitly checks whether a product is
a Console before assigning warranties.

This is a concrete point for evaluating type-dependent behavior.
The presence of instanceof alone does not establish a violation.

The team must determine whether this is necessary coordination
under the existing requirements or behavior that should be
assigned to a stable abstraction.

No definitive violation ID is assigned to this observation yet.

## 7. Pure Fabrication

### Dedicated sale persistence

Assessment: correct for the reviewed separation of file persistence.

SaleRepository is a technical collaborator that concentrates
file access and conversion between Sale objects and stored records.

This keeps the file path, delimiters, UTF-8 handling and parsing
outside Sale and ConsoleUI.

Sale provides sale information without knowing the sales file
format. ConsoleUI invokes services rather than writing sales
records directly.

Evidence:

- SaleRepository.save and load perform file access.
- SaleRepository.toLine and fromLine handle the record format.
- Sale contains no file-access operations.

Sources:

- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/persistence/SaleRepository.java
- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/model/Sale.java

This assessment concerns the responsibility separated into
SaleRepository. A class is not justified merely because it is
named Repository or Service; its responsibilities and dependencies
must support that separation.

## 8. Indirection

### Separation between console interaction and persistence

Assessment: correct for the reviewed sale-operation path.
The final verdict for the complete scope remains pending.

ConsoleUI invokes SaleService instead of accessing SaleRepository
or the sales file directly.

SaleService coordinates the sale operation and delegates file
persistence to SaleRepository.

This introduces intermediaries with defined responsibilities:

ConsoleUI -> SaleService -> SaleRepository

The interface does not need to know the file path, record delimiters
or parsing details to register a sale.

Evidence:

- ConsoleUI.registerSale invokes saleService.registerSale.
- SaleService.registerSale invokes repository.save.
- SaleRepository owns file access and record conversion.

Sources:

- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/ui/ConsoleUI.java
- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/service/SaleService.java
- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/persistence/SaleRepository.java

The presence of an intermediary alone does not prove good design.
Its responsibilities and dependencies must justify the separation.



## 9. Protected Variations

### Promotion calculation

Assessment: protection is present for variation in discount formulas.
Other foreseeable changes require separate assessment.

SaleService invokes calculateDiscount through a Promotion reference.
Different promotion implementations can provide their own formula
without placing each formula inside SaleService.

This protects sale coordination from changes to the calculation
implemented by an existing promotion subtype.

Evidence:

- SaleService.registerSale uses a Promotion reference.
- The discount is obtained through bestPromotion.calculateDiscount(sale).

Source:

- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/service/SaleService.java

### Limits of the observed protection

This does not establish that adding a promotion type requires
no changes anywhere in the system. Registration and persistence
may still require adaptations.

SaleService explicitly coordinates product and accessory catalogs
and identifies consoles when assigning warranties.

The team must assess how foreseeable catalog or warranty changes
would affect those responsibilities before assigning the final
Protected Variations verdict.

No definitive violation ID is assigned to these observations yet.

## Boundary reports

Pending review and team discussion.

## Out-of-scope functional findings

Pending review.

## Peer review

Pending.