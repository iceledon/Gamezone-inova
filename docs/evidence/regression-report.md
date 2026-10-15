# Regression Report - Requirement 6

## 1. Method

The baseline was reconstructed from pre-refactor revision `d423eb8`.
It was not captured before implementation began.

The baseline and refactored application were compiled with Java 17
compatibility and executed with the same scripted input.

Both executions started from identical data copied from the baseline
revision and ran in isolated working directories.

The project's original data directory was not modified by these
isolated tests.

The initial revision identifiers are recorded in:

- before-commit.txt
- after-commit.txt

## 2. Initial regression evidence

- input-operations.txt: input used for both executions.
- before.txt: baseline console output.
- after.txt: refactored console output.
- before-stderr.txt: baseline standard error output.
- after-stderr.txt: refactored standard error output.
- before-data/: baseline data after execution.
- after-data/: refactored data after execution.

## 3. Original operation comparison

| # | Operation | Initial comparison result |
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

Both initial executions completed successfully.

The script also checked successful registration messages for the
video game, console, customer and sale.

The initial comparisons reported no differences between:

- before.txt and after.txt.
- before-data/ and after-data/.

These results apply to the valid inputs used in this scenario.
They do not establish exhaustive correctness.

## 4. Missing-resource verification

A sale attempt with customer identifier `CLIENTE_INEXISTENTE_R6`
displayed:

> No se encontro el recurso solicitado: No existe una persona con el identificador CLIENTE_INEXISTENTE_R6.

The application remained usable and allowed navigation back to
the main menu and normal exit.

This scenario was observed interactively. Its console transcript
has not been saved separately in this evidence directory.

## 5. Numeric and date input verification

Both scenarios were executed interactively.

| Scenario | Observed result |
|---|---|
| Enter `abc` at the video game price prompt | Invalid-data message displayed; product submenu remained usable |
| Enter `2026-99-99` at the promotion start-date prompt | Invalid-data message displayed; promotion submenu remained usable |

Observed messages:

> Los datos ingresados no son validos: El campo numero se esperaba un numero decimal valido..

> Los datos ingresados no son validos: El campo fecha se esperaba una fecha valida en formato aaaa-mm-dd..

The user returned to the main menu after each failure and exited normally.
No stack trace was displayed.

The messages contain duplicated final punctuation, which remains a
presentation detail to correct.

These checks verify error feedback and continued navigation.
Persisted data was not compared separately during these interactive tests.

## 6. Insufficient-stock verification

An isolated data copy was configured with one unit of `VG001`.
A sale requested two units of that same product.

The application displayed:

> No se puede completar la operacion: Stock insuficiente para The Legend of Zelda Tears of the Kingdom. Disponible: 1, solicitado: 2.

The application allowed navigation to continue and exited normally.

Evidence:

- stock-error.txt

The saved transcript confirms rejection and continued navigation.
The file-hash comparison result was printed separately by the test
script and is not included in this transcript.

## 7. Corrupt product data during startup

An isolated products.txt contained a nonnumeric price.

The application displayed only:

> Ocurrio un problema al acceder a los datos del sistema. Contacte al administrador.

No technical exception trace appeared in the saved output.
The standard output file was empty.

Evidence:

- persistence-startup-stdout.txt
- persistence-startup-stderr.txt

The test script checked the process exit code, but that code was
not saved in these output files.

The project's original product file was not modified.

## 8. Persistence failure during product registration

A video game registration was attempted using an isolated data copy
with products.txt set to read-only.

### Initial finding

The initial test exposed an inconsistency: the rejected product
remained in memory although the file was unchanged.

### Correction

ProductService registration was corrected to save the proposed
inventory before adding the new product to the in-memory list.

This strategy was applied to video game and console registration.

### Repeated video game test

The repeated test produced:

- Exit code: 0.
- Expected persistence message displayed: True.
- Products file unchanged: True.
- Rejected product appears in subsequent inventory listing: False.

The menu remained usable and the application exited normally.

Evidence:

- persistence-write-stdout.txt
- persistence-write-stderr.txt
- persistence-write-result.txt

The project's original data was not modified.

The console registration failure scenario has not yet been tested
separately.

This correction does not establish atomic file writes or state
preservation for other operations such as sales or stock updates.

## 9. Regression repeated after the registration correction

The ten-operation scenario was repeated after correcting
ProductService registration.

Evidence directory:

- post-fix-20261015-170806/

This directory contains:

- output.txt
- stderr.txt
- result.txt
- commit.txt
- uncommitted-product-service.diff
- data/

The application compiled and completed with exit code 0.

Comparison with the baseline showed only the expected generated
sale-date change from `2026-10-14` to `2026-10-15`:

- Console receipts displayed the new execution date.
- The V003 record in sales.txt contained the new execution date.
- No other differences were reported in console output or saved data.

For this scenario, products, stock, identifiers and monetary amounts
matched the baseline.

The ten-operation regression after the registration correction is
completed. The earlier evidence has been preserved.

The original project data was not modified.

## 10. Remaining verification

- Duplicate customer identifier: record the actual execution result.
- Sale without products: record the actual execution result.
- Persistence failure during console registration.
- Persistence failures in other operations.
- State preservation for other rejected or failed operations.
- Additional integrated operations involving accessories,
  promotions, warranties and returns.
- Correct duplicated punctuation in invalid-input messages.

Tests involving damaged, inaccessible or read-only files must use
isolated copies of the data.

## 11. Result and limitations

The ten original operations were compared before and after the refactor.

After the product registration correction, the scenario was repeated.
Only the generated sale date differed because execution occurred
on a different day.

Observed error handling covered:

- A missing customer.
- Invalid numeric input.
- An invalid date.
- Insufficient stock.
- Corrupt product data during startup.
- A write failure during video game registration.

The write-failure test confirmed that the rejected video game no
longer remained in memory and that the product file was unchanged.

These results support the tested scenarios. They do not establish
complete coverage of every integrated operation or guarantee
transactional persistence across multiple files.