# Additional baseline execution

Source code: b416df2, compiled baseline classes. Executed 2026-10-23.
Runner: ../run-additional-baseline.ps1. All runs use isolated data copies.

Executed twice: E01 missing customer/seller/item; E02 repeated item exceeding
stock; E03 invalid decimal/integer/date; E04 duplicate customer/empty sale;
E10 restart from persisted sale/return/warranty data; E11 invalid options,
return navigation in seven submenus and exit.

Each process exited 0; stderr was empty. stdout and resulting data were
identical between both runs. Observed messages match each scenario and E10
reload displays V003 and D002. This is baseline behavior, not refactor validation.

The remaining groups were subsequently executed with run-special-baseline.ps1; see special-summary.md for exact coverage and the expected startup exit code.
E10 establishes these recorded fixtures reload; it does not prove every
historical format or serialization edge case.