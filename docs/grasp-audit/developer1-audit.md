# GRASP Audit - Developer 1

**Author:** Jaime Granados (Developer 1)
**Audited branch / commit:** `develop` @ `d423eb8`

> Note: Requirement 6 (`feature/exception-handling`) is not merged into `develop` at this
> commit, so the `exceptions` and `validation` packages do not exist yet in the audited code.
> Their rows in the scope are marked as pending. They will be audited, and the commit above
> updated, after the Requirement 6 merge.

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
| Returns (Req. 3) | `Return` | constructor, getters, `calculateRefundAmount`, `discountRatio`, `generateReturnReceipt` |
| Returns (Req. 3) | `Sale` (only this method) | `canBeReturned` (assigned to Developer 1 by the PDF; the git history shows it was committed by Developer 2, see section 12) |
| Warranties (Req. 4) | `Warranty` | constructor, getters, `isActive`, `generateWarrantyCertificate`, `getDurationInMonths` / `getWarrantyType` / `getAdditionalCost` (abstract) |
| Warranties (Req. 4) | `BasicWarranty`, `ExtendedWarranty` | constructors, `getDurationInMonths`, `getWarrantyType`, `getAdditionalCost` |
| Exceptions and validations (Req. 6) | `GameZoneException`, `InvalidDataException`, `ResourceNotFoundException`, `BusinessRuleException`, `PersistenceException` | Pending: not in `develop` yet |
| Exceptions and validations (Req. 6) | `ProductValidator`, `PersonValidator`, `SaleValidator` | Pending: not in `develop` yet |

## 2. Verdict summary

| # | Pattern | Verdict | Violations |
|---|---|---|---|
| 1 | Information Expert | TODO | TODO |
| 2 | Creator | TODO | TODO |
| 3 | Controller | TODO | TODO |
| 4 | Low Coupling | TODO | TODO |
| 5 | High Cohesion | TODO | TODO |
| 6 | Polymorphism | TODO | TODO |
| 7 | Pure Fabrication | TODO | TODO |
| 8 | Indirection | TODO | TODO |
| 9 | Protected Variations | TODO | TODO |

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

## 4. Creator

> Who should create instances of this class?

**Verdict:** correct

- `ProductService.registerVideoGame` and `registerConsole` create `VideoGame` and `Console` objects (lines 42 and 60). `ProductService` keeps the list of products (`products`) and has all the data needed to create them, so it fulfills two Creator criteria: it contains the objects and it has their initializing data.
- `ProductRepository.fromLine` creates `VideoGame` and `Console` (lines 82 and 84) when it loads the file. It is the only class that has the initializing data at that moment (the text line).
- `Accessory` creates its own `ArrayList` for `compatibleConsoleIds` in its constructor, because it is the owner of that list.
- My model classes (`Promotion`, `Return`, `Warranty` hierarchies) do not create other domain objects. They are created by `PromotionService`, `ReturnService` and `WarrantyService` (Developer 2), which keep their lists.

## 5. Controller

> Who receives and coordinates the system events?

**Verdict:** correct

My code does not receive events from the user directly; `ConsoleUI` (Technical Leader) does. For the product use cases, `ProductService` is the first object after the UI that receives the system operations: `ConsoleUI` calls `productService.registerVideoGame` (`ConsoleUI` line 156), `registerConsole` (line 175), `listAll` (line 186) and `findById` (line 340). `ProductService` coordinates the operation (checks duplicate ids, creates the object, asks the repository to save) and the UI never talks to `ProductRepository`. This is a use-case controller.
