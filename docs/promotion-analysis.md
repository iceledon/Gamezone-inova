# Promotion Analysis

## 1. Class hierarchy and polymorphic discount calculation

`Promotion` is an abstract class containing the attributes shared by all
promotions: `id`, `name`, `startDate` and `endDate`. It implements
`isActive(LocalDate)` and declares `calculateDiscount(Sale)` as abstract.

Three subclasses implement different calculation rules:

- `PercentageDiscount` applies a percentage to the complete item subtotal.
- `CategoryDiscount` applies a percentage only to products matching
  `VIDEOGAME`, `CONSOLE` or `ACCESSORY`.
- `BulkPurchaseDiscount` applies a percentage to the item subtotal when
  the number of item entries reaches `minimumQuantity`. Otherwise,
  it returns zero.

Each subclass overrides `calculateDiscount(Sale)`. When
`PromotionService` calls this method through a `Promotion` reference,
Java executes the implementation of the concrete subclass.

This is runtime polymorphism through method overriding. The service
does not need to identify each promotion type to calculate its discount.
The type checks inside `CategoryDiscount` identify product categories,
not promotion types.

## 2. Declaring calculateDiscount in the base class

The method is declared as:

```java
public abstract double calculateDiscount(Sale sale);
```

It has no body in `Promotion`. Every concrete subclass must implement it.

This declaration provides a common contract: code working with a
`Promotion` reference can calculate a discount without knowing the
concrete promotion type.

A new promotion subclass can implement its own calculation without
requiring changes to the selection loop in `PromotionService`.

## 3. Where the best promotion is selected

The selection is implemented in
`PromotionService.findBestPromotionFor(Sale)`.

The method iterates over active promotions, calculates the monetary
discount offered by each one and selects the highest positive discount.
Promotions are not combined.

If no promotion offers a positive discount, the method returns `null`.
When two promotions offer the same positive discount, the first one
encountered remains selected because the comparison uses `>`.

This responsibility belongs in the service layer because it coordinates
registered promotions and applies the selection policy.

`Sale` calculates its own amounts and stores the selected promotion's
name and discount. `ConsoleUI` handles interaction with the user.

## 4. Changes to Sale and generateReceipt

`Sale` stores `appliedPromotionName` and `discountAmount`, with their
getters and setters. It also stores `extraCost` for extended warranties.

`calculateSubtotal()` adds the prices of all item entries in the sale,
including accessories. It excludes warranty costs and discounts.

`calculateTotal()` uses this formula:

```text
total = item subtotal + extended warranty costs - promotion discount
```

`generateReceipt()` displays:

- The customer, seller and sold items.
- The item subtotal.
- The extended warranty cost when it is positive.
- The promotion name and discount when the discount is positive.
- The final total.

Without an applicable promotion, `discountAmount` remains zero.
The total is therefore the item subtotal plus any warranty costs.

Adjustment A3 places promotion selection and discount assignment
immediately after creating the sale, before assigning warranties and
adding their extra costs.

All three promotion implementations calculate their discount from
item prices, excluding warranty costs.

## 5. Where promotion validity is checked

`Promotion.isActive(LocalDate)` checks whether a supplied date is
within the promotion's validity period. Both `startDate` and `endDate`
are included.

`PromotionService.listActivePromotions()` obtains the current date
with `LocalDate.now()` and calls `isActive` for each promotion.

`findBestPromotionFor(Sale)` compares only the promotions returned
by that filter.

The current implementation checks validity against the current system
date, rather than the date stored in the supplied sale.

The model checks the validity of an individual promotion. The service
filters the collection and selects the best applicable discount.
