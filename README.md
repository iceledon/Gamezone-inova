# GameZone Inova

Console application for managing a video game and console store: it registers products,
customers, sellers and sales, and updates the inventory automatically every time a sale is
registered. Data survives between runs through plain-text files, with no database involved.

Built in Java with a strict four-layer architecture as the workshop requires.

## Requirements

- JDK 17 or later
- Maven 3.8 or later

## Build

```
mvn clean package
```

## Run

```
java -jar target/gamezone-inova-1.0.0-SNAPSHOT.jar
```

The application must be run from the repository root, because the data files are read from and
written to the `data/` folder using relative paths.

## Architecture

Four layers under the `com.gamezone` package, with dependencies flowing in one direction only:
`ui → service → persistence → model`.

```mermaid
flowchart TD
    UI["ui — console menus"] --> SERVICE["service — business rules"]
    SERVICE --> PERSISTENCE["persistence — file input/output"]
    SERVICE --> MODEL["model — domain entities"]
    PERSISTENCE --> MODEL
```

| Layer | Responsibility |
|---|---|
| `model` | Domain entities: `Person`, `Customer`, `Seller`, `Product`, `VideoGame`, `Console`, `Accessory`, `Controller`, `Cable`, `Memory`, `Sale`, `Return`, `Warranty`, `BasicWarranty`, `ExtendedWarranty`. Depends on nothing. |
| `persistence` | Reads and writes the files under `data/`: `PersonRepository`, `ProductRepository`, `AccessoryRepository`, `SaleRepository`, `ReturnRepository`, `WarrantyRepository`. |
| `service` | Business rules and coordination: `PersonService`, `ProductService`, `AccessoryService`, `SaleService`, `ReturnService`, `WarrantyService`. |
| `ui` | `ConsoleUI`, the only class that talks to the user. Never touches a repository. |

## Data files

Plain text, one record per line, fields separated by `;`:

| File | Format |
|---|---|
| `data/products.txt` | `VIDEOGAME;id;title;price;quantity;platform;genre;ageRating`<br>`CONSOLE;id;title;price;quantity;brand;model;generation` |
| `data/customers.txt` | `id;name;phone;email` |
| `data/sellers.txt` | `id;name;phone;employeeCode;shift` — preloaded with three sellers |
| `data/sales.txt` | `id;date;customerId;sellerId;productId1,productId2,...;extraCost` |
| `data/returns.csv` | `id;date;saleId;productId1,productId2,...;reason;refundAmount` |
| `data/warranties.csv` | `BASIC/EXTENDED;id;productId;saleId;startDate` |
| `data/accessories.csv` | `CONTROLLER,id,title,price,quantity,connectionType,compatibleConsoleIds`<br>`CABLE,id,title,price,quantity,lengthMeters,connectorType,compatibleConsoleIds`<br>`MEMORY,id,title,price,quantity,capacityGb,memoryType,compatibleConsoleIds`<br>(`compatibleConsoleIds` is a list of console ids joined with `\|`, empty if none) — preloaded with three accessories |

Sales store only the ids of what they reference; the objects are resolved against the products and
people already loaded in memory when the application starts.

## Available operations

**Products:** register a video game · register a console · list the inventory
**People:** register a customer · list customers · list sellers
**Sales:** register a sale (products, accessories, or a mix of both) · full sales history · history by customer · history by seller
**Returns:** register a return · full return history · history by customer · history by sale · monthly balance
**Warranties:** consult warranty by product and sale · list all warranties · list active warranties · list warranties expiring soon
**Accessories:** register a controller · register a cable · register a memory card · list all accessories · list by type · list accessories compatible with a console

Registering a sale validates that it has at least one product, that the customer, the seller and
every product exist, and that there is enough stock for every unit requested; only then is the
inventory discounted and the sale saved. For each console sold, a basic factory defect warranty
(6 months, no additional cost) is automatically assigned, and an optional extended warranty (12 months,
10% of product price) can be requested and added to the sale total.

Registering a return validates that the original sale exists, that no more than 30 calendar days
have passed since it was made, and that every product being returned really belongs to that sale;
only then is the stock restored and the return saved. The monthly balance subtracts the refunds of
a month from the sales of that same month.

## Documentation

- [TEAM.md](TEAM.md) — members, roles and committed activities
- [docs/analysis.md](docs/analysis.md) — answers to the orienting questions
- [docs/hierarchy-diagram.md](docs/hierarchy-diagram.md) — inheritance in the model layer
- [docs/class-diagram.md](docs/class-diagram.md) — full class diagram of the four layers
- [docs/return-analysis.md](docs/return-analysis.md) — answers to the return module orienting questions
- [docs/return-class-diagram.md](docs/return-class-diagram.md) — class diagram of the return module
- [docs/warranty-analysis.md](docs/warranty-analysis.md) — answers to the warranty module orienting questions
- [docs/warranty-class-diagram.md](docs/warranty-class-diagram.md) — class diagram of the warranty module
- [docs/accessory-analysis.md](docs/accessory-analysis.md) — answers to the accessory module orienting questions
- [docs/accessory-class-diagram.md](docs/accessory-class-diagram.md) — class diagram of the accessory module
- [docs/layers-diagram.md](docs/layers-diagram.md) — layer dependencies
- [docs/ai-usage/](docs/ai-usage) — AI usage logs of each team member

## License

MIT — see [LICENSE](LICENSE).
