# Integrated R7 verification

Source: feature/grasp-refactor HEAD 5af8633 plus the recorded integration diff.
Source hashes and source.diff identify the exact production files tested.
Date: 2026-10-23. Baseline: archived b416df2 source, freshly compiled.

Result: all 19 recorded groups match between baseline/current stdout, stderr,
exit status and resulting data. Seven groups cover the 30 submenu operations;
the remaining groups exercise E01-E11 (E05 has separate read/write cases).
See results.csv. Original repository data hashes remained unchanged.

Corrupt startup exits 1 in both versions and displays the safe persistence
message. All other recorded processes exit 0. Raw captures are retained; no
accent normalization was used to make comparisons pass. The same capture
method was used for both versions, including the returns output.

Additional RefactorSemanticsProbe comparisons cover all three category labels,
lowercase/unknown/null targets, null product entries, a partial discounted
return and a full discounted return with warranty refund. Both versions agree.
A null-product guard was restored in CategoryDiscount because the original
instanceof-based classification ignored null entries. This preserves existing
behavior and does not add a new validation rule.

Production changes under verification: stock/category/subtype model operations,
service consumers, Sale ratio and explicit date API, Return ratio delegation,
service-independent return loading, focused monthly reporting, seven module
menus and shared input/error support. ConsoleUI now retains main navigation
and construction/wiring only.

Compilation: javac --release 17 for both versions and the probes succeeded.
Maven is unavailable through Get-Command mvn; no Maven build is claimed.
The combined runner completed before the semantics hook was added; the same
probe was then compiled/executed separately against both compiled versions.
The updated runner parses and includes that hook for later reruns.

Limits: these are the recorded fixtures, not exhaustive proof of all possible
inputs, clock dates or third-party subclasses. Independent review and PR
integration remain pending. This evidence alone does not close the assignment's
individual contribution, delivery-count or review obligations.