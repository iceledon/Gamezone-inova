# GameZone Inova

Java console application for managing a video game store with products,
accessories, customers, sellers, promotions, sales, warranties and returns.

The application stores data in text files under `data/`.

## Requirements

- JDK 17 or later.
- Maven 3.8 or later for the Maven build.

## Build and run with Maven

From the repository root:

```text
mvn clean package
java -jar target/gamezone-inova-1.0.0-SNAPSHOT.jar
```

## Compile and run without Maven on Windows

The application declares no external dependencies. In PowerShell, from
the repository root:

```powershell
New-Item -ItemType Directory -Force -Path target/manual-classes | Out-Null

$gamezoneSources = @(
    Get-ChildItem -Path src/main/java -Recurse -Filter *.java |
    Select-Object -ExpandProperty FullName
)

javac --release 17 -encoding UTF-8 -d target/manual-classes $gamezoneSources
```

If compilation succeeds, run:

```powershell
java -cp target/manual-classes com.gamezone.Main
```

This compiles application sources; it does not execute an automated
test suite.

Run the application from the repository root because data paths are relative.

## Available operations

- **Products:** register videogames and consoles; list inventory.
- **People:** register customers; list customers and sellers.
- **Accessories:** register controllers, cables and memory cards; query
  accessories by type or console compatibility.
- **Promotions:** register percentage, category and bulk purchase discounts;
  list registered and active promotions.
- **Sales:** register sales containing products and accessories; consult
  history by customer or seller.
- **Warranties:** query coverage by product and sale; list all, active and
  soon-to-expire warranties.
- **Returns:** register returns; query history by customer or sale; display
  monthly sales, returns and net balance.

## Integrated sale flow

Sale registration validates the customer, seller, items and available stock.
It creates the sale, assigns the best active promotion, assigns console
warranties, updates inventory and saves the sale.

Only one promotion applies: the active promotion offering the highest
positive monetary discount. Category promotions support `VIDEOGAME`,
`CONSOLE` and `ACCESSORY`.

Consoles receive a basic warranty. An optional extended warranty adds its
cost separately from the item subtotal used for discounts.

```text
sale total = item subtotal - promotion discount + warranty costs
```

The saved sale includes its warranty costs, discount amount and applied
promotion name.

## Integrated return flow

Return registration checks that the original sale exists, that it is
within the 30-day return period and that requested items belong to it.

Stock is restored through `ProductService` or `AccessoryService`,
depending on the item type.

The original sale discount is allocated proportionally across item prices:

```text
discount ratio = original discount / original item subtotal
item refund = item price * (1 - discount ratio)
total refund = sum of item refunds + warranty refund
```

When the original subtotal is zero or negative, the discount ratio is zero.

Returning a console removes warranties matching its product and sale
identifiers and adds their additional costs to the refund.

The monthly balance reports sales and returns separately, then calculates:

```text
monthly net balance = monthly sales - monthly returns
```

Sales are counted by sale date; returns are counted by return date.

## Integration adjustments

| Adjustment | Result |
|---|---|
| A1 | Category promotions recognize accessories; registration validates the three supported categories. |
| A2 | `WarrantyService` loads sales through `SaleRepository`, removing its dependency on `SaleService`. |
| A3 | Sale registration assigns the promotion before generating warranties. |
| A4 | Accessory returns restore accessory stock and can be resolved when loading return records. |
| A5 | Item refunds apply the original sale's proportional discount. |
| A6 | Monthly sales, returns and net balance are exposed separately; existing behavior was reviewed. |
| A7 | Console returns cancel matching warranties and include their refundable costs. |

Additional persistence corrections save promotion information with each
sale and include accessories when reconstructing sales for warranty loading.

## Architecture

The application has four packages under `com.gamezone`:

| Layer | Responsibility |
|---|---|
| `model` | Products, people, accessories, promotions, sales, warranties and returns. |
| `persistence` | File access through the corresponding repositories. |
| `service` | Business rules and coordination across modules. |
| `ui` | Console interaction through `ConsoleUI`. |

`Main` constructs and connects the application components.

The intended layer direction is `ui -> service -> persistence -> model`,
with services also using model objects and the UI reading returned objects.

An existing exception is `ReturnRepository`, which depends on
`SaleService`, `ProductService` and `AccessoryService` to resolve identifiers.
See [the layers diagram](docs/layers-diagram.md).

