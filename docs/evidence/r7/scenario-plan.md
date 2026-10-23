# R7 non-regression preparation

Status: scenario inventory prepared from the 30 existing submenu operations.
This is not executed test evidence and is not the approved phase-two baseline.
The formal baseline must be captured after the audit is accepted and before
source corrections, on the exact integrated develop revision.

## Execution protocol

1. Compile the approved baseline; record revision, date and tool commands.
2. Use disposable copies of the same initial data for every comparison.
3. Preserve exact input lines, stdout, stderr, exit code and resulting files.
4. Capture the baseline twice and identify actual nondeterminism.
5. After each correction repeat affected cases, then the complete matrix.
6. Keep R6 evidence intact. Store R7 evidence under docs/evidence/r7/.
7. Treat the actual baseline behavior as the reference; do not silently fix
   pre-existing functional defects during this refactor.

## Menu coverage inventory

Expected output and exact inputs must be recorded from actual baseline runs.
The file column lists expected access scope; verify it rather than assuming
that every operation writes every listed file.

| ID | Area | Operation | Files / access | Result |
|---|---|---|---|---|
| R7-01 | Products | Register video game | products.txt | Not run |
| R7-02 | Products | Register console | products.txt | Not run |
| R7-03 | Products | List products | Read only | Not run |
| R7-04 | People | Register customer | customers.txt | Not run |
| R7-05 | People | List customers | Read only | Not run |
| R7-06 | People | List sellers | Read only | Not run |
| R7-07 | Sales | Register sale with console, video game, accessory and optional extended warranty | sales.txt; products.txt; accessories.csv; warranties.csv | Not run |
| R7-08 | Sales | Full sales history | Read only | Not run |
| R7-09 | Sales | Sales by customer | Read only | Not run |
| R7-10 | Sales | Sales by seller | Read only | Not run |
| R7-11 | Returns | Register return | returns.csv; products.txt; accessories.csv; warranties.csv | Not run |
| R7-12 | Returns | All returns | Read only | Not run |
| R7-13 | Returns | Returns by customer | Read only | Not run |
| R7-14 | Returns | Returns by sale | Read only | Not run |
| R7-15 | Returns | Monthly sales, returns and net balance | Read only | Not run |
| R7-16 | Warranties | Find by product and sale | Read only | Not run |
| R7-17 | Warranties | List all warranties | Read only | Not run |
| R7-18 | Warranties | List active warranties | Read only | Not run |
| R7-19 | Warranties | List expiring warranties | Read only | Not run |
| R7-20 | Promotions | Register percentage discount | promotions.csv | Not run |
| R7-21 | Promotions | Register category discount | promotions.csv | Not run |
| R7-22 | Promotions | Register bulk discount | promotions.csv | Not run |
| R7-23 | Promotions | List all promotions | Read only | Not run |
| R7-24 | Promotions | List active promotions | Read only | Not run |
| R7-25 | Accessories | Register controller | accessories.csv | Not run |
| R7-26 | Accessories | Register cable | accessories.csv | Not run |
| R7-27 | Accessories | Register memory | accessories.csv | Not run |
| R7-28 | Accessories | List all accessories | Read only | Not run |
| R7-29 | Accessories | Filter accessories by type | Read only | Not run |
| R7-30 | Accessories | Find compatible accessories | Read only | Not run |

## Additional scenarios

| ID | Scenario | Purpose | Result |
|---|---|---|---|
| R7-E01 | Missing customer, seller or item | Preserve resource messages and navigation | Not run |
| R7-E02 | Repeated item IDs above available stock | Preserve quantity aggregation and rejection | Not run |
| R7-E03 | Invalid integer, decimal and date | Preserve parsing messages and input consumption | Not run |
| R7-E04 | Duplicate identifiers and empty sale | Preserve business-rule feedback | Not run |
| R7-E05 | Corrupt/read-only files in isolated directories | Preserve failure categories, messages and actual state effects | Not run |
| R7-E06 | Product-first lookup when identifiers overlap | Preserve catalog precedence | Not run |
| R7-E07 | Return on day 30 and day 31 | Preserve inclusive return boundary | Not run |
| R7-E08 | Two equal positive promotion discounts | Preserve existing tie selection and ordering | Not run |
| R7-E09 | Old sale/return records with optional fields absent | Preserve loading defaults and historical behavior | Not run |
| R7-E10 | Restart and reload after operations | Preserve record reconstruction | Not run |
| R7-E11 | Back from each submenu, invalid option and normal exit | Preserve menu loops | Not run |

## Correction coverage

| Finding | Scenarios to repeat |
|---|---|
| V-01 stock increase | Returns with products/accessories and inventory reload; existing amount/failure behavior |
| V-02 discount ratio | Discounted sales and partial returns, receipt/refund comparisons; zero-subtotal guard |
| V-03 product categories | Category promotion matching for existing product types; unknown/null category behavior |
| V-04 return date | Day 30/day 31, current no-argument behavior and existing future-date handling |
| V-05 return persistence | Reload, product-first lookup, missing references, old records, refund defaults and messages |
| V-06 accessory classification, if accepted | Type filters and exact labels, case handling and unknown subtype fallback |
| V-07 reporting extraction, if accepted | Monthly sales, returns and net; existing public delegation behavior |
| V-08 UI extraction | All menu cases and error/navigation cases, exact prompts and input consumption |

Do not claim these cases passed until their execution artifacts exist.
This plan does not establish that Requirement 8's entry conditions are met.
