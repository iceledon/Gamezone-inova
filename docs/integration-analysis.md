# Integration Analysis

This document describes adjustments A1-A7 and the subsequent persistence
corrections in the integrated system.

It records the implemented behavior and distinguishes code inspection
and compilation from end-to-end testing.

## A1 - Accessory category discount

**Problem:** Category promotions did not recognize accessories.

**Cause:** Category matching only handled videogames and consoles.

**Solution:** `CategoryDiscount` recognizes `ACCESSORY` through
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

**Solution:** `WarrantyService` loads sales directly through
`SaleRepository`, rather than obtaining them from `SaleService`.

After the additional persistence correction, its constructor receives:

- `WarrantyRepository`
- `SaleRepository`
- `ProductService`
- `PersonService`
- `AccessoryService`

It combines products and accessories into one catalog, loads sales using
that catalog and the registered people, and then reconstructs warranties.

`Main` creates `WarrantyService` before `SaleService` and connects the
warranty service through `setWarrantyService`.

The setter remains, but `WarrantyService` no longer depends on
`SaleService`.

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

The additional persistence correction stores the sale discount so it
can be restored when loading newly saved sales.

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

## Additional persistence corrections

### Preserve promotion data in saved sales

**Problem:** Reloaded sales lost their promotion name and discount.

**Cause:** `SaleRepository` saved warranty costs but omitted
`discountAmount` and `appliedPromotionName`.

**Correction:** New sale records include both promotion fields:

```text
id;date;customerId;sellerId;productIds;extraCost;discountAmount;appliedPromotionName
```

When reading a record, `SaleRepository` restores the discount and
promotion name if the corresponding fields are present and nonblank.

A missing promotion name is written as an empty field.

Older records remain readable. Missing discount amounts default to
zero, and missing promotion names remain unset.

Discounts that were never saved in older records cannot be recovered
automatically. The loader does not recalculate historical promotions.

### Load mixed sales during warranty initialization

**Problem:** A saved sale containing an accessory could prevent startup.

**Cause:** `WarrantyService` supplied only the product catalog to
`SaleRepository`, whose loader rejects unresolved item identifiers.

**Correction:** The warranty service constructor now combines:

- `ProductService.listAll()`
- `AccessoryService.listAllAccessories()`

It uses this combined catalog to load sales and warranty references.

`Main` supplies `AccessoryService` as the new constructor argument.

This correction retains the direct dependency on `SaleRepository`
and does not reintroduce a dependency on `SaleService`.

## Architecture observations

The project retains the `ui`, `service`, `persistence` and `model`
packages.

Services coordinate promotions, sales, inventory, returns and warranties.
Domain calculations remain in model classes, while file operations
remain in repositories.

There is an exception to the intended dependency direction:
`ReturnRepository` imports and uses `SaleService`, `ProductService`
and `AccessoryService` to resolve stored identifiers.

The current implementation therefore includes dependencies from
persistence back to service. Diagrams must show this accurately;
strict one-way dependencies would require a separate refactoring.

`WarrantyService` uses `SaleRepository`, `ProductService`,
`PersonService` and `AccessoryService` during construction.
It retains its warranty repository and warranty collection.

## Remaining persistence limitations

`ReturnRepository` reconstructs returns and recalculates refunds
instead of restoring the saved `refundAmount`.

Sales reference catalog products rather than storing historical
snapshots of their prices. Changes to catalog prices can therefore
affect reconstructed sale totals and return calculations.

Older sale records without promotion fields cannot provide discount
information that was never stored.

These limitations are distinct from the corrected omission of promotion
fields in newly saved sales and the corrected mixed-catalog loading.

## Verification status

The persistence corrections compiled successfully with Java 17
compatibility using `javac --release 17`.

Compilation confirms that the updated sources are compatible at the
compiler level. It does not establish runtime persistence correctness.

End-to-end restart and refund verification remains pending.

## Final integration verification checklist

1. Register a sale containing a console and an accessory, with an active
   promotion and an extended warranty.
2. Check the subtotal, promotion discount, warranty cost and final total.
3. Restart the application and confirm that the mixed sale loads.
4. Confirm that the promotion name, discount and warranty cost are restored.
5. Return an accessory and check stock restoration and proportional refund.
6. Return a console and check warranty removal and its refundable cost.
7. Check monthly sales, returns and net balance.
8. Restart again and inspect the saved return records and amounts.
9. Load older sale records without promotion fields and verify that they
   remain readable with default promotion values.

This checklist describes pending verification and does not claim that
every scenario has passed.