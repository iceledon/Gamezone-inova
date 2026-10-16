# Exception Analysis - Requirement 6

Requirement 6 introduces a custom exception hierarchy and dedicated
validators into the existing GameZone Inova application.

The existing user interface is named ConsoleUI. It implements the
ConsoleMenu responsibility described in the requirement. The existing
menus and operations will be preserved.

## 1. Should GameZoneException be checked or unchecked?

GameZoneException will be an abstract class extending RuntimeException,
as explicitly required by Requirement 6.

It is therefore an unchecked exception. Service and repository methods
are not required to declare throws GameZoneException, and intermediate
callers do not need to catch and rethrow it.

This allows the application to preserve its existing method signatures
while communicating failures through specific exception types.

Unchecked exceptions still require deliberate handling. ConsoleUI will
handle operation failures, and Main will handle failures during initial
data loading.

Repositories will catch IOException internally and wrap it in
PersistenceException, preserving the original cause.

## 2. Why use a hierarchy with a common root?

IllegalArgumentException does not distinguish invalid input from a
missing resource, a business rule violation or a storage failure.

The new hierarchy defines four categories:

- ResourceNotFoundException: a required resource does not exist.
- BusinessRuleException: an operation violates a business rule.
- InvalidDataException: input has an invalid format, type or range.
- PersistenceException: reading or writing stored data fails.

The common GameZoneException root provides shared error metadata,
including an error code.

This design supports separation of concerns and single responsibility.
Validators detect invalid data and rules, services coordinate business
operations, repositories access files, and the user interface presents
appropriate messages.

Specific exception types allow the interface to identify failures
without interpreting message strings.

## 3. Where are validators invoked?

Services invoke validators before modifying application state or
persisting an operation.

When a validation requires existing information, the service obtains
that information first and passes the values or model objects to the
validator.

For example, a service passes the existing people list to
validateUniquePerson rather than allowing the validator to read a file.

The relevant dependency paths are:

- ui -> service -> validation -> model
- ui -> service -> persistence -> model

The exceptions package is transversal and can be used by these layers.

Validators must not invoke services or repositories, and must not
read or write files.

## 4. How will existing behavior be preserved?

Before refactoring services and repositories, the team will record a
baseline for the ten original menu operations using valid inputs.

After integration, the team will repeat those operations with the same
initial data and compare their functional results and persisted records.

Tests will run with copies of the data to avoid changing the working
dataset or losing existing information.

Additional checks will cover the integrated accessory, promotion,
warranty and return modules.

Error checks will include missing resources, insufficient stock,
duplicate identifiers, invalid input and persistence failures.

For rejected operations, the team will inspect whether application
state or stored records changed unexpectedly.

Any new rejection explicitly required by Requirement 6 will be
documented separately from regressions in previously valid operations.

Evidence will include execution commands, console output, data
comparisons and a regression report containing actual results.

Compilation and application startup have been checked. Full regression
verification remains pending.

## 5. How should the interface catch exceptions?

The interface will use specific catch blocks for the four custom
exception types at the operation-handling boundary.

Each category will produce a different Spanish message:

- Missing resource: "No se encontro el recurso solicitado: ..."
- Business rule: "No se puede completar la operacion: ..."
- Invalid input: "Los datos ingresados no son validos: ..."
- Persistence: "Ocurrio un problema al acceder a los datos del sistema.
  Contacte al administrador."

Persistence messages will not expose file paths, technical causes or
stack traces to the user.

Numeric and date parsing failures must also receive clear feedback.

Specific catch blocks must appear before general catch blocks.
A shared presentation method can prevent duplicated message handling
across the existing ConsoleUI operations.

Compatibility with exceptions used by unchanged integrated modules
will be reviewed before removing any existing handlers.

## Implementation responsibilities

- Isabel: analysis, class diagram, ConsoleUI error handling, startup
  protection in Main, README updates and regression evidence.
- Jaime: custom exceptions and dedicated validators.
- Nicoll: service and repository refactoring.