# Integration Analysis

This document describes adjustments A1-A7 in the integrated system.
It records the implemented behavior and distinguishes code inspection
from functional testing.

## A1 - Accessory category discount

**Problem:** Category promotions did not recognize accessories.

**Cause:** Category matching only handled videogames and consoles.

**Solution:** `CategoryDiscount` now recognizes `ACCESSORY` through
`instanceof Accessory`. `PromotionService.registerCategoryDiscount`
accepts `VIDEOGAME`, `CONSOLE` and `ACCESSORY`, rejecting other values.

The console prompt includes the accessory category, and the sample
promotion data includes an accessory discount.

The discount is calculated from eligible item prices only.
Warranty costs are excluded.

The sample promotion dated 2026-09-21 through 2026-09-26 is active only
within that period. A current-date demonstration requires a promotion
whose validity includes the demonstration date.

## A2 - Warranty circular dependency

**Problem:** `WarrantyService` depended on `SaleService`, while
`SaleService` needed `WarrantyService` to assign warranties.

**Cause:** Loading warranty references required sales obtained through
another service, creating a circular service dependency.

**Solution:** The `WarrantyService` constructor receives
`SaleRepository`, `ProductService` and `PersonService`. It loads sales
directly through the repository and uses them to reconstruct warranties.

`Main` creates `WarrantyService` before `SaleService` and connects the
warranty service through `setWarrantyService`.

The setter remains, but `WarrantyService` no longer depends on
`SaleService`.

The constructor currently passes the product catalog to the sales
loader. Reloading warranties associated with mixed product/accessory
sales should therefore be included in integration verification.

## A3 - Unified sale registration order

**Problem:** Sale registration assigned warranties before selecting
and applying the promotion.

**Cause:** The warranty and promotion blocks ran in the opposite order
to the required flow.

**Solution:** `SaleService.registerSale` follows this sequence:

1. Validate the item list, customer and seller.
2. Resolve products and accessories and validate requested stock.
3. Create the sale.
4. Select and assign the best active promotion.
5. Assign console warranties and add extended warranty costs.
6. Update inventory.
7. Save the sale.

The final amount is:

```text
sale total = item subtotal - promotion discount + warranty costs
```

Promotion implementations use item prices or the item subtotal.
Extended warranty costs are excluded from the discount base.

## A4 - Accessory stock restoration and return loading

**Problem:** Returning an accessory did not restore its stock through
the accessory inventory service.

**Cause:** Return processing and reference loading did not account
for the separate accessory catalog.

**Solution:** `ReturnService` receives `AccessoryService`.
For each returned item, it calls:

- `AccessoryService.restoreStock(id, 1)` for accessories.
- `ProductService.restoreStock(id, 1)` for other products.

`ReturnRepository` also receives `AccessoryService`. When loading a
return, it searches the product catalog first and then the accessory
catalog to resolve each stored item identifier.

## A5 - Proportional refund for discounted sales

**Problem:** A return refunded the full list price even when the
original sale included a promotion discount.

**Cause:** The refund calculation ignored the original sale's discount.

**Solution:** `Return.discountRatio()` divides the original discount
amount by the original item subtotal. If the subtotal is zero or
negative, it returns zero.

Each returned item is refunded using:

```text
discount ratio = original discount / original item subtotal
item refund = item price * (1 - discount ratio)
```

This proportionally allocates the sale-wide discount across all items.
It does not reconstruct which individual items qualified for a
category promotion.

The return receipt displays each item's list price, allocated
discount and refund.

After A7, the total also includes the warranty refund:

```text
total refund = sum of item refunds + warranty refund
```

## A6 - Separate monthly sales, returns and net balance

The required separation was already implemented:

- `calculateMonthlySales(month, year)` sums `Sale.calculateTotal()`
  for sales dated in the selected month and year.
- `calculateMonthlyReturns(month, year)` sums refund amounts
  for returns dated in the selected month and year.
- `generateMonthlyBalance(month, year)` subtracts returns from sales.

Sales are counted by sale date; returns are counted by return date.

[Monthly balance verification](monthly-balance-verification.md)
records that `ConsoleUI.showMonthlyBalance()` displays sales, returns
and net balance separately.

A6 required verification rather than a functional code change.

## A7 - Warranty cancellation and refund on console return

**Problem:** Returning a console left its warranties registered and
did not include their refundable costs in the return.

**Cause:** Return registration did not coordinate with the warranty
service.

**Solution:** Before constructing the return, `ReturnService` calls
`WarrantyService.cancelWarranties(productId, saleId)` for returned
consoles.

The warranty service:

1. Finds warranties matching both product and sale identifiers.
2. Sums their additional costs.
3. Removes the matching warranties from its collection.
4. Saves the remaining warranties.
5. Returns the refundable amount.

Cancellation removes matching records; it does not retain a separate
cancelled status. The method does not filter matches by current validity.

`Return` stores `warrantyRefund`, adds it to the proportional item
refunds and displays it in the receipt when positive.

`ReturnRepository` writes seven fields:

```text
id;date;saleId;productIds;reason;refundAmount;warrantyRefund
```

Product identifiers within `productIds` are separated by commas.

When loading an older record without the seventh field,
`warrantyRefund` defaults to zero. If that field exists, the current
loader expects a numeric value.

The loader reconstructs the return and recalculates its total.
It does not restore the stored `refundAmount` field directly.

## Architecture observations

The project retains the `ui`, `service`, `persistence` and `model`
packages.

Services coordinate promotions, sales, inventory, returns and
warranties. Domain calculations remain in model classes, while
file operations remain in repositories.

There is an exception to the intended dependency direction:
`ReturnRepository` imports and uses `SaleService`, `ProductService`
and `AccessoryService` to resolve stored identifiers.

The current implementation therefore includes dependencies from
persistence back to service. Diagrams must show this accurately;
strict one-way dependencies would require a separate refactoring.

## Confirmed persistence limitations

`SaleRepository` stores `extraCost` but does not serialize or restore
`discountAmount` or `appliedPromotionName`. Reloaded sales therefore
lose their promotion data, affecting sale totals and return calculations.

`WarrantyService` loads sales using only `ProductService.listAll()`.
`SaleRepository` throws an exception when a stored item identifier is
missing from that list. Saved sales containing accessories can therefore
prevent startup.

`ReturnRepository` reconstructs returns and recalculates refunds instead
of restoring the saved `refundAmount`, so lost sale discounts also affect
reloaded return amounts.

These findings come from code inspection. Fixes and restart verification
are needed before the final integrated release.

## Verification scope

This analysis is based on inspection of the integrated implementation.
It does not, by itself, establish that every end-to-end scenario passes.

Final integration verification should cover:

1. A sale containing a console and an accessory, with an active
   promotion and an extended warranty.
2. The subtotal, promotion discount, warranty cost and final total.
3. Accessory stock restoration after a return.
4. Console warranty removal and inclusion of its refundable cost.
5. Separate monthly sales, returns and net balance.
6. Application restart and reloading of sales, warranties and returns,
   including mixed sales and discounted refunds.