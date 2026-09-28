# Return Module — Class Diagram

Class diagram of the return module and its integration with the classes that already
existed in Taller 1. Attributes and methods shown here are exactly the ones implemented in
the source code. Existing classes are shown only with the members the module touches.

```mermaid
classDiagram
    class Sale {
        -String id
        -LocalDate date
        -Customer customer
        -Seller seller
        -List~Product~ products
        +calculateTotal() double
        +canBeReturned() boolean
    }

    class Return {
        -String id
        -LocalDate date
        -Sale originalSale
        -List~Product~ returnedProducts
        -String reason
        -double refundAmount
        +calculateRefundAmount() double
        +generateReturnReceipt() String
    }

    class Product {
        -String id
        -String title
        -double price
        -int quantity
        +decreaseStock(int)
        +setQuantity(int)
    }

    class ReturnRepository {
        -SaleService saleService
        -ProductService productService
        +saveAll(List~Return~)
        +loadAll() List~Return~
    }

    class ReturnService {
        -ReturnRepository repository
        -SaleService saleService
        -ProductService productService
        -List~Return~ returns
        +registerReturn(String, List~String~, String) Return
        +viewAllReturns() List~Return~
        +viewReturnsByCustomer(String) List~Return~
        +viewReturnsBySale(String) List~Return~
        +generateMonthlyBalance(int, int) double
        +calculateMonthlySales(int, int) double
        +calculateMonthlyReturns(int, int) double
    }

    class ProductService {
        +restoreStock(String, int)
    }

    class SaleService {
        +findById(String) Sale
    }

    class ConsoleUI {
        -ReturnService returnService
    }

    Return "0..*" --> "1" Sale : originalSale
    Return "1" o-- "1..*" Product : returnedProducts
    ReturnService --> ReturnRepository
    ReturnService --> SaleService
    ReturnService --> ProductService
    ReturnRepository --> SaleService
    ReturnRepository --> ProductService
    ConsoleUI --> ReturnService
```
