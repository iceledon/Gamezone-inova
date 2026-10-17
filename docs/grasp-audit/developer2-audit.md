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

## 3. Controller
### People module

Verdict: correct for customer registration coordination.

Evidence: PersonService.registerCustomer(Customer).

The method receives the customer registration request, delegates data
and uniqueness checks to PersonValidator, loads existing customers,
adds the customer and delegates saving to PersonRepository.

It coordinates the operation without reading console input, displaying
results or handling file access directly.

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

## 7. Pure Fabrication
### People module

Verdict: correct for separating file persistence from person entities.

Evidence: PersonRepository.saveCustomers(), loadCustomers(),
saveSellers() and loadSellers().

PersonRepository is a technical class that groups file persistence
operations. Assigning these operations to the repository keeps
Person, Customer and Seller independent of file paths, delimiters
and Java file APIs.

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