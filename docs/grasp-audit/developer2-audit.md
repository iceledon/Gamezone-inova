# Developer 2 — GRASP Audit

## Author
Nicoll Jhoana Gómez Siosi

## Baseline
- Branch: develop
- Commit: 3dff9c12c7c523bd8658e448eeb236b11ec94818
- Audit branch: feature/grasp-audit

## Scope
- People: Person, Customer, Seller, PersonRepository, PersonService.
- Accessories: AccessoryRepository, AccessoryService.
- Promotions: PromotionRepository, PromotionService.
- Returns and monthly balance: ReturnRepository, ReturnService.
- Warranties: WarrantyRepository, WarrantyService.
- Exception and validator usage within the classes listed above.

## Initial compilation
Compilation using javac --release 17 completed with exit code 0.
Maven verification remains pending because mvn was unavailable.

## Audit status
In progress. No functional code changes have been made for this audit.
Pattern verdicts and findings will be recorded after reviewing the evidence.

## 1. Information Expert
### People module

Verdict: correct for the reviewed responsibilities.

Evidence:
- Customer.getRoleDescription() and Seller.getRoleDescription()
  provide the description corresponding to their own role.
- PersonService.findCustomerById() and findSellerById() search
  the corresponding collections loaded through PersonRepository.
- PersonRepository.saveCustomers() and saveSellers() handle
  serialization using the file paths and record formats they own.

Each responsibility uses the information available to the class
or obtained through its collaborators.

### Accessories module

Verdict: correct for inventory searches and compatibility delegation.

Evidence:
- AccessoryService.find(String) searches the inventory collection
  owned by the service using accessory.getId().
- findAccessoriesCompatibleWith(String) delegates each compatibility
  check to accessory.isCompatibleWith(consoleId).
- AccessoryRepository.toLine() and fromLine() use the CSV format
  defined within the repository.

The service uses its inventory to locate accessories and delegates
compatibility decisions to the model. The repository owns the
information needed to interpret stored records.

The type-classification candidate V-01 is recorded under Polymorphism.

### Promotions module

Verdict: correct for discount calculation delegation.

Evidence: PromotionService.findBestPromotionFor(Sale) calls
promotion.calculateDiscount(sale) through a Promotion reference.

The service delegates each discount calculation to the actual
promotion object, then compares the returned amounts. It does not
use subtype checks to choose the discount calculation algorithm.

Only active promotions are evaluated. The initial best discount
is zero, and the strict greater-than comparison preserves the
first promotion encountered when positive discounts are equal.

PromotionRepository.toLine() and fromLine() distinguish record
types for persistence. These conversion responsibilities are
separate from the discount calculation assessed here.

### Returns module

Verdict: correct for the reviewed eligibility delegation and aggregations.

Evidence:
- ReturnService.registerReturn() delegates return-period eligibility
  to sale.canBeReturned() and searches sale.getProducts() for
  the requested product identifiers.
- calculateMonthlySales() filters sales by date and obtains each
  amount through sale.calculateTotal().
- calculateMonthlyReturns() filters returns by date and obtains
  each amount through return.getRefundAmount().
- generateMonthlyBalance() subtracts monthly refunds from monthly sales.

The service obtains the information needed for these operations
through its collections and collaborators. Candidate V-03 separately
assesses whether monthly reporting should belong to another class.

### Warranties module

Verdict: correct for the reviewed searches and calculation delegation.

Evidence:
- WarrantyService.findWarrantyByProduct() searches its collection
  using both product and sale identifiers.
- listActiveWarranties() delegates validity checks to warranty.isActive(today).
- listWarrantiesExpiringSoon() obtains each end date through
  warranty.getEndDate() and applies inclusive date limits.
- cancelWarranties() obtains each matching warranty's additional
  cost through warranty.getAdditionalCost().

The service owns the collection needed for searching and filtering.
Warranty objects provide their coverage information and costs.
The repository resolves stored references against supplied collections.

## 2. Creator
### People module

Verdict: correct for reconstructing stored customers and sellers.

Evidence:
- PersonRepository.loadCustomers() calls
  new Customer(parts[0], parts[1], parts[2], parts[3]).
