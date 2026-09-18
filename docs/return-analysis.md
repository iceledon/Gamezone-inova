# Return Module Analysis — GameZone Inova

Answers to the orienting questions of Requirement 3. Every decision recorded here is the
one actually implemented in the source code, and it matches
[docs/return-class-diagram.md](return-class-diagram.md).

## Q1: Relationship between Return and Sale

`Return` is related to `Sale` through **association**, not inheritance, aggregation or
composition. A return holds a reference to exactly one original sale (`originalSale`),
but it does not own that sale's life cycle: the sale exists before the return is created
and keeps existing after it, and the same sale could in principle be referenced by more
than one return over time (since a return does not have to include every product of the
sale). It is not composition, because deleting a return must never delete the sale it
references. It is not inheritance either, since a return is not a kind of sale.

## Q2: Representing a partial return

A return does not assume it covers the whole original sale. `Return` keeps its own
`List<Product> returnedProducts`, populated only with the specific product instances the
customer chose to return, resolved by id against `originalSale.getProducts()`. This lets
the customer keep some products from the sale and return only the rest, since the
returned list can be a proper subset of the original sale's product list.

## Q3: Where the 30-day rule lives

The date comparison itself lives inside `Sale.canBeReturned()`, because it only needs
data the `Sale` object already owns (its own `date`) — the same reasoning already used for
`calculateTotal()` in the base analysis. The decision of what to do when the answer is
`false` — reject the whole operation with a message the user can understand — belongs to
`ReturnService.registerReturn(...)`, in the service layer, because rejecting an invalid
request is a business rule that must apply no matter who calls the operation. The Java
mechanism used is `java.time.LocalDate`: `date.plusDays(30)` to compute the deadline and
`LocalDate.now().isAfter(...)` to compare it against today.

## Q4: Reusing existing stock logic

`ProductService.restoreStock(...)` reuses the setter `Product.setQuantity(int)`, which
was already generated in Taller 1 for the `quantity` attribute. It is invoked from
`ProductService.restoreStock(String, int)`, which is called by `ReturnService` once a
return is validated. Reusing the existing setter instead of writing a new field-touching
method, or duplicating the logic already used by `decreaseStock(int)`, avoids having two
different pieces of code responsible for the same invariant (the product's available
quantity), which would make it easy for them to drift apart later.

## Q5: Where the monthly balance report lives

`generateMonthlyBalance(int, int)` lives in `ReturnService`, because it needs to
consolidate data from two modules — sales and returns — and coordinating across modules is
exactly the responsibility of a service class, matching the layer definition already
used in the base analysis ("service = business rules and coordination"). Placing it here
instead of in `SaleService` keeps the returns module self-contained: `ReturnService`
already depends on `SaleService` to resolve the original sale of every return, so no new
dependency direction is introduced. The dependencies it needs are `SaleService` (to read
every sale of the requested month through `getAllSales()` and filter by date) and its own
in-memory list of returns, loaded through `ReturnRepository`, to sum the refund amounts of
the same period.
