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
| Sales model | Sale | Calculations assessed; shared contributions documented |
| Sales persistence | SaleRepository | Persistence and reconstruction assessed; peer review pending |
| Sales coordination | SaleService | Coordination and reviewed dependencies assessed; peer review pending |
| Console interface | ConsoleUI | Delegation assessed; L-V02 proposed and awaiting validation |
| Application startup | Main | Assembly and startup responsibilities assessed; peer review pending |
| Stock restoration | ProductService.restoreStock | L-V01 confirmed by Jaime; consolidation pending |

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

### ConsoleUI: interaction responsibilities

ConsoleUI displays menus, reads input, invokes services and presents
results or error messages.

Its registerSale method collects identifiers and warranty choices,
then invokes SaleService.registerSale.

The interface does not calculate the final sale amount or write
sales records directly.

Evidence:
https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/ui/ConsoleUI.java

#### Controller boundary

The sale interaction separates user input from application
coordination: ConsoleUI gathers the request and SaleService
coordinates the sale.

This supports the reviewed sale-operation boundary. Other menu
operations must be considered before assigning a complete verdict.

#### High Cohesion

The class contains interaction responsibilities for seven modules:
products, people, sales, returns, warranties, promotions and accessories.

Its size alone does not establish a violation. The assessment must
consider whether changes to separate module interfaces create
independent reasons to modify this class and whether separating
those interactions would improve cohesion.

A final decision about this observation remains pending.

#### Low Coupling and type-dependent interaction

ConsoleUI knows concrete service and model classes.

During sale input, it identifies consoles to offer an extended
warranty choice. This couples that interaction to the Console type.

The review must distinguish presentation-specific decisions from
business rules. The existence of a type check is not sufficient
to confirm a Polymorphism violation.

#### Error presentation

showError centralizes the Spanish messages for exception categories.
Numeric and date helpers translate parsing failures into
InvalidDataException.

This separates error presentation from repository file access,
while retaining compatibility handling for existing exceptions.


## Descriptive observations: Main

Main constructs repositories, then services and finally ConsoleUI.

It supplies the required collaborators and connects WarrantyService
to SaleService through setWarrantyService.

It starts the interface and handles failures that escape to the
application entry point, including persistence failures during loading.

Main does not implement sale calculations or file serialization.
Its construction responsibilities must be assessed separately from
the creation of domain objects during business operations.

### Main.main: responsibility review

Main.main constructs the application's repositories, services
and ConsoleUI. It supplies their collaborators and starts the UI.

It also handles failures that reach the application entry point.
It does not implement sale calculations, record serialization
or the menu's operation dispatch.

Evidence:
https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/Main.java

#### High Cohesion

The reviewed responsibilities belong to application assembly,
startup and top-level failure handling.

This supports a correct assessment for Main's startup role.
It does not establish the verdict for other classes in the scope.

#### Low Coupling

Main explicitly knows concrete repositories, services and ConsoleUI.
Changes to component construction can therefore require changes here.

This dependency is expected at the application assembly point.
The number of concrete classes alone does not demonstrate a violation.

The review must distinguish this assembly responsibility from
business classes unnecessarily constructing their own dependencies.

#### Controller

Main starts ConsoleUI but does not receive and coordinate individual
sale operations. Sale-operation coordination is assessed separately
in ConsoleUI and SaleService.

#### Review status

These conclusions concern Main's reviewed responsibilities only.
Peer confirmation and the complete scope-wide verdicts remain pending.


## Descriptive observations: ProductService.restoreStock

The method finds an existing product, reads its quantity, adds
the supplied amount, changes the product quantity and saves
the inventory through ProductRepository.

Evidence:
https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/service/ProductService.java#L179-L185

Responsibilities observed:

- ProductService locates the product in its inventory.
- Product owns the quantity value and exposes its getter and setter.
- ProductService performs the addition outside Product.
- ProductRepository performs persistence.

Questions for the pattern assessment:

- Does increasing quantity belong in Product, which owns that state,
  or is the current distribution justified?
- Does the service appropriately coordinate lookup and persistence?
- Would moving the addition into Product preserve existing behavior?

