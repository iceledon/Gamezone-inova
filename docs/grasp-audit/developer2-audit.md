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
