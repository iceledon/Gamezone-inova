# GRASP refactor implementation log

## 2026-10-23 - stock/type consumers and discount-ratio producer

Base revision: d2fee8f. Implementer: Isabel with substantive AI coding assistance.
Jaime's producer methods were already present before this delivery.

| Finding | Change | Status |
|---|---|---|
| V-01 | ProductService.restoreStock and AccessoryService.restoreStock delegate arithmetic to Product.increaseStock; persistence calls remain in place | Implemented; review pending |
| V-06 | AccessoryService.typeOf delegates to getAccessoryType for the existing concrete hierarchy | Implemented; review pending |
| V-02 | Sale.calculateDiscountRatio uses the original nonpositive-subtotal guard and discount/subtotal expression | Producer implemented; Jaime's Return consumer remains pending |

Validation: javac --release 17 succeeds. Replayed the seven menu scripts
covering 30 operations using their original per-module input-data fixtures.
All exit codes are zero; resulting data matches the original baseline.
Raw stdout differs from the user-captured historical files because of capture
encoding. A control replay of unchanged baseline classes under the same capture
method matches the current stdout byte-for-byte in all seven modules.
Original captures were retained. Evidence: evidence/r7/replay-stock-types-ratio.
A direct probe verifies zero-subtotal and 25-percent discount-ratio results.

Maven was not executed in this delivery. Remaining special-case replay and final
full integration checks are pending. No completed R7 claim is made here.

Commit and PR: pending publication/review; replace with actual identifiers.