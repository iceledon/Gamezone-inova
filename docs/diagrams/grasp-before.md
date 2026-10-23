# R7 design before corrections

Source: b416df2. Focused dependency diagram for V-01 through V-08;
this is not a complete class/attribute inventory. Arrows describe current
collaboration, not the proposed design. Created before Java corrections.

```mermaid
classDiagram
    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory
    Main --> ConsoleUI
    Main --> ReturnRepository
    ConsoleUI --> ProductService
    ConsoleUI --> PersonService
    ConsoleUI --> SaleService
    ConsoleUI --> ReturnService
    ConsoleUI --> WarrantyService
    ConsoleUI --> PromotionService
    ConsoleUI --> AccessoryService
    ConsoleUI --> Scanner
    ProductService --> Product : calculates restored quantity
    AccessoryService --> Accessory : restores quantity and classifies subtype
    Return --> Sale : reads subtotal and discount
    CategoryDiscount --> Product : tests concrete category types
    Sale --> LocalDate : reads current date for return window
    ReturnService --> ReturnRepository
    ReturnService --> SaleService : monthly sales and return coordination
    ReturnService --> ProductService
    ReturnService --> AccessoryService
    ReturnService --> WarrantyService
    ReturnRepository --> SaleService : resolves saved sale
    ReturnRepository --> ProductService : resolves saved items
    ReturnRepository --> AccessoryService : resolves saved accessories
```

Evidence: Main constructors; ConsoleUI fields and menu methods;
ProductService.restoreStock; AccessoryService.restoreStock/typeOf;
Return.discountRatio; CategoryDiscount.matchesTargetCategory;
Sale.canBeReturned; ReturnService monthly methods; ReturnRepository fields.

The after diagram must describe the implemented revision, not merely this plan.