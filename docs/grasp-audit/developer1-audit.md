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
