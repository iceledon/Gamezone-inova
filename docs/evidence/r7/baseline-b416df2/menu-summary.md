# R7 baseline menu execution summary

Source reference: b416df2 (full revision in reference.txt).
Evidence reviewed on 2026-10-23, America/Bogota.

All 30 submenu operations were exercised across the seven module scripts.
Each script ran twice against independent copies of the same module input data.
This is baseline evidence before GRASP corrections, not a post-refactor result.

| Evidence directory | Scenario IDs | Both exit codes | stdout comparison | Resulting data comparison | stderr |
|---|---|---|---|---|---|
| products | R7-01 through R7-03 | 0 | Identical | Identical | Empty |
| people | R7-04 through R7-06 | 0 | Identical | Identical | Empty |
| sales | R7-07 through R7-10 | 0 | Identical | Identical | Empty |
| returns | R7-11 through R7-15 | 0 | Identical | Identical | Empty |
| warranties | R7-16 through R7-19 | 0 | Identical | Identical | Empty |
| promotions | R7-20 through R7-24 | 0 | Identical | Identical | Empty |
| accessories | R7-25 through R7-30 | 0 | Identical | Identical | Empty |

Each directory contains input.txt and run-1/run-2 evidence. Products and people
start from initial-data. Sales combine initial-data with the recorded product
and customer registrations. Returns and warranties start from sales/run-1/data;
warranties therefore exercise the state before the return. Promotions and
accessories start from initial-data. Preserve these dependencies when replaying.

Observed examples: V003 total 150550.00 includes an extended-warranty charge
of 50.00. D002 refunds 150550.00 and the October 2026 net balance is 0.00.
Individual warranty lookup returns the basic warranty; the full listing includes
both basic and extended warranties. These observations describe the baseline.

## Remaining work and comparison limits

- Additional scenarios R7-E01 through R7-E11 still require their own evidence;
  incidental navigation/reloading in menu runs does not complete those cases.
- The before diagram and post-refactor comparisons remain pending.
- Current evidence does not establish preservation of behavior after corrections.
- The runs depend on the current date, locale, Java runtime and output encoding.
  Record/control those when replaying. Do not silently normalize other changes.
- Keep raw output, including existing accented-character encoding artifacts.
- javac compilation is recorded in compilation.txt; it is not a Maven test run.