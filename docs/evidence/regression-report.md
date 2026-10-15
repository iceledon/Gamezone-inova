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




- Duplicate customer identifier.
- Sale without products.
- Persistence failure during an operation.

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
## Numeric and date input verification

Both scenarios were executed interactively.

| Scenario | Result |
|---|---|
| Enter `abc` at the video game price prompt | Invalid-data message displayed; product submenu remained usable |
| Enter `2026-99-99` at the promotion start-date prompt | Invalid-data message displayed; promotion submenu remained usable |

The user returned to the main menu after each failure and exited normally.
No stack trace was displayed.

Observed messages:

- "Los datos ingresados no son validos: El campo numero se esperaba un numero decimal valido.."
- "Los datos ingresados no son validos: El campo fecha se esperaba una fecha valida en formato aaaa-mm-dd.."

The messages contain duplicated final punctuation, which remains a
presentation detail to correct.

These checks verify error feedback and continued navigation. Persisted
data was not compared separately during these interactive executions.

## Stock and startup persistence verification

### Insufficient stock

An isolated copy of the data was configured with one unit of VG001.
A sale requested two units of that same product.

The application displayed:

"No se puede completar la operacion: Stock insuficiente para The Legend of Zelda Tears of the Kingdom. Disponible: 1, solicitado: 2."

The application allowed navigation to continue and exited normally.

Evidence: stock-error.txt.

The saved transcript confirms rejection and continued navigation.
The file-hash comparison result was printed separately by the test
script and is not included in this transcript.

### Corrupt product data during startup

An isolated products.txt contained a nonnumeric price.

The application displayed only:

"Ocurrio un problema al acceder a los datos del sistema. Contacte al administrador."

No technical exception trace appeared in the saved output.
The standard output file was empty.

Evidence:

- persistence-startup-stdout.txt
- persistence-startup-stderr.txt

The process exit code was checked by the test script but was not
saved in these output files.

### Scope

Both tests used copies of the data. The project's original data
directory was not modified by their setup.

Persistence failures during an ongoing operation, additional
integrated scenarios and remaining state-preservation checks
require separate verification.