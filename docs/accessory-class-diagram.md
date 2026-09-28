# Accessory Module — Class Diagram

Class diagram of the accessory module and its integration with the classes that already
existed before this module. Attributes and methods shown here are exactly the ones
implemented in the source code. Existing classes are shown only with the members the
module touches.

```mermaid
classDiagram
    class Product {
        <<abstract>>
        -String id
        -String title
        -double price
        -int quantity
        +getDescription() String
        +decreaseStock(int)
    }

    class Accessory {
        <<abstract>>
        -List~String~ compatibleConsoleIds
        +getCompatibleConsoleIds() List~String~
        +addCompatibleConsole(String)
        +removeCompatibleConsole(String)
        +isCompatibleWith(String) boolean
    }

    class Controller {
        -String connectionType
        +getDescription() String
    }

    class Cable {
        -double lengthMeters
        -String connectorType
        +getDescription() String
    }

    class Memory {
        -int capacityGb
        -String memoryType
        +getDescription() String
    }

    class Console {
        -String brand
        -String model
        -int generation
    }

    class AccessoryRepository {
        +saveAll(List~Accessory~)
        +loadAll() List~Accessory~
    }

    class AccessoryService {
        -AccessoryRepository repository
        -List~Accessory~ accessories
        +registerController(...) void
        +registerCable(...) void
        +registerMemory(...) void
        +listAllAccessories() List~Accessory~
        +listAccessoriesByType(String) List~Accessory~
        +findAccessoriesCompatibleWith(String) List~Accessory~
        +findById(String) Accessory
        +updateStock(String, int) void
    }

    class Sale {
        -List~Product~ products
        +calculateTotal() double
    }

    class SaleService {
        -ProductService productService
        -AccessoryService accessoryService
        +registerSale(String, String, List~String~) Sale
    }

    class ConsoleUI {
        -AccessoryService accessoryService
    }

    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory
    AccessoryService --> AccessoryRepository
    SaleService --> AccessoryService
    SaleService --> Sale : builds
    Sale "1" o-- "1..*" Product : products
    ConsoleUI --> AccessoryService
    Accessory "0..*" ..> "0..*" Console : compatibleConsoleIds (by id)
```

## Notes

- `Accessory` extends `Product`, not a new independent hierarchy (see
  [docs/accessory-analysis.md](accessory-analysis.md), Q1) — that is what lets `Sale`'s
  existing `List<Product> products` hold accessories with no changes to `Sale` itself.
- The dotted arrow from `Accessory` to `Console` is intentionally not a normal association:
  compatibility is stored as a list of console **ids** on `Accessory`
  (`compatibleConsoleIds`), not as direct references to `Console` objects, and `Console`
  carries nothing back — the relationship is unidirectional (Q3).
- `SaleService` depends on both `ProductService` (already existing) and `AccessoryService`
  (new, additive constructor parameter) to resolve a sold item and discount its stock
  regardless of which catalog it belongs to (Q4).