- PersonRepository.loadSellers() calls
  new Seller(parts[0], parts[1], parts[2], parts[3], parts[4]).

The repository has the initialization data read from each record,
so it has the information needed to reconstruct these objects.

### Accessories module

Verdict: correct for accessory creation and reconstruction.

Evidence:
- AccessoryService.registerController() creates a Controller.
- AccessoryService.registerCable() creates a Cable.
- AccessoryService.registerMemory() creates a Memory.
- AccessoryRepository.fromLine(String) reconstructs these objects
  using the fields read from a CSV record.

The service has the registration data and adds each new accessory
to its inventory collection. The repository has the initialization
data required to reconstruct stored accessories.

### Promotions module

Verdict: correct for promotion creation and reconstruction.

Evidence:
- PromotionService.registerPercentageDiscount() creates
  a PercentageDiscount.
- registerCategoryDiscount() creates a CategoryDiscount.
- registerBulkPurchaseDiscount() creates a BulkPurchaseDiscount.
- PromotionRepository.fromLine(String) reconstructs these objects
  using the discriminator and fields read from the stored record.

The service receives the initialization data and adds each promotion
to its collection. The repository has the stored data needed
to reconstruct each concrete promotion.

### Returns module

Verdict: correct for return creation and reconstruction.

Evidence:
- ReturnService.registerReturn() creates a Return using the identifier,
  current date, original sale, selected products, reason and warranty refund.
  It then adds the object to its returns collection.
- ReturnRepository.fromLine(String) reconstructs a Return using stored
  fields and resolved references.

The service coordinates the initialization data and records the new return.
The repository reconstructs stored objects; its dependency on services
for resolving references is assessed separately in V-02.

### Warranties module

Verdict: correct for warranty creation and reconstruction.

Evidence:
- WarrantyService.assignBasicWarranty() creates a BasicWarranty.
- assignExtendedWarranty() creates an ExtendedWarranty.
- Both methods receive the product, sale and start date, generate
  an identifier and add the warranty to the service collection.
- WarrantyRepository.fromLine() reconstructs the concrete warranty
  using stored fields and references resolved from the supplied lists.

The service has the initialization data and records newly assigned
warranties. The repository has the information needed to reconstruct
stored warranties.

## 3. Controller
### People module

Verdict: correct for customer registration coordination.

Evidence: PersonService.registerCustomer(Customer).

The method receives the customer registration request, delegates data
and uniqueness checks to PersonValidator, loads existing customers,
adds the customer and delegates saving to PersonRepository.

It coordinates the operation without reading console input, displaying
results or handling file access directly.

### Accessories module

Verdict: correct for registration and stock-operation coordination.

Evidence: AccessoryService.registerController(), registerCable(),
registerMemory(), updateStock() and restoreStock().

The registration methods check identifier uniqueness, create the
requested accessory, add it to the inventory and delegate saving
to AccessoryRepository.

The stock methods locate the accessory, update its quantity through
model methods and delegate persistence to the repository.

These methods coordinate operations without reading console input,
displaying results or accessing files directly. This assessment
does not establish that all input and business validations are complete.

### Promotions module

Verdict: correct for promotion registration coordination.

Evidence: PromotionService.registerPercentageDiscount(),
registerCategoryDiscount() and registerBulkPurchaseDiscount().

These methods check identifier uniqueness, create the requested
promotion, add it to the collection and delegate saving to
PromotionRepository. registerCategoryDiscount() also checks
the accepted target category.

The service coordinates registration without reading console input,
displaying results or accessing files directly. This assessment
does not establish that all input validations are complete.

### Returns module

Verdict: correct for coordinating the return-registration operation.

Evidence: ReturnService.registerReturn(String, List<String>, String).

The method obtains the original sale, checks return eligibility
and product membership, requests warranty cancellation for consoles,
creates the return, restores stock through the appropriate service
and delegates saving to ReturnRepository.

It coordinates collaborators without reading console input,
displaying results or accessing files directly.

This verdict concerns responsibility assignment. It does not establish
complete validation coverage or atomicity across persistence operations.

### Warranties module

Verdict: correct for warranty assignment and cancellation coordination.

Evidence: WarrantyService.assignBasicWarranty(),
assignExtendedWarranty() and cancelWarranties().

