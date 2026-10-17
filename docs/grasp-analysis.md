# GRASP Analysis — Requirement 7

Status: team discussion pending.
This document currently contains Developer 2's proposed response
to question 4. The other assigned responses remain pending.

## 4. Non-regression strategy

Author: Nicoll Jhoana Gómez Siosi.

Before refactoring, record the agreed baseline commit and execute
every menu operation. Preserve the initial data files, exact inputs,
execution date, operation order, console output and resulting files.

The verification matrix must cover:

| Area | Operations |
| --- | --- |
| Products | Register video game, register console, list products. |
| People | Register customer, list customers, list sellers. |
| Sales | Register sale; view complete history and history by customer or seller. |
| Accessories | Register controller, cable and memory; list all, filter by type and find compatible accessories. |
| Promotions | Register percentage, category and bulk discounts; list all and active promotions. |
| Returns | Register return; list all, filter by customer or sale and calculate monthly balance. |
| Warranties | Find by product and sale; list all, active and expiring warranties. |
| Navigation and errors | Return from submenus, exit, invalid options, invalid input, business failures and persistence failures. |

For each case, record its identifier, initial data, exact inputs,
observed baseline result, files affected and evidence location.
Expected results must come from the baseline execution.

After each correction, restore an independent copy of the same
initial data and repeat the affected cases. Compare messages,
amounts, stock, ordering, filters and file contents. Restart the
application to verify that persisted data reloads consistently.

Control or document dates for promotion validity, the return period,
warranty expiration and monthly reports. Preserve the first promotion
encountered when equal positive discounts are compared.

Repeat the complete matrix after integration. Record baseline and
final results in docs/grasp-refactor-log.md. Resolve regressions
before merging.

Existing functional defects must be documented separately.
Do not change their visible behavior as an undocumented GRASP fix.

This section proposes a strategy; it does not claim that these
verification cases have already been executed.