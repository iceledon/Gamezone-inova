# GRASP Audit - Developer 1

**Author:** Jaime Granados (Developer 1)
**Audited branch / commit:** `develop` @ `701255d4209e070146c32ee5a663b08d3b4b0bb4`
(team reference commit, merge of PR #33)

### Baseline update

The first version of this audit used `develop` @ `d423eb8`, before Requirement 6 was merged.
The differences between `d423eb8` and `701255d` were reviewed:

- `Product`, `VideoGame`, `Console`, `Accessory` and its subclasses, `Promotion` and its
  subclasses, `Return`, `Warranty` and its subclasses, `Sale`, `AccessoryService` and
  `PromotionService` did not change. The evidence of V-01, V-02 and V-03 is still valid.
- `ProductService`, `ProductRepository`, `SaleService`, `ReturnService` and `ConsoleUI`
  changed (typed exceptions and validators). The line numbers cited from these classes were
  updated, and the Low Coupling justification was rewritten (section 6).
- The `exceptions` and `validation` packages were added by Requirement 6.

## 1. Scope

Classes and methods audited, organized by module and requirement (section 4 of Requirement 7).

| Module (requirement) | Class | Methods audited |
|---|---|---|
| Products (Workshop 1) | `Product` | constructor, getters/setters, `decreaseStock`, `getDescription` (abstract) |
| Products (Workshop 1) | `VideoGame` | constructor, getters, `getDescription` |
| Products (Workshop 1) | `Console` | constructor, getters, `getDescription` |
| Products (Workshop 1) | `ProductRepository` | `save`, `load`, `toLine`, `fromLine` |
| Products (Workshop 1) | `ProductService` | constructor, `registerVideoGame`, `registerConsole`, `rejectIfIdExists`, `find`, `listAll`, `findById`, `updateStock` (`restoreStock` excluded: Technical Leader) |
| Accessories (Req. 1) | `Accessory` | constructor, `getCompatibleConsoleIds`, `addCompatibleConsole`, `removeCompatibleConsole`, `isCompatibleWith` |
| Accessories (Req. 1) | `Cable`, `Controller`, `Memory` | constructors, getters/setters, `getDescription` |
| Promotions (Req. 2) | `Promotion` | constructor, getters/setters, `isActive`, `calculateDiscount` (abstract) |
| Promotions (Req. 2) | `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` | constructors, getters/setters, `calculateDiscount`, `CategoryDiscount.matchesTargetCategory` |
| Promotions (Req. 2) | `Sale` (only these members) | `getAppliedPromotionName` / `setAppliedPromotionName`, `getDiscountAmount` / `setDiscountAmount`, `generateReceipt` (added by Developer 1, commits `09d5c3e` and `0575ed7`). `calculateSubtotal` and `calculateTotal` are shared with the Technical Leader: see "Shared assessments" in section 3 |
| Returns (Req. 3) | `Return` | constructor, getters, `calculateRefundAmount`, `discountRatio`, `generateReturnReceipt` |
| Returns (Req. 3) | `Sale` (only this method) | `canBeReturned` (assigned to Developer 1 by the PDF; the git history shows it was committed by Developer 2, see section 12) |
| Warranties (Req. 4) | `Warranty` | constructor, getters, `isActive`, `generateWarrantyCertificate`, `getDurationInMonths` / `getWarrantyType` / `getAdditionalCost` (abstract) |
| Warranties (Req. 4) | `BasicWarranty`, `ExtendedWarranty` | constructors, `getDurationInMonths`, `getWarrantyType`, `getAdditionalCost` |
| Exceptions and validations (Req. 6) | `GameZoneException` | constructors, `getErrorCode` |
| Exceptions and validations (Req. 6) | `InvalidDataException`, `ResourceNotFoundException`, `BusinessRuleException`, `PersistenceException` | constructors |
| Exceptions and validations (Req. 6) | `ProductValidator` | `validateProductData`, `validateStockAvailability`, `validateProductExists`, `requireText` |
| Exceptions and validations (Req. 6) | `PersonValidator` | `validatePersonData`, `validatePersonExists`, `validateUniquePerson`, `hasValidEmailFormat`, `requireText` |
| Exceptions and validations (Req. 6) | `SaleValidator` | `validateSaleData`, `validateSaleExists` (`validateSaleProducts` was added by Developer 2 in PR #33 and is assessed by Developer 2) |

How the other services use these exceptions and validators is assessed by their owners
(`docs/grasp-audit/developer2-audit.md`, section 10, and `docs/grasp-audit/leader-audit.md`).

## 2. Verdict summary

| # | Pattern | Verdict | Violations |
|---|---|---|---|
| 1 | Information Expert | violation | V-01 |
| 2 | Creator | correct | - |
| 3 | Controller | correct | - |
| 4 | Low Coupling | correct | - (the coupling problem in `Return` is registered once, as V-01) |
| 5 | High Cohesion | correct | - |
| 6 | Polymorphism | violation | V-02 |
| 7 | Pure Fabrication | correct | - |
| 8 | Indirection | correct | - |
| 9 | Protected Variations | violation | V-03 |

Verdict values: `violation`, `correct`, `not applicable`.

## 3. Information Expert

> Who has the information needed to fulfill this responsibility?

**Verdict:** violation

Where it is correct:

- `Product.decreaseStock(int)` changes `quantity`, and `Product` is the class that owns `quantity`.
- `Accessory.isCompatibleWith(String)`, `addCompatibleConsole` and `removeCompatibleConsole` work on `compatibleConsoleIds`, which only `Accessory` holds.
- `Promotion.isActive(LocalDate)` and `Warranty.isActive(LocalDate)` use `startDate` and `endDate`, which each class owns.
- `Warranty.generateWarrantyCertificate()` and `Return.generateReturnReceipt()` build their text from their own fields.
- `Sale.canBeReturned()` uses the sale `date`, which `Sale` owns.
- `Sale` stores `appliedPromotionName` and `discountAmount`: the discount belongs to one sale, so the sale keeps it. `Promotion.calculateDiscount(Sale)` calculates the amount and `SaleService` stores the result in the sale.
- `Sale.generateReceipt()` (lines 178-198) builds the receipt from data that the sale owns or reaches through its own associations: `id`, `date`, `customer`, `seller`, `products`, `extraCost`, `discountAmount`, `appliedPromotionName`, and its own `calculateSubtotal()` and `calculateTotal()`.
- `InvalidDataException` and `ResourceNotFoundException` build their own Spanish message from the data they receive (field and problem, resource type and id), so the message format is defined in one place.

Considered but not a violation: `ProductValidator.validateStockAvailability(Product, int)` (lines 43-49) reads `product.getQuantity()` outside `Product` to decide if there is enough stock. It was not registered as a violation because:

- It only reads the stock and compares it; it does not change `Product`. The responsibility it has is "apply the business rule and report the error", not "manage the quantity".
- Requirement 6 asks to keep the validation rules in validator classes, so services do not repeat them and the model does not create user messages. The team decided the same in Workshop 1: `Product.decreaseStock` does not validate, and the stock rule lives outside the model.
- This is different from `ProductService.restoreStock` (section 12): there the service calculates the new quantity and writes it back to `Product`, which changes the state of the object outside its owner.

### V-01

| Field | Content |
|---|---|
| ID | V-01 |
| Pattern | Information Expert (also affects Low Coupling) |
| Module | Returns (Req. 3) |
| Evidence | `Return.discountRatio()`, lines 117-123: `double subtotal = originalSale.calculateSubtotal(); if (subtotal <= 0) { return 0.0; } return originalSale.getDiscountAmount() / subtotal;` It is used by `calculateRefundAmount()` (line 102) and `generateReturnReceipt()` (line 132). |
| Explanation | The discount ratio of a sale ("what fraction of the subtotal was discounted") only needs data that belongs to `Sale`: its subtotal and its `discountAmount`. `Return` asks `Sale` for two values and does the math itself, so the responsibility is in the class that does not own the information. |
| Consequence | `Return` depends on how `Sale` stores and calculates its discount. If the way the discount is applied changes (for example, the discount is limited to some products, or the extended warranty cost becomes part of the discount base), `Return` must change too, even though the rule belongs to `Sale`. Any other class that needs the same ratio would have to copy this code. |
| Proposed solution | Move the calculation to `Sale` as a public method `calculateDiscountRatio()` (same logic, same result), and make `Return` call `originalSale.calculateDiscountRatio()`. Pattern applied: Information Expert, which also lowers the coupling of `Return` to the internal data of `Sale`. |
| Other member's code | Yes: `Sale` is the Technical Leader's class. The discount fields in `Sale` were added by Developer 1 in Req. 2 (git history), so the Technical Leader decides in the consolidation who adds the new method. |

### Shared assessments

`Sale.calculateSubtotal()` (lines 138-144) and `Sale.calculateTotal()` (lines 152-154) have
contributions from the Technical Leader (commit `f6a7832`) and from Developer 1 (commit
`a77d068`, which made the promotions use the subtotal). To avoid duplicate findings, the primary
assessment of these two methods is the one in `docs/grasp-audit/leader-audit.md`
(section 1, "Sale calculations", verdict: correct). This audit does not assess them again and
agrees with that verdict.

`Sale.generateReceipt()` and the discount fields of `Sale` were added by Developer 1, so this
audit is their primary assessment (sections 3 and 7).

Status: allocation proposed to the Technical Leader in PR #31, pending her confirmation.

## 4. Creator

> Who should create instances of this class?

**Verdict:** correct

- `ProductService.registerVideoGame` and `registerConsole` create `VideoGame` and `Console` objects (lines 57 and 97). `ProductService` keeps the list of products (`products`) and has all the data needed to create them, so it fulfills two Creator criteria: it contains the objects and it has their initializing data.
- `ProductRepository.fromLine` creates `VideoGame` and `Console` (lines 97 and 99) when it loads the file. It is the only class that has the initializing data at that moment (the text line).
- `Accessory` creates its own `ArrayList` for `compatibleConsoleIds` in its constructor, because it is the owner of that list.
- My model classes (`Promotion`, `Return`, `Warranty` hierarchies) do not create other domain objects. They are created by `PromotionService`, `ReturnService` and `WarrantyService` (Developer 2), which keep their lists.
- The validators create `InvalidDataException`, `ResourceNotFoundException` and `BusinessRuleException` (for example, `ProductValidator` lines 29, 45 and 60). The validator is the class that detects the problem and has the initializing data (the field, the id, the available and requested stock). `ProductRepository` creates `PersistenceException` (lines 36, 60, 63 and 101) because it is the class that detects the failure: the file cannot be read or written, or a line has corrupt data or an unknown product type. When there is an original exception (for example, an `IOException`), it is kept as the cause.

## 5. Controller

> Who receives and coordinates the system events?

**Verdict:** correct

My code does not receive events from the user directly; `ConsoleUI` (Technical Leader) does. For the product use cases, `ProductService` is the first object after the UI that receives the system operations: `ConsoleUI` calls `productService.registerVideoGame` (`ConsoleUI` line 168), `registerConsole` (line 187), `listAll` (line 198) and `findById` (line 369). `ProductService` coordinates the operation (checks duplicate ids, creates the object, asks the repository to save) and the UI never talks to `ProductRepository`. This is a use-case controller.

## 6. Low Coupling

> How are dependencies between classes reduced?

**Verdict:** correct

- `ProductService` depends on `ProductRepository`, the model classes, `ProductValidator` and the exception classes. The validator and the exceptions are small, stable classes without state that only depend on the model, so they do not tie `ProductService` to other modules. Other services (`SaleService`, `ReturnService`, `WarrantyService`) use the product catalog through `ProductService` (`listAll`, `findById`, `updateStock`), not through the repository or the file.
- `ProductRepository` depends only on the model classes it reads and writes and on `PersistenceException`.
- The validators depend only on the model classes they check and on the exception classes. The exception classes do not depend on any other class of the project. `GameZoneException` extends `RuntimeException`, so services and repositories do not need `throws` clauses, and a new exception does not force changes in the method signatures of other layers.
- The services call the validators through static methods, so they depend on the concrete validator class. This is accepted here because the validators have no state and their rules are stable; whether they should be behind an abstraction is a Dependency Inversion question for Requirement 8 (SOLID), not a GRASP violation.
- `Promotion.calculateDiscount(Sale)` and `Warranty` (fields `product` and `sale`) depend on `Sale` and `Product` only because they need them to do their job (calculate a discount, print a certificate).
- The only unnecessary coupling found (`Return` reading the internal discount data of `Sale`) is the same code fragment as V-01. It is registered once, under Information Expert, because fixing that pattern also removes the coupling.

## 7. High Cohesion

> How are classes kept focused and understandable?

**Verdict:** correct

- `ProductService` has only product use cases: register, list, find and update stock.
- `ProductRepository` has only persistence of products: write and read `data/products.txt` and convert a product to a line and back.
- Each model class keeps its own data and the rules about that data: `Accessory` (compatible consoles), `Promotion` (validity dates and discount), `Warranty` (coverage period and cost), `Return` (refund).
- Each validator has only the rules of one entity (`ProductValidator` for products, `PersonValidator` for people, `SaleValidator` for sales), and each exception class represents one category of error (invalid data, resource not found, business rule, persistence).
- Noted, not a GRASP violation: the private method `requireText` is the same in `ProductValidator` (lines 64-68) and `PersonValidator` (lines 71-75). It is a small duplication of code, not a responsibility in the wrong class. Also, `ProductService` checks duplicate ids in its own `rejectIfIdExists` (lines 115-121), while `PersonService` uses `PersonValidator.validateUniquePerson`. Both work, but the style is not the same.
- `Return.generateReturnReceipt()`, `Warranty.generateWarrantyCertificate()` and `Sale.generateReceipt()` build a text in the model. This was considered: the text only uses the object's own data and the requirements asked for these methods in these classes (for `Sale.generateReceipt()`, see `docs/promotion-analysis.md`, section 4), so they stay with their information expert. The console only prints the returned text (`ConsoleUI` line 436 for the sale receipt). If the system needed other output formats (for example, a PDF receipt), this would become a cohesion issue and the formatting could move to a separate class; that change is not in the current requirements.

## 8. Polymorphism

> How are behavior variants handled without conditionals on the type?

**Verdict:** violation

Where it is correct:

- `Product.getDescription()` is abstract and each subclass (`VideoGame`, `Console`, `Cable`, `Controller`, `Memory`) gives its own version. `ConsoleUI` prints any product with `product.getDescription()` without asking its type.
- `Promotion.calculateDiscount(Sale)` is abstract; each type of promotion calculates its own discount, and `SaleService` calls it without knowing the type.
- `Warranty` declares `getDurationInMonths()`, `getWarrantyType()` and `getAdditionalCost()` as abstract, so `BasicWarranty` and `ExtendedWarranty` change the behavior without any `if` on the type.
- The exception hierarchy: every custom exception extends `GameZoneException`, and each subclass fixes its own error code and message format in its constructor. The services only throw the right subtype; the code that handles the error (`ConsoleUI.showError`, Technical Leader) catches each subtype in its own `catch` block, which is the normal Java way to select behavior by exception type. Observation: `getErrorCode()` is not used by any class at the audited commit, because the UI selects the message by exception type.

Considered but not a violation: `ProductRepository.toLine` and `fromLine` use `instanceof` and the `"VIDEOGAME"` / `"CONSOLE"` type field. The text format of the file is a persistence detail; moving it to `Product` would put file format knowledge in the model layer. The repository is a Pure Fabrication created to keep this outside the domain, so the type check stays there.

### V-02

| Field | Content |
|---|---|
| ID | V-02 |
| Pattern | Polymorphism |
| Module | Promotions (Req. 2) |
| Evidence | `CategoryDiscount.matchesTargetCategory(Product)`, lines 79-90: `if ("VIDEOGAME".equals(targetCategory)) { return product instanceof VideoGame; } if ("CONSOLE".equals(targetCategory)) { return product instanceof Console; } if ("ACCESSORY".equals(targetCategory)) { return product instanceof Accessory; } return false;` |
| Explanation | The category of a product is a variation that depends on the product type, but it is decided with a chain of `if` + `instanceof` in another class. Each product subclass should answer "what is your category" itself. |
| Consequence | When the store adds a new product type (the owner plans to grow the catalog), `CategoryDiscount` must be edited to add one more `if`. If someone forgets, products of the new type are never discounted and nothing warns about it. |
| Proposed solution | Add a polymorphic method `getCategory()` to `Product` (abstract), implemented in `VideoGame` (`"VIDEOGAME"`), `Console` (`"CONSOLE"`) and `Accessory` (`"ACCESSORY"`, inherited by `Cable`, `Controller`, `Memory`). `matchesTargetCategory` becomes `targetCategory.equals(product.getCategory())`. Same results for every existing category. Pattern applied: Polymorphism (it also protects `CategoryDiscount` from new product types, see section 11). |
| Other member's code | No. All the classes changed (`Product`, `VideoGame`, `Console`, `Accessory`, `CategoryDiscount`) are Developer 1's. |

## 9. Pure Fabrication

> Where does a responsibility go when it does not belong to any domain class?

**Verdict:** correct

`ProductRepository` is a Pure Fabrication: a "product repository" does not exist in the store's real world. It was created to hold the file access (`Files.write`, `Files.readAllLines`) and the text format, so that `Product`, `VideoGame` and `Console` do not have any file logic and stay cohesive. `ProductService` is also an invented class that groups the product use cases.

`ProductValidator`, `PersonValidator` and `SaleValidator` are also Pure Fabrications. They hold the validation rules so that the model classes do not create user messages and the services do not repeat the same checks. They have a private constructor and only static methods, because they do not keep any state.

## 10. Indirection

> How are two components that must collaborate decoupled?

**Verdict:** correct

`ProductService` is an intermediate object between `ConsoleUI` and `ProductRepository`. The UI never knows that products are saved in a text file. Other modules also go through `ProductService` to use products: `SaleService.updateStockOf` calls `productService.updateStock` (`SaleService` line 283) and `ReturnService` calls `productService.restoreStock` (`ReturnService` line 93), instead of reading or writing `data/products.txt` themselves.

## 11. Protected Variations

> How is the system protected from foreseeable changes?

**Verdict:** violation

Where it is correct:

- The abstract classes `Product`, `Promotion` and `Warranty` are stable interfaces: a new kind of promotion or warranty is added as a new subclass, and `SaleService` and `ConsoleUI` keep working with the base type.
- `ProductRepository` hides the file format behind `save` and `load`; if the format changes, only the repository changes.
- `PersistenceException` protects the upper layers from file-access details: `ProductRepository` catches the `IOException` and throws `PersistenceException` with the original cause (lines 35-36 and 59-63). The UI only knows "the data could not be read or written", not the `java.io` exception, so changing how data is stored does not change how the UI handles the error.
- The message format of invalid data and missing resources is defined once, in the constructors of `InvalidDataException` and `ResourceNotFoundException`. Changing that format changes one class, not every place that throws the error.
- Limit of this protection: it only works in the modules that use the hierarchy. `docs/grasp-audit/developer2-audit.md` (sections 9 and 10) already records that the accessory, promotion, return and warranty modules use other exception types. That observation is not repeated here.
- The variation "new product type" in `CategoryDiscount` is covered by V-02 (same code, registered once).

### V-03

| Field | Content |
|---|---|
| ID | V-03 |
| Pattern | Protected Variations |
| Module | Returns (Req. 3) |
| Evidence | `Sale.canBeReturned()`, lines 162-169: `LocalDate limitDate = this.date.plusDays(30); LocalDate today = LocalDate.now(); if (today.isAfter(limitDate)) { return false; } return true;` |
| Explanation | Two points that will change are written directly inside the method: the return period (the magic number `30`) and the current date (`LocalDate.now()`, read from the system clock). Nothing protects the rest of the code from those changes. |
| Consequence | If the store changes its return policy (for example, 15 or 45 days), the number must be found inside the method, and the message "Ya paso el plazo de 30 dias" in `ReturnService` is a second place with the same value. Because the method reads the system clock, it is not possible to check the rule for a given date (for example, to show in the oral defense that a 31-day-old sale is rejected) without changing the computer date. |
| Proposed solution | Keep the period in a named constant (`RETURN_PERIOD_DAYS = 30`) and add `canBeReturned(LocalDate date)` with the rule; the current `canBeReturned()` calls it with `LocalDate.now()`, so `ReturnService` and the user see exactly the same behavior. Pattern applied: Protected Variations. |
| Other member's code | Yes, possibly: `Sale` is the Technical Leader's class, and the git history shows `canBeReturned` was committed by Developer 2. The text "30 dias" in `ReturnService` is Developer 2's and is not changed (the user message must stay the same). The Technical Leader decides the owner in the consolidation. |

## 12. Findings reported to the Technical Leader

Problems found during this audit whose origin is in another member's code (not audited here, sent to the consolidation).

| Class / method | Owner | Short description |
|---|---|---|
| `Sale.canBeReturned` | Developer 2 (git history) / Developer 1 (PDF table) | The PDF assigns this method to Developer 1, but commit `45c8cf2` ("feat(model): add canBeReturned method to Sale") is by Developer 2. Audited here (V-03) as the PDF says; please confirm the owner of the correction. |
| `ProductService.restoreStock` (lines 179-185) | Technical Leader | Information Expert: it does `product.setQuantity(product.getQuantity() + amount)` instead of asking `Product` to increase its own stock (the opposite of `Product.decreaseStock`). Same finding as L-V01 in `leader-audit.md`: see "Boundary finding L-V01" below. |
| `AccessoryService.restoreStock` (lines 170-174) | Developer 2 (commit `b9e12fb`) | Same problem as above (line 172: `accessory.setQuantity(accessory.getQuantity() + amount)`). Proposed to be part of the same consolidated finding as L-V01. |
| `SaleService.registerSale` (line 143), `ReturnService.registerReturn` (lines 81 and 90), `ConsoleUI.registerSale` (line 337) | Technical Leader / Developer 2 | Polymorphism: `instanceof Console` / `instanceof Accessory` checks on products. If V-02 adds `Product.getCategory()`, these places could use it; the decision is theirs. |
| `PromotionService.isValidTargetCategory` (lines 160-163) | Developer 2 | Repeats the category strings `"VIDEOGAME"`, `"CONSOLE"`, `"ACCESSORY"`. Related to V-02. |
| `Return` (`warrantyRefund` field, constructor parameter and receipt line) | Developer 2 (git history) | Added by Developer 2 in the warranty-refund integration; the JavaDoc of `Return` is in Spanish, which goes against the "comments in English" rule. Not a GRASP issue; noted for the Phase 2 change in `Return`. |
| `SaleValidator.validateSaleProducts` (lines 21-41) | Developer 2 (PR #33) | The JavaDoc is in Spanish and there is no blank line before `validateSaleData`. Not a GRASP issue; noted so the style can be fixed if the class is changed in Phase 2. |

### Boundary finding L-V01 (Technical Leader and Developer 1)

The Technical Leader registered L-V01 (Information Expert) in `docs/grasp-audit/leader-audit.md`
for `ProductService.restoreStock`. Developer 1 confirms the finding: it describes the same
problem as the `ProductService.restoreStock` row above, so it must be consolidated as **one**
finding, not two.

- **Product side (Developer 1):** `Product` owns `quantity` and already has
  `decreaseStock(int)`. The missing operation is `increaseStock(int amount)`, which only adds
  the amount, with the same style as `decreaseStock` (no new validation and no new messages).
  `Accessory` extends `Product`, so `Cable`, `Controller` and `Memory` inherit it.
- **Proposed scope of the consolidated finding:** `ProductService.restoreStock` (Technical
  Leader) and `AccessoryService.restoreStock` (Developer 2). Both repeat the same calculation and
  can delegate to `increaseStock`.
- **Order for Phase 2:** first Developer 1 adds `Product.increaseStock(int)` (producer), then
  each owner changes their `restoreStock` to call it (consumers). The behavior seen by the user
  must not change.
- The definitive ID is assigned in the consolidated audit.

Status: confirmed by Developer 1; inclusion of `AccessoryService.restoreStock` pending agreement
with the Technical Leader and Developer 2.