The assignment methods create the requested warranty, add it to
the collection and delegate saving to WarrantyRepository.

cancelWarranties() identifies warranties matching the product and sale,
sums their additional costs, removes the matches and delegates saving.

These methods coordinate warranty operations without reading console
input, displaying results or accessing files directly.

## 4. Low Coupling
### People module

Verdict: correct for the reviewed dependency boundaries.

Evidence: PersonService stores the PersonRepository received through
its constructor and delegates validation to PersonValidator.
It has no dependency on ConsoleUI or Java file APIs.

PersonRepository depends on Customer, Seller and PersistenceException,
without depending on services or the user interface. Person, Customer
and Seller have no persistence dependencies.

PersonService depends on a concrete repository class. This dependency
alone does not establish a violation; file handling remains isolated
behind the repository methods.

### Accessories module

Assessment: file-access dependencies are separated; subtype coupling
is recorded in candidate V-01.

Evidence:
- AccessoryService receives AccessoryRepository through its constructor
  and delegates loading and saving to it.
- AccessoryRepository uses model classes and file APIs without
  depending on services or the user interface.
- AccessoryService.typeOf() explicitly references Controller,
  Cable and Memory.

The repository boundary keeps CSV details out of the service.
The classification dependency requires reviewing the service when
new accessory types are introduced. This is a related consequence
of V-01, not a separate finding.

### Promotions module

Verdict: correct for the reviewed dependency boundaries.

Evidence:
- PromotionService receives PromotionRepository through its constructor.
- findBestPromotionFor(Sale) uses Promotion.calculateDiscount(Sale)
  without selecting a calculation algorithm by subtype.
- PromotionRepository depends on model classes and file APIs,
  without depending on services or the user interface.

The service depends on concrete promotion classes for registration
and on Sale as calculation input. These collaborations support
its assigned responsibilities. File formats and file access remain
inside the repository.

### Returns module — finding V-02

Status: pending team validation.
Primary pattern: Low Coupling.
Related pattern: Indirection.
Module: Returns / R3.
Evidence: ReturnRepository constructor, service fields and
fromLine(String).

ReturnRepository imports and stores SaleService, ProductService
and AccessoryService. fromLine() uses these services to resolve
stored sale and product identifiers.

Consequence: persistence depends on the service layer. Changes
to service lookup contracts can require repository changes,
and isolated repository verification requires service collaborators.

Proposed solution: provide the already-loaded sales, products
and accessories as load parameters. Resolve identifiers against
those collections without depending on service classes.

Preserve record order, product-first lookup, missing-reference
behavior, warranty-refund defaults and the existing file format.

Ownership: Nicoll owns ReturnRepository and ReturnService.
Coordinate construction and loading changes with Isa, who owns Main.

### Warranties module

Verdict: correct for the repository dependency direction.
Startup coordination requires discussion with Isa.

Evidence:
- WarrantyRepository.loadAll(List<Product>, List<Sale>) receives
  the entities needed to resolve stored references.
- fromLine(), findProduct() and findSale() use those lists
  without depending on service classes.
- WarrantyService receives WarrantyRepository through its constructor.

The repository depends on model classes and file APIs.
Unlike ReturnRepository in V-02, it does not call services
to reconstruct stored entities.

WarrantyService's constructor also loads sales through SaleRepository
using data obtained from three services. Review this loading
coordination with Isa before deciding whether a correction is needed.

## 5. High Cohesion
### People module

Verdict: correct within the reviewed scope.

PersonService coordinates customer registration and person lookups.
Its registerCustomer() method delegates validation to PersonValidator
and file operations to PersonRepository.

PersonRepository groups the related persistence operations:
saveCustomers(), loadCustomers(), saveSellers() and loadSellers().

Person, Customer and Seller contain person data and role descriptions,
without performing file access. These responsibilities remain focused
within each class.

### Accessories module

Verdict: correct for the inventory and persistence responsibility groups.

Evidence:
- AccessoryService groups registration, listing, filtering,
  compatibility searches and stock operations for accessories.
- AccessoryRepository.saveAll(), loadAll(), toLine() and fromLine()
  handle accessory persistence and CSV conversion.

The service coordinates inventory operations, while the repository
concentrates file handling. Neither class handles console interaction.