The placement of the quantity calculation requires an explicit
Information Expert assessment. No violation is confirmed solely
because the implementation uses a getter and setter.

The method changes the product before saving. Any failure-state
issue must be distinguished from a design finding and recorded
separately if it is outside the GRASP refactor scope.

## Pattern assessment summary

This table summarizes the current review status. It is not a
declaration that the complete individual audit has been approved.

| Pattern | Evidence reviewed | Current status | Remaining work |
|---|---|---|---|
| Information Expert | Sale calculations and ProductService.restoreStock | L-V01 confirmed by Jaime; sale calculations assessed separately | Assign consolidated ID and confirm the complete responsibility split |
| Creator | SaleService.registerSale and SaleRepository.fromLine | Correct for the reviewed Sale creation paths | Peer validation|
| Controller | Delegation from the seven menu areas and Main's startup role | Correct for the reviewed UI/service boundary | Peer validation |
| Low Coupling | Persistence boundaries, service coordination and Main assembly | Correct for the reviewed boundaries, with documented limitations | Peer validation |
| High Cohesion | SaleRepository, Main and ConsoleUI responsibilities | Correct observations in persistence/startup; L-V02 proposed as a violation | Author review and peer validation of L-V02 |
| Polymorphism | Promotion formulas, product descriptions and console-specific warranty interaction | Correct for the reviewed responsibilities | Peer validation |
| Pure Fabrication | SaleRepository file access and record conversion | Correct for the reviewed persistence separation | Peer validation |
| Indirection | ConsoleUI -> SaleService -> SaleRepository | Correct for the reviewed sale-operation path | Peer validation|
| Protected Variations | Promotion formula abstraction and separation of sales file details | Correct for the reviewed variation points | Peer validation |

Peer confirmation of a specific finding does not constitute approval
of all assessments in this report.

Candidates must be confirmed or rejected explicitly before the
individual audit is presented as complete.

## 1. Information Expert

### Local finding L-V01: stock-increase responsibility

Verdict: violation identified by the technical lead and confirmed
by Jaime in PR #31. Consolidated ID assignment remains pending.

| Field | Content |
|---|---|
| Local ID | L-V01 |
| Pattern | Information Expert |
| Module | Stock restoration used by returns |
| Evidence | ProductService.restoreStock, lines 179–185 of the audited commit |
| Explanation | Product owns quantity, but ProductService reads it, calculates the increased value and writes it back. The state-changing calculation is performed outside the object that owns the required information. |
| Consequence | Callers that need to increase stock must reproduce the calculation and know how to update Product. A change to the stock-increase rule may require changes in those callers. |
| Proposed solution | Give Product an operation for increasing its quantity. ProductService should locate the product, delegate the increase and persist the inventory. |
| Other developer's code | Yes. ProductService.restoreStock belongs to Isabel; Product belongs to Jaime's scope. |
| Review status | Confirmed by Jaime in PR #31; definitive scope and consolidated ID remain pending |
Evidence:
https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/service/ProductService.java#L179-L185

Proposed coordination:

1. Jaime reviews the Product-side responsibility and, if the finding
   is confirmed, provides the stock-increase operation.
2. Isabel updates restoreStock to delegate to that operation.
3. The producer change must be available before its consumer changes.

The correction must preserve existing behavior, including the effect
of supplied amounts and existing setter rules. It must not introduce
new validation or different user messages as an undocumented change.

The current sale-calculation assessments remain separate examples
within this pattern; they do not invalidate this finding.

L-V01 is a local identifier. The consolidated audit will assign
the definitive team identifier and resolve any duplicate reports.

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

### Application assembly and input construction

Main creates repositories, services and ConsoleUI because it is
responsible for assembling the application and supplying their
collaborators. This technical construction role is distinct from
creating domain objects during a business operation.

ConsoleUI.registerCustomer constructs Customer from the data it
collects, then delegates registration to PersonService. Having the
initialization data supports that creation decision; the service
retains registration and persistence coordination.

Verdict: correct for the reviewed creation responsibilities in
SaleService, SaleRepository, Main and ConsoleUI.

This conclusion does not transfer ownership of shared methods.
Peer validation remains pending.

## 3. Controller

