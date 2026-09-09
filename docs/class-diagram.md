# Class Diagram — GameZone Inova

Complete class diagram of the four layers, with UML visibility (`+` public, `-` private), abstract
classes and abstract methods marked, and multiplicity on every relationship. Attributes and methods
shown here are exactly the ones implemented in the source code.

```mermaid
classDiagram
    %% ===================== MODEL LAYER =====================
    class Person {
        <<abstract>>
        -id: String
        -name: String
        -phone: String
        +getRoleDescription() String*
    }
    class Customer {
        -email: String
        +getRoleDescription() String
    }
    class Seller {
        -employeeCode: String
        -shift: String
        +getRoleDescription() String
    }
    Person <|-- Customer
    Person <|-- Seller

    class Product {
        <<abstract>>
        -id: String
        -title: String
        -price: double
        -quantity: int
        +getDescription() String*
        +decreaseStock(amount int) void
    }
    class VideoGame {
        -platform: String
        -genre: String
        -ageRating: String
        +getDescription() String
    }
    class Console {
        -brand: String
        -model: String
        -generation: int
        +getDescription() String
    }
    Product <|-- VideoGame
    Product <|-- Console

    class Sale {
        -id: String
        -date: LocalDate
        -customer: Customer
        -seller: Seller
        -products: List~Product~
        +calculateTotal() double
    }
    Sale "0..*" --> "1" Customer
    Sale "0..*" --> "1" Seller
    Sale "1" o-- "1..*" Product

    %% ===================== PERSISTENCE LAYER =====================
    class PersonRepository {
        +saveCustomers(customers List~Customer~) void
        +loadCustomers() List~Customer~
        +saveSellers(sellers List~Seller~) void
        +loadSellers() List~Seller~
    }
    class ProductRepository {
        +save(products List~Product~) void
        +load() List~Product~
    }
    class SaleRepository {
        +save(sales List~Sale~) void
        +load(products List~Product~, customers List~Customer~, sellers List~Seller~) List~Sale~
    }
    PersonRepository ..> Customer
    PersonRepository ..> Seller
    ProductRepository ..> Product
    SaleRepository ..> Sale
    SaleRepository ..> Product
    SaleRepository ..> Customer
    SaleRepository ..> Seller

    %% ===================== SERVICE LAYER =====================
    class PersonService {
        -repository: PersonRepository
        +registerCustomer(id String, name String, phone String, email String) void
        +listCustomers() List~Customer~
        +listSellers() List~Seller~
        +findCustomerById(id String) Customer
        +findSellerById(id String) Seller
    }
    class ProductService {
        -repository: ProductRepository
        +registerVideoGame(...) void
        +registerConsole(...) void
        +listAll() List~Product~
        +updateStock(productId String, amount int) void
        +findById(id String) Product
    }
    class SaleService {
        -repository: SaleRepository
        -productService: ProductService
        -personService: PersonService
        +registerSale(customerId String, sellerId String, productIds List~String~) Sale
        +getAllSales() List~Sale~
        +getSalesByCustomer(customerId String) List~Sale~
        +getSalesBySeller(sellerId String) List~Sale~
    }
    PersonService "1" --> "1" PersonRepository
    ProductService "1" --> "1" ProductRepository
    SaleService "1" --> "1" SaleRepository
    SaleService "1" --> "1" ProductService
    SaleService "1" --> "1" PersonService
    SaleService ..> Sale

    %% ===================== UI LAYER =====================
    class ConsoleUI {
        -productService: ProductService
        -personService: PersonService
        -saleService: SaleService
        +start() void
    }
    ConsoleUI "1" --> "1" ProductService
    ConsoleUI "1" --> "1" PersonService
    ConsoleUI "1" --> "1" SaleService

    %% ===================== ENTRY POINT =====================
    class Main {
        +main(args String[])$ void
    }
    Main ..> ConsoleUI
```

## Notes on the relationships

- **Inheritance:** `Person <|-- Customer/Seller` and `Product <|-- VideoGame/Console`.
- **Association:** `Sale --> Customer` and `Sale --> Seller`, with multiplicity `0..*` to `1`; many
  sales may reference the same person, and the person exists independently of any sale.
- **Aggregation:** `Sale o-- Product` (`1..*`); the sale groups products that live in the inventory
  on their own.
- **Dependency:** repositories and services depend on the model classes they read, write or
  orchestrate; `Main` depends on `ConsoleUI` only to launch it.
- A `Customer` deliberately does **not** hold a list of its own sales: keeping that list inside the
  model would couple `Customer` to `Sale` for no benefit. The purchase history is resolved on demand
  by `SaleService.getSalesByCustomer(...)`, which filters the sales it already holds.
- `SaleRepository.load(...)` receives the products, customers and sellers it needs to resolve the
  ids stored in `data/sales.txt`. They are passed in by `SaleService`, the layer above, instead of
  being pulled from the services: that keeps `persistence` depending only on `model` and never
  inverts the layer direction defined in [layers-diagram.md](layers-diagram.md).