The classification method typeOf() is assessed separately in
candidate V-01 under Polymorphism.

### Promotions module

Verdict: correct for the reviewed responsibility groups.

Evidence:
- PromotionService groups promotion registration, listing,
  active-date filtering, identifier lookup and best-discount selection.
- PromotionRepository.saveAll(), loadAll(), toLine() and fromLine()
  group file access and promotion record conversion.

The service manages the promotion catalog and selection policy.
The repository handles persistence. Neither class handles console
interaction, and discount algorithms are delegated to the model.

### Returns module — candidate finding V-03

Status: pending team confirmation.
Primary pattern: High Cohesion.
Module: Returns / R3.

Evidence:
- ReturnService.registerReturn() coordinates return registration.
- viewAllReturns(), viewReturnsByCustomer() and viewReturnsBySale()
  query registered returns.
- calculateMonthlySales(), calculateMonthlyReturns() and
  generateMonthlyBalance() provide financial reporting.

The class combines return operations with monthly reporting,
including aggregation of sales obtained from SaleService.

Potential consequence: changes to financial reports can require
modifying the same class that coordinates returns.

Proposed solution: evaluate moving monthly calculations into a
dedicated reporting service. Preserve existing public entry points
through delegation where needed.

Preserve month/year filtering, operation order and the calculation:
sales dated in the selected month minus refunds dated in that month.

Ownership: Nicoll. Coordinate affected callers with Isa.
The team must confirm whether separation improves this design
before treating the candidate as a required correction.

### Warranties module

Verdict: correct for the reviewed operational responsibility groups.
Constructor loading coordination remains a discussion point.

Evidence:
- WarrantyService groups warranty assignment, lookup, active and
  expiration filtering, and cancellation.
- WarrantyRepository.saveAll(), loadAll(), toLine() and fromLine()
  group warranty persistence and record conversion.

Operational methods focus on managing warranties, while file handling
remains in the repository. The constructor's additional sale-loading
responsibility must be discussed with Isa, as noted under Low Coupling.
No separate violation is confirmed for that coordination yet.

## 6. Polymorphism

### People module

Verdict: correct for the role-description implementations.

Evidence:
- Person declares the abstract method getRoleDescription().
- Customer overrides it to return "Cliente".
- Seller overrides it to return "Vendedor".

Both subclasses implement the same operation with behavior specific
to their role. A call through a Person reference selects the
implementation of the actual object.

### Promotions module

Verdict: correct for discount calculation delegation.

Evidence: PromotionService.findBestPromotionFor(Sale) calls
promotion.calculateDiscount(sale) through a Promotion reference.

The service delegates each discount calculation to the actual
promotion object, then compares the returned amounts. It does not
use subtype checks to choose the discount calculation algorithm.

Only active promotions are evaluated. The initial best discount
is zero, and the strict greater-than comparison preserves the
first promotion encountered when positive discounts are equal.

PromotionRepository.toLine() and fromLine() distinguish record
types for persistence. These conversion responsibilities are
separate from the discount calculation assessed here.

### Accessories module — candidate finding V-01

Status: pending team confirmation.
Primary pattern: Polymorphism.
Related pattern: Protected Variations.
Module: Accessories / R1.
Evidence: AccessoryService.typeOf(Accessory).

The method checks instanceof Controller, Cable and Memory to return
"CONTROLLER", "CABLE" or "MEMORY". listAccessoriesByType(String)
depends on this classification.

Consequence: adding another accessory subtype requires modifying
the service classification method.

Proposed solution: discuss a common type-query operation in the
Accessory hierarchy, then delegate classification to the accessory.
Preserve the existing type labels and case-insensitive filtering.

Cross-owner coordination: Jaime owns the model hierarchy;
Nicoll owns the service change.

Repository boundary: AccessoryRepository.toLine() and fromLine()
also distinguish accessory types. Their serialization and object
reconstruction responsibilities must be assessed separately.
CSV handling must remain in the persistence layer.

### Returns module

Assessment: explicit type decisions require boundary review;
no additional violation is confirmed from these checks alone.

Evidence: ReturnService.registerReturn() checks instanceof Console
before cancelling warranties and instanceof Accessory before choosing
the service responsible for stock restoration.