Verdict: correct for the reviewed separation between console
interaction and application-operation coordination.
Peer validation remains pending.

ConsoleUI receives user input and delegates operations to services:

| Menu area | Collaborator |
|---|---|
| Products | ProductService |
| People | PersonService |
| Sales | SaleService |
| Returns and monthly balance | ReturnService |
| Warranties | WarrantyService |
| Promotions | PromotionService |
| Accessories | AccessoryService |

For sale registration, ConsoleUI collects identifiers and warranty
choices. SaleService coordinates validation, participant and item
resolution, promotions, warranties, inventory updates and persistence.

The console does not directly access repositories or write data
files. SaleService does not read keyboard input or print messages.

Main constructs the components and starts ConsoleUI. It is the
application assembly point, rather than the controller of individual
sale operations.

Evidence at the audited commit:

- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/ui/ConsoleUI.java
- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/service/SaleService.java
- https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/Main.java

This is a static assessment of responsibility delegation, not
evidence that every menu operation was executed.

The separate High Cohesion proposal L-V02 remains subject to
review. Correct delegation does not automatically demonstrate
high cohesion in the complete interface class.

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
### Assessment of the reviewed service dependencies

Verdict: correct for the reviewed dependency boundaries.

SaleService needs collaborators for people, inventory, promotions,
warranties and persistence to coordinate the existing sale operation.

The product/accessory fallback explicitly reflects the two existing
catalogs. This creates coupling to that organization, but the reviewed
evidence does not establish that this dependency is unnecessary.

No additional violation is assigned solely because the collaborators
are concrete classes. A proposed abstraction must identify the change
it protects and demonstrate a benefit beyond adding another interface.

Main concentrates component construction, while ConsoleUI invokes
services rather than accessing repositories directly.

These conclusions accept the reviewed dependencies with their stated
limitations. They do not establish compliance with every SOLID principle.

Peer validation remains pending.

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

### Candidate L-V02: module interaction concentrated in ConsoleUI

Status: proposed violation supported by the responsibility analysis;
author review and peer validation remain pending.

| Field | Content |
|---|---|
| Local ID | L-V02 |
| Pattern | High Cohesion |
| Module | Console interaction across the integrated modules |
| Evidence | ConsoleUI contains showProductMenu, showPersonMenu, showSaleMenu, showReturnMenu, showWarrantyMenu, showPromotionMenu and showAccessoryMenu, together with their interaction methods |
| Responsibility concern | Changes to the interaction of separate modules require modifications to the same class |
| Consequence | Developers working on independent module interfaces must edit ConsoleUI, increasing coordination needs and the possibility of overlapping changes |
| Proposed solution | Evaluate separating module-specific console interactions into focused UI collaborators while retaining main navigation in ConsoleUI |
| Behavior constraint | Preserve menu options, prompts, output ordering, error messages and navigation |
| Ownership | Shared contributions require method-level coordination with Jaime and Nicoll |

Source:
https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/ui/ConsoleUI.java

The concern is not the number of lines alone. It is the concentration
of module-specific interactions that can change independently.

The proposed High Cohesion violation is supported by independent
module-specific interaction responsibilities sharing one class.

For example, changing promotion-registration prompts and changing
warranty-query interaction both require editing ConsoleUI, although
they belong to separate use cases.

The proposed correction is to move module-specific interactions
into focused UI collaborators while retaining main navigation
in ConsoleUI.

This must preserve prompts, menu options, output ordering, error
handling and navigation. Additional classes are justified only
if they create focused responsibilities rather than merely
redistributing methods.

This is an AI-assisted proposal, not a completed personal assessment
or peer-approved finding. It requires author review and validation
before entering the definitive consolidated finding list.

No Java modification is authorized by this candidate entry.


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
### Console-specific warranty behavior

Verdict: correct for the reviewed polymorphic delegation and
the existing console-specific warranty requirement.

Promotion formulas are delegated through Promotion.calculateDiscount.
Product descriptions are obtained through polymorphic model methods.

SaleService identifies consoles before assigning their warranties.
ConsoleUI identifies consoles when collecting the optional extended
warranty choice.

These checks correspond to an existing operation restricted to consoles.
Their presence alone does not demonstrate a missing polymorphic
responsibility.

