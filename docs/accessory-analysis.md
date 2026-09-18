# Accessory Module Analysis — GameZone Inova

Answers to the orienting questions of Requirement 1. Every decision recorded here is the
one actually implemented in the source code, and it matches
[docs/accessory-class-diagram.md](accessory-class-diagram.md).

## Q1: Should accessories extend Product or form an independent hierarchy?

`Accessory` **extends `Product`**. Every accessory already needs an id, a title, a price
and a quantity, and a way to discount stock when it is sold — all of which `Product`
already provides, exactly the same set `VideoGame` and `Console` reuse. Duplicating those
four attributes and `decreaseStock(int)` into a separate hierarchy would mean maintaining
the same invariant (available stock) in two unrelated places.

The stronger reason is integration, not just field reuse: `Sale` already stores
`List<Product> products`, and `SaleService` already resolves sold items and discounts
their stock through `Product` references. Because `Accessory` **is a** `Product`, it slots
into that existing list, that existing repository lookup and that existing stock-discount
logic with no changes to `Sale` at all, and only additive changes to `SaleService` (see
Q4). An independent hierarchy would have forced `Sale` to hold two parallel lists (or a
wrapper type), `SaleRepository` to resolve ids against two unrelated catalogs by hand, and
`calculateTotal()` to sum across two different types — three places where a bug could make
products and accessories drift out of sync with each other.

## Q2: Common vs. specific attributes

Common to all three concrete accessory types, inherited from `Product`: `id`, `title`,
`price`, `quantity`. Also common to the three, but specific to accessories (not shared with
`VideoGame`/`Console`): `compatibleConsoleIds`, declared on `Accessory` itself. Specific to
each concrete type: `Controller.connectionType`; `Cable.lengthMeters` and
`Cable.connectorType`; `Memory.capacityGb` and `Memory.memoryType`.

This mirrors exactly how `Product` already splits between `VideoGame` and `Console`: an
abstract class in the middle (`Accessory`) holds what every accessory needs regardless of
type, and each concrete subclass adds only the fields unique to it, together with its own
`getDescription()` override.

## Q3: Representing compatibility between an accessory and a console

Compatibility is a **unidirectional association**, stored only on the accessory side:
`Accessory.compatibleConsoleIds` is a `List<String>` of console ids. `Console` carries no
back-reference to the accessories compatible with it.

This is an attribute of the accessory, not of the console, and not of both. The direction
follows how the system actually queries it: every lookup in the PDF goes
"which accessories fit this console" (`AccessoryService.findAccessoriesCompatibleWith`),
never "which accessories does this console list," so only one side needs the data. Storing
console **ids** instead of `Console` references also keeps `model` decoupled — `Accessory`
never needs to load or hold a real `Console` object, and adding compatibility never touches
the `Console` class at all, which matters because `Console` belongs to a different module
in this exact same exam (the product hierarchy) that this module is not supposed to modify.

In persistence, the list is stored inline in `data/accessories.csv`: each accessory's line
carries its compatible console ids joined with `|` in the same field
(`AccessoryRepository.toLine`/`fromLine`), so no separate compatibility table or file is
needed.

## Q4: Changes needed in SaleService

`SaleService`'s constructor gained one additive parameter, `AccessoryService`. Everything
else about how a sale is built is unchanged for pure-product sales:

- The constructor now builds one combined catalog (`productService.listAll()` plus
  `accessoryService.listAllAccessories()`) before calling `SaleRepository.load(...)`. This
  works with zero changes to `SaleRepository` because that class already takes a plain
  `List<Product>` to resolve ids against, and an `Accessory` already is a `Product`.
- Two new private helpers, `findItemById(id)` and `updateStockOf(id, amount)`, each try
  `productService` first and fall back to `accessoryService` only if the product lookup
  throws `NoSuchElementException`. Existing behavior with only products is preserved
  because the product lookup always succeeds first for a real product id — the accessory
  path is only ever reached for ids `ProductService` does not recognize.
- `registerSale`'s validation (at least one item, enough stock per id) and `Sale`'s own
  `calculateTotal()` needed no changes: both already operate on `Product` references and
  do not care whether a given reference is a `VideoGame`, a `Console` or an `Accessory`.

## Q5: Layer placement of the new classes

`Accessory`, `Controller`, `Cable` and `Memory` live in `model`: they are plain domain
entities with no file I/O, the same responsibility `Product`, `VideoGame` and `Console`
already have in that layer. `AccessoryRepository` lives in `persistence`: it is the only
class in this module that touches `data/accessories.csv`, keeping file-format details out
of the domain classes (a model class doing its own I/O would violate the same rule that
already keeps `ProductRepository` separate from `Product`). `AccessoryService` lives in
`service`: it owns the business rules this module adds — rejecting duplicate ids, deciding
how to filter by type or by console compatibility, coordinating persistence — mirroring
`ProductService`'s role for the existing product catalog. The criterion in all three cases
is the same one already used for the rest of the system: what a class is *responsible for*,
not which layer would be convenient to put it in.
