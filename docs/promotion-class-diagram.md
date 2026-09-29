# Promotion Class Diagram

The diagram shows the promotion hierarchy and its integration with sales.
Domain attributes are listed for the promotion classes and `Sale`.
Supporting classes show only the members relevant to this module;
constructors and routine accessors are omitted.

```mermaid
classDiagram
    class Promotion {
        <<abstract>>
        -String id
        -String name
        -LocalDate startDate
        -LocalDate endDate
        +isActive(LocalDate date) boolean
        +calculateDiscount(Sale sale) double*
    }

    class PercentageDiscount {
        -double percentage
        +calculateDiscount(Sale sale) double
    }

    class CategoryDiscount {
        -double percentage
        -String targetCategory
        +calculateDiscount(Sale sale) double
        -matchesTargetCategory(Product product) boolean
    }

    class BulkPurchaseDiscount {
        -int minimumQuantity
        -double percentage
        +calculateDiscount(Sale sale) double
    }

    class PromotionService {
        -PromotionRepository repository
        -List~Promotion~ promotions
        +registerPercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage) void
        +registerCategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage, String targetCategory) void
        +registerBulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate, int minimumQuantity, double percentage) void
        +listAllPromotions() List~Promotion~
        +listActivePromotions() List~Promotion~
        +findBestPromotionFor(Sale sale) Promotion
        +findById(String id) Promotion
        -rejectIfIdExists(String id) void
        -isValidTargetCategory(String targetCategory) boolean
    }

    class PromotionRepository {
        +loadAll() List~Promotion~
        +saveAll(List~Promotion~ promotions) void
    }

    class Sale {
        -String id
        -LocalDate date
        -Customer customer
        -Seller seller
        -List~Product~ products
        -double extraCost
        -String appliedPromotionName
        -double discountAmount
        +addExtraCost(double amount) void
        +setAppliedPromotionName(String appliedPromotionName) void
        +setDiscountAmount(double discountAmount) void
        +calculateSubtotal() double
        +calculateTotal() double
        +canBeReturned() boolean
        +generateReceipt() String
    }

    class SaleService {
        +registerSale(String customerId, String sellerId, List~String~ productIds, List~String~ productIdsWithExtendedWarranty) Sale
    }

    class Product
    class VideoGame
    class Console
    class Accessory
    class Customer
    class Seller

    Promotion <|-- PercentageDiscount
    Promotion <|-- CategoryDiscount
    Promotion <|-- BulkPurchaseDiscount

    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory

    Promotion ..> Sale : evaluates
    CategoryDiscount ..> VideoGame : VIDEOGAME
    CategoryDiscount ..> Console : CONSOLE
    CategoryDiscount ..> Accessory : ACCESSORY

    PromotionService --> PromotionRepository : loads and saves
    PromotionService --> Promotion : manages and compares
    PromotionRepository ..> Promotion : persists

    SaleService --> PromotionService : selects best promotion
    SaleService ..> Sale : creates and updates

    Sale --> Product : contains items
    Sale --> Customer : purchased by
    Sale --> Seller : registered by
```

## Integration notes

- `CategoryDiscount` recognizes accessories through `instanceof Accessory`.
- `PromotionService` accepts `VIDEOGAME`, `CONSOLE` and `ACCESSORY` when
  registering a category discount.
- Only the active promotion offering the highest positive discount applies.
- `SaleService` assigns the promotion before generating warranties.
- Warranty costs are added separately from the item subtotal used for discounts.