No additional violation is confirmed without showing how moving
the decision would improve responsibility assignment while preserving
the required interaction and behavior.

Peer validation remains pending.

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

### Scope conclusion

Verdict: correct for the reviewed separation of technical
responsibilities.

SaleRepository isolates sales file access and record conversion.
SaleService coordinates application operations without placing
file serialization in Sale or ConsoleUI.

Main performs application assembly. ConsoleUI handles interaction
and error presentation.

These roles are justified by their responsibilities, not simply
by their class names. The separate cohesion proposal L-V02 does
not invalidate the decision to keep UI responsibilities outside
the domain model.

Peer validation remains pending.

## 8. Indirection

### Separation between console interaction and persistence

Assessment: correct for the reviewed sale-operation path.
The assessment below includes the reviewed console-specific
warranty decisions. Peer validation remains pending.
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

### Scope conclusion

Verdict: correct for the reviewed interaction and persistence paths.

ConsoleUI invokes the corresponding services for the seven menu
areas. For sales, SaleService delegates storage to SaleRepository
and invokes specialized collaborators for inventory, people,
promotions and warranties.

Main supplies these collaborators during application assembly.

Directly reading returned model objects for presentation does not
require an additional intermediary by itself.

This conclusion concerns the reviewed collaborations, not an
assertion that every dependency requires or already uses an interface.

Peer validation remains pending.



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

### Scope and limits of protection

Verdict: correct for the reviewed protection of discount formulas
and separation of file-access details from console interaction.

Promotion.calculateDiscount allows formulas to vary behind the
Promotion abstraction.

SaleRepository concentrates the sales file representation.
ConsoleUI does not need to know its path or record delimiters.

This does not mean that adding a promotion type or changing storage
requires no changes anywhere. Registration, construction and persistence
may still need adaptations.

No additional violation is confirmed merely because the application
does not anticipate every possible future extension.

Any later finding must identify a concrete variation point,
its affected responsibilities and the proposed stable boundary.

Peer validation remains pending.

## Boundary reports

L-V01 crosses the responsibilities of Isabel and Jaime.

Jaime confirmed in PR #31 that it matches a concern already
reported in section 12 of developer1-audit.md.

The agreed sequence, if retained in the consolidated audit, is:

1. Jaime provides Product.increaseStock(int).
2. Isabel updates ProductService.restoreStock to delegate to it.

Both source reports must be referenced under one consolidated
finding rather than counted as separate violations.

Jaime also proposed including AccessoryService.restoreStock.
That extension requires Nicoll's review and is not yet recorded
as a confirmed responsibility assignment.

Isabel's assessment of calculateSubtotal and calculateTotal
will be the primary reference. Jaime will document his receipt
and discount-field contributions, which Isabel will reference.

Source discussion:
https://github.com/iceledon/Gamezone-inova/pull/31

Confirmation of L-V01 is not approval of the complete leader audit.
Implementation remains deferred until the consolidated audit
is reviewed and merged.

## Out-of-scope functional findings

The following functional concerns were reported by Nicoll in PR #34,
using the same reference commit as this audit:

701255d4209e070146c32ee5a663b08d3b4b0bb4

| Reported concern | Source | Status |
|---|---|---|
| restoreStock accepts zero or negative amounts and its addition may overflow | Nicoll's validation review | Reported; requires team verification |
| Stock changes may remain in memory when persistence fails | Nicoll's validation review | Reported; requires team verification |
| Sale registration can fail after some inventory or warranty changes | Nicoll's validation review | Reported; requires team verification |
| Persisted monetary values may include non-finite values | Nicoll's validation review | Reported; requires team verification |

Source:
https://github.com/iceledon/Gamezone-inova/pull/34

These concerns are distinct from L-V01, which evaluates where
the stock-increase calculation belongs.

They must not be treated as confirmed GRASP violations merely
because they affect the same methods.

Introducing new input restrictions or changing failure recovery
may alter visible behavior. Such corrections require an explicit
scope decision and must not be included silently in a
behavior-preserving GRASP refactor.

This section records another contributor's reported concerns;
it does not claim that Isabel executed tests confirming them.

## Peer review

Pending.