`WarrantyService` receives `SaleRepository`, `ProductService`,
`PersonService` and `AccessoryService` during construction. It combines
products and accessories before loading sale and warranty references.

## Data files

Most files use semicolons between fields. Accessories use commas.

| File | Record format |
|---|---|
| `data/products.txt` | `VIDEOGAME;id;title;price;quantity;platform;genre;ageRating` or `CONSOLE;id;title;price;quantity;brand;model;generation` |
| `data/customers.txt` | `id;name;phone;email` |
| `data/sellers.txt` | `id;name;phone;employeeCode;shift` |
| `data/sales.txt` | `id;date;customerId;sellerId;productIds;extraCost;discountAmount;appliedPromotionName` |
| `data/returns.csv` | `id;date;saleId;productIds;reason;refundAmount;warrantyRefund` |
| `data/warranties.csv` | `BASIC/EXTENDED;id;productId;saleId;startDate` |
| `data/promotions.csv` | Formats listed below. |
| `data/accessories.csv` | Formats listed below. |

Sale and return product identifiers are separated by commas.

Promotion records:

```text
PERCENTAGE;id;name;startDate;endDate;percentage
CATEGORY;id;name;startDate;endDate;percentage;targetCategory
BULK;id;name;startDate;endDate;minimumQuantity;percentage
```

Accessory records:

```text
CONTROLLER,id,title,price,quantity,connectionType,compatibleConsoleIds
CABLE,id,title,price,quantity,lengthMeters,connectorType,compatibleConsoleIds
MEMORY,id,title,price,quantity,capacityGb,memoryType,compatibleConsoleIds
```

Compatible console identifiers are separated by `|`.

Older sales without promotion fields remain readable. Missing discount
amounts default to zero, and missing promotion names remain unset.

Older return records without `warrantyRefund` load with a zero warranty
refund. If the field is present, it must contain a numeric value.

## Persistence corrections and remaining limitations

`SaleRepository` now saves and restores `discountAmount` and
`appliedPromotionName`, in addition to warranty costs.

Previously unsaved discounts cannot be recovered automatically from older
records. The application does not recalculate historical promotions to
replace missing information.

`WarrantyService` now combines the product and accessory catalogs when
loading sales and warranty references. This addresses unresolved accessory
identifiers during warranty initialization.

Compilation succeeded with Java 17 compatibility. End-to-end restart
and refund verification remains pending.

Remaining limitations:

- `ReturnRepository` recalculates refunds when loading instead of
  restoring the saved `refundAmount`. Values depend on reconstructed
  sales and current product prices.
- Sales reference catalog products rather than storing a historical
  snapshot of each item's price.
- `ReturnRepository` depends on services, contrary to the intended
  strict layer direction.

The sample accessory promotion expires on 2026-09-26. Use a promotion
valid on the demonstration date when checking automatic discounts.

## Verification

The final integration scenario should include:

1. A console and accessory sale with an active promotion and extended warranty.
2. Verification of subtotal, discount, warranty cost and total.
3. Accessory and console returns, including restored stock and warranty refund.
4. Verification of monthly sales, returns and net balance.
5. Application restart and verification of restored records and amounts.
6. Loading older sale records that do not contain promotion fields.

The checklist describes required verification, not a claim that every
scenario has passed.

## Documentation

- [Team](TEAM.md)
- [Initial analysis](docs/analysis.md)
- [Initial hierarchy diagram](docs/hierarchy-diagram.md)
- [Initial class diagram](docs/class-diagram.md)
- [Accessory analysis](docs/accessory-analysis.md)
- [Accessory class diagram](docs/accessory-class-diagram.md)
- [Promotion analysis](docs/promotion-analysis.md)
- [Promotion class diagram](docs/promotion-class-diagram.md)
- [Warranty analysis](docs/warranty-analysis.md)
- [Warranty class diagram](docs/warranty-class-diagram.md)
- [Return analysis](docs/return-analysis.md)
- [Return class diagram](docs/return-class-diagram.md)
- [Monthly balance verification](docs/monthly-balance-verification.md)
- [Integration analysis](docs/integration-analysis.md)
- [Integrated class diagram](docs/integrated-class-diagram.md)
- [Layers diagram](docs/layers-diagram.md)
- [AI usage logs](docs/ai-usage/)

The integrated diagrams describe the combined system. Earlier diagrams
provide context for their original modules and may omit later adjustments.

## License

MIT - see [LICENSE](LICENSE).