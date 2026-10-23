# Special baseline cases

Reference source b416df2; executed 2026-10-23. No production Java changed.
Runner: ../run-special-baseline.ps1; probe: ../R7BaselineProbe.java.
All six cases ran twice; stdout and resulting data comparisons are identical.

- E05 corrupt startup: truncated products record; safe persistence message on
  stderr, exit 1. This is the observed expected baseline, not an execution pass
  inferred from exit 0. The first runner stopped on this code; it was corrected
  to retain the evidence and continue the second run.
- E05 write failure: after loading ProductService, move the isolated products
  file to a backup and occupy its former path with a directory. Registration
  raises PersistenceException; the rejected product is absent from memory.
  This exercises an unwritable target, not every Windows ACL/read-only case.
- E06 overlapping IDs: accessory VG001 collides with product VG001; existing
  return reconstruction resolves the product first, refund 259900.00. This
  verifies ReturnRepository resolution, not every other catalog consumer.
- E07 return boundary: no-argument Sale.canBeReturned for sales dated today
  minus 30/31 days returns true/false; a future-dated sale remains returnable.
  Probe dates derive from LocalDate.now; no production clock was changed.
- E08 promotion tie: two equal 10% active promotions; first encountered wins.
  Sale subtotal 259900.00, discount 25990.00, total 233910.00.
- E09 legacy optional fields: sales trimmed to five fields, returns to six.
  History and returns reload successfully with the current default handling.

Cases other than corrupt startup exit 0 with empty stderr. Keep runtime date,
locale and encoding consistent for replay; preserve raw evidence. These are
baseline observations, not proof that later refactoring preserves behavior.

The planned additional groups E01 through E11 now have recorded scenarios.
This does not assert exhaustive edge-case coverage or replace the final replay.