These checks route operations to different collaborators. They do not
select alternative implementations of a method currently declared
by Product.

The team must assess whether a common collaborator contract would
improve this routing. File access and service coordination must remain
outside the model. Coordinate model boundaries with Jaime and sales
integration with Isa before proposing a correction.

### Warranties module

Assessment: service operations use the common Warranty contract.
Confirm subtype-specific implementations with Jaime's model audit.

Evidence:
- listActiveWarranties() calls warranty.isActive(today).
- listWarrantiesExpiringSoon() calls warranty.getEndDate().
- cancelWarranties() calls warranty.getAdditionalCost().

These operations use Warranty references without subtype checks
to select coverage or cost calculations. The model audit must identify
which implementations are inherited and which vary by subtype.

WarrantyRepository.toLine() uses instanceof ExtendedWarranty
to choose the stored discriminator. This persistence decision
requires separate assessment and does not by itself confirm
a violation of Polymorphism.

## 7. Pure Fabrication
### People module

Verdict: correct for separating file persistence from person entities.

Evidence: PersonRepository.saveCustomers(), loadCustomers(),
saveSellers() and loadSellers().

PersonRepository is a technical class that groups file persistence
operations. Assigning these operations to the repository keeps
Person, Customer and Seller independent of file paths, delimiters
and Java file APIs.

### Accessories module

Verdict: correct for assigning CSV persistence to a technical class.

Evidence: AccessoryRepository.saveAll(), loadAll(), toLine()
and fromLine().

AccessoryRepository groups file access and record conversion instead
of assigning these tasks to Accessory, Controller, Cable or Memory.

This separation keeps CSV paths, separators and file APIs within
the persistence layer. Any proposed correction for subtype handling
must preserve this boundary.

### Promotions module

Verdict: correct for assigning persistence to a technical class.

Evidence: PromotionRepository.saveAll(), loadAll(), toLine()
and fromLine().

PromotionRepository groups file access and record conversion,
keeping file paths, separators and encoding details out of
the promotion entities and PromotionService.

PromotionService coordinates catalog operations and best-promotion
selection, allowing individual promotion classes to concentrate
on their own discount behavior.

### Returns module

Verdict: correct for assigning file persistence to a technical class.

Evidence: ReturnRepository.saveAll(), loadAll(), toLine()
and fromLine().

ReturnRepository groups return-file access and record conversion.
ReturnService delegates saving to repository.saveAll(returns),
keeping file paths, separators and writer operations out of
the return-registration method.

The repository's dependency on service classes is a separate
coupling issue recorded in V-02. This verdict does not dismiss it.

### Warranties module

Verdict: correct for assigning file persistence to a technical class.

Evidence: WarrantyRepository.saveAll(), loadAll(), toLine()
and fromLine().

WarrantyRepository groups file access, record conversion and
stored-reference resolution. File paths, separators, encoding
and Java file APIs remain within the persistence layer.

WarrantyService delegates loading and saving through repository
methods, keeping these technical details out of its assignment,
consultation and cancellation operations.

## 8. Indirection
### People module

Verdict: correct for separating service operations from file access.

Evidence: PersonService receives a PersonRepository through its
constructor. registerCustomer() calls repository.loadCustomers()
and repository.saveCustomers(customers). The listing and lookup
methods also delegate loading to this repository.

PersonRepository acts as an intermediary between PersonService
and the file APIs. PersonService does not handle file paths,
delimiters, readers or writers directly.

### Accessories module

Verdict: correct for separating inventory operations from CSV access.

Evidence:
- AccessoryService receives AccessoryRepository through its constructor
  and loads accessories using repository.loadAll().
- registerController(), registerCable(), registerMemory(),
  updateStock() and restoreStock() delegate saving to
  repository.saveAll(accessories).
- AccessoryRepository.saveAll() and loadAll() perform file access.

AccessoryRepository acts as an intermediary between the service
and the CSV file. AccessoryService does not handle CSV delimiters,
record parsing or Java file APIs directly.

### Promotions module

Verdict: correct for separating catalog operations from file access.

Evidence: PromotionService receives PromotionRepository through
its constructor, loads the catalog using repository.loadAll()
and delegates saving to repository.saveAll(promotions).

PromotionRepository acts as an intermediary between the service
and data/promotions.csv. The service does not handle readers,
writers, delimiters, encoding or record parsing directly.

### Returns module

Assessment: the repository separates file access, but reference
resolution introduces the dependency recorded in V-02.

Evidence:
- ReturnService delegates loading and saving to ReturnRepository.
- ReturnRepository encapsulates readers, writers and record conversion.
- ReturnRepository.fromLine() calls SaleService, ProductService
  and AccessoryService to resolve stored identifiers.

The repository provides an intermediary for persistence operations.
However, its use of service-layer collaborators weakens this boundary.
See V-02 under Low Coupling for the proposed correction and ownership.
This is the same finding, not an additional violation.

### Warranties module

Verdict: correct for separating warranty operations from file access.

Evidence: WarrantyService receives WarrantyRepository through
its constructor, loads warranties using repository.loadAll(catalog, sales)
and delegates saving to repository.saveAll(warranties).

WarrantyRepository acts as an intermediary between the service
and data/warranties.csv. Its loading method receives model collections
for reference resolution without calling service-layer collaborators.

The service's operational methods do not handle record parsing,
delimiters, encoding, readers or writers.

## 9. Protected Variations
### People module

Verdict: correct for isolating file access details and I/O failures.

Evidence:
- PersonRepository keeps file paths, delimiters and file APIs
  inside its persistence methods.
- Its IOException handlers throw PersistenceException and
  preserve the original exception as the cause.
- PersonService accesses stored people through repository methods
  without handling their text representation.

This boundary limits the effect of changes to file handling on
the service. I/O failures are exposed through PersistenceException
instead of IOException.

### Accessories module

Assessment: CSV details are isolated by the repository; subtype
classification remains a variation point in candidate V-01.

Evidence:
- AccessoryRepository encapsulates file paths, separators,
  serialization and record parsing.
- saveAll() and loadAll() wrap IOException in UncheckedIOException,
  preserving the original cause.
- AccessoryService.typeOf() explicitly classifies three subtypes.

Changes to CSV handling can remain within the repository when
its public contract is preserved. Adding an accessory subtype
requires reviewing the classification described in V-01.

The use of UncheckedIOException, IllegalArgumentException and
NoSuchElementException must also be recorded in the exception-usage
review. Replacing them could change externally observed error handling.

### Promotions module

Verdict: correct for isolating discount algorithms during selection
and separating file-format details from the service.

Evidence:
- PromotionService.findBestPromotionFor(Sale) calls
  promotion.calculateDiscount(sale) through the common Promotion type.
- PromotionRepository encapsulates paths, encoding, separators
  and record conversion.

Changes to individual discount algorithms do not require subtype
conditions in the selection loop. Adding a new promotion type
still requires reviewing registration and persistence.

Repository I/O handlers preserve the original cause but expose
generic RuntimeException. Parsing failures are not wrapped there.
These observations must also be covered by the exception-usage review.

### Returns module

Assessment: record-format details are isolated, but service lookup
dependencies remain exposed through the boundary described in V-02.

Evidence:
- ReturnRepository encapsulates paths, separators, encoding and
  record conversion.
- fromLine() resolves identifiers through service collaborators.
- I/O handlers wrap IOException in RuntimeException and preserve
  the original cause.

File-format changes can remain within the repository when its
public contract is preserved. Changes to service lookup contracts
can affect reconstruction; this consequence is recorded in V-02.

The exception-usage review must also cover generic RuntimeException,
unwrapped parsing failures and the existing handling of missing references.

### Warranties module

Assessment: persistence details are isolated, and service operations
use the common Warranty contract.

Evidence:
- WarrantyRepository encapsulates paths, encoding, separators
  and record conversion.
- loadAll() receives product and sale collections for reference resolution.
- WarrantyService queries coverage and costs through Warranty methods.

Changes to file handling can remain within the repository when its
public contract is preserved. Confirm subtype-specific variation
protection with Jaime's model audit.

I/O handlers preserve the cause but expose generic RuntimeException.
Missing references and unknown record types also produce
RuntimeException; parsing failures are not wrapped.
Include these observations in the exception-usage review.