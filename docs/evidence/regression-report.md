# Regression Report - Requirement 6

## Method

The baseline was reconstructed from pre-refactor revision `d423eb8`.
It was not captured before implementation began.

The baseline and current implementation were compiled with Java 17
compatibility and executed with the same scripted input.

Both executions started from identical data copied from the baseline
revision and ran in isolated working directories.

The project's original data directory was not modified.

Exact revision identifiers are recorded in:

- before-commit.txt
- after-commit.txt

## Evidence

- input-operations.txt: input used for both executions.
- before.txt: baseline console output.
- after.txt: current implementation console output.
- before-stderr.txt: baseline standard error output.
- after-stderr.txt: current standard error output.
- before-data/: baseline data after execution.
- after-data/: current data after execution.

## Original operation comparison

| # | Operation | Comparison result |
|---|---|---|
| 1 | Register video game | Matching console output and saved data |
| 2 | Register console | Matching console output and saved data |
| 3 | List products | Matching console output |
| 4 | Register customer | Matching console output and saved data |
| 5 | List customers | Matching console output |
| 6 | List sellers | Matching console output |
| 7 | Register sale | Matching console output and saved data |
| 8 | Full sales history | Matching console output |
| 9 | Sales by customer | Matching console output |
| 10 | Sales by seller | Matching console output |

Both executions completed successfully. The script also checked
successful registration messages for the video game, console,
customer and sale.

The following comparisons reported no differences:

- before.txt versus after.txt
- before-data/ versus after-data/

This demonstrates matching behavior for the valid inputs used
in this scenario. It does not establish exhaustive correctness.

## Verified error handling

A sale attempt with customer identifier `CLIENTE_INEXISTENTE_R6`
displayed:

"No se encontro el recurso solicitado: No existe una persona con el
identificador CLIENTE_INEXISTENTE_R6."

The application remained usable and allowed navigation back to
the main menu and normal exit.

This scenario was observed interactively. Its console transcript
has not yet been saved in this evidence directory.

## Pending verification

- Invalid numeric input.
- Invalid date input.
- Insufficient stock.
- Duplicate customer identifier.
- Sale without products.
- Persistence failure during an operation.
- Persistence failure during startup.
- State preservation after rejected operations.
- Additional integrated operations involving accessories,
  promotions, warranties and returns.

Tests involving damaged or inaccessible files must use isolated
copies of the data.

## Conclusion

The ten original operations produced matching outputs and persisted
data for the executed regression scenario.

Remaining error scenarios and integrated module checks must be
completed and documented before claiming full Requirement 6
verification.