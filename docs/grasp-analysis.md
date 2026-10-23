# GRASP Analysis — Requirement 7

Status: working draft for team review. All five guiding questions have responses; final approval remains pending.

## 1. Criteria for GRASP assessments

Author responsible for review: Isabel Sofía Celedon Martinez.

Status: AI-assisted draft; personal review and team confirmation pending.

### Violation

We classify a responsibility assignment as a violation when
concrete code evidence shows that it conflicts with a GRASP
principle and produces an identifiable design consequence.

A large class, several dependencies, a getter call or an instanceof
check is not sufficient evidence by itself. The assessment must
explain why the responsibility is misplaced and how this affects
maintenance, collaboration or a foreseeable change.

Each finding must include a proposed correction that applies the
relevant pattern while preserving existing behavior.

### Correct

We classify a pattern as correctly applied when a specific class,
method and collaboration demonstrate its intended responsibility
assignment.

The explanation must identify what the implementation does and
why that distribution is appropriate. The absence of a reported
violation does not automatically establish a correct verdict.

Evidence limited to one method must not be presented as a verdict
for an entire module that has not been reviewed.

### Not applicable

We use not applicable when the reviewed code has no relevant
application point for the pattern.

The report must explain that absence in the context of the class
or methods reviewed. This verdict must not replace an incomplete
review or uncertainty about the pattern.

### Required evidence

Every assessment must identify:

- The audited commit.
- The class and method.
- A fixed source link and relevant lines or code excerpt.
- The pattern being evaluated.
- The explanation connecting the code to the pattern.

A violation must additionally identify:

- A local finding identifier.
- A concrete consequence.
- The proposed solution and pattern applied.
- The responsible contributor or contributors.
- Any dependency on another contributor's code.

Observations, candidate findings and peer-confirmed findings must
be distinguished explicitly.

### Overlapping patterns

When the same fragment affects several patterns, we record one
primary finding and identify the related patterns in its explanation.

The primary pattern is selected according to the responsibility
problem that guides the proposed correction.

The consolidated audit maps individual identifiers to unique
team identifiers and unifies duplicate reports without losing
their source evidence or contributor responsibilities.

### Functional defects and scope

Incorrect results, missing input restrictions or inconsistent
failure recovery must be distinguished from design violations.

A functional defect is recorded separately when correcting it
would change the behavior that this refactor must preserve.

No candidate finding authorizes a code change until the team
confirms its classification, scope, ownership and correction order.

## 2. Boundary findings and shared responsibilities

Author responsible for review: Isabel Sofía Celedon Martinez.

Status: AI-assisted draft; personal review and team confirmation pending.

### Identified boundary case

The leader's report records local finding L-V01 in
ProductService.restoreStock.

The method reads the product quantity, calculates the increased
value, updates Product through its setter and saves the inventory.

The proposed responsibility change crosses two contributors:

- Isabel is responsible for the restoreStock contribution.
- Jaime is responsible for the Product-side contribution.

The proposal is to encapsulate the quantity increase in Product
while retaining lookup and persistence coordination in ProductService.

Consolidated V-01 records Jaime's confirmation and Nicoll's accessory-consumer acceptance in report section 11. Final team review remains pending.

### Recording and consolidation

The finding is documented in leader-audit.md with its source
evidence, explanation, consequence and proposed responsibility split.

Jaime must compare it with his own report. If both reports describe
the same problem, the consolidated audit will register one definitive
finding and reference both sources.

Local identifiers must be mapped to unique consolidated identifiers.

### Proposed correction order

If the finding is confirmed:

1. Jaime provides the agreed Product operation.
2. Isabel changes restoreStock to delegate to that operation.
3. The affected operations are compared with the recorded baseline.

The producer change must be available before its consumer changes.

No Java correction is performed during the audit phase.

### Distinction from functional concerns

Nicoll's PR #34 separately reports concerns involving invalid
amounts, integer overflow and state after persistence failures.

These concerns must not be merged automatically into L-V01.
Sharing a method does not make two reports the same finding.

New input restrictions or different failure recovery may change
observable behavior and therefore require a separate scope decision.

The consolidated audit must distinguish responsibility refactoring
from functional corrections.

### Current status

V-01 identifies Jaime as the Product operation producer and Isabel and Nicoll as consumer owners. Precise contracts and final approval remain pending.

The remaining cross-owner dependencies must be checked before
claiming that all boundary cases have been covered.


## 3. Related patterns and avoiding duplicate findings

Responsible for review: Isabel Sofia Celedon Martinez with the team.

Status: AI-assisted synthesis; final team review pending.

The audit identifies responsibility problems that affect several GRASP
patterns. Each problem receives one consolidated finding ID, a primary
pattern and related patterns. Related verdicts reference the same ID
instead of creating duplicate corrections.

### V-02: Information Expert and Low Coupling

Return.discountRatio reads Sale's subtotal and discount amount and
interprets them to calculate a ratio. Sale owns the required information.
Return also becomes coupled to the details of that calculation.

Information Expert guides the correction: Isabel provides
Sale.calculateDiscountRatio(), then Jaime delegates from Return.
Low Coupling is a related benefit, not a second finding. Preserve the
subtotal <= 0 guard and the existing discountAmount / subtotal expression.

### V-05: Low Coupling and Indirection

ReturnRepository depends on service objects to reconstruct return records.
This couples persistence to the application-service layer and exposes a
collaboration boundary that requires an agreed resolution mechanism.

Low Coupling guides the correction: Nicoll removes the service dependency
using agreed model-data inputs; Isabel coordinates Main integration.
Indirection references V-05 because it describes the same boundary problem.
Exact constructor and loading contracts still require agreement.

### V-03: Polymorphism and Protected Variations

CategoryDiscount.matchesTargetCategory branches on product types.
The same branches expose promotion matching to category changes.

Polymorphism guides the correction: Jaime provides the category operation
on Product and its concrete types. Protected Variations is related to
isolating category changes. Both verdicts reference V-03 and preserve the
existing matching and fallback behavior.

### Consolidation rule

Retain one team ID, source-report identifiers, fixed baseline evidence,
the primary and related patterns, owners and one coordinated correction.
Choose the primary pattern from the misplaced responsibility and proposed
change. Link the same verification evidence from the related verdicts.

These proposals do not establish final team approval or implementation.

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


## 5. Correction order and shared-branch coordination

Author responsible for review: Isabel Sofía Celedon Martinez.

Status: AI-assisted draft; personal review and team confirmation pending.

### Starting condition

The correction phase starts only after the consolidated audit
has been reviewed and merged into develop.

The consolidated audit must identify each confirmed finding,
its owner, the agreed solution and its dependencies.

Candidate findings do not authorize implementation.

### Dependency-based order

Corrections follow their dependencies rather than a fixed order
based only on contributor names.

When one correction provides an operation required by another,
the producer change must be available before the consumer changes.

For L-V01, if confirmed, the proposed sequence is:

1. Jaime provides the agreed operation in Product.
2. Isabel updates ProductService.restoreStock to invoke it.
3. The affected behavior is compared with the baseline.

Other findings may require a different sequence. The final order
must be recorded in the consolidated audit before implementation.

### Shared-branch coordination

The team uses feature/grasp-audit for audit documentation and
feature/grasp-refactor for confirmed design corrections.

Work is organized in coordinated turns. Each contributor checks
the branch state and pending commits before publishing.

A pull request includes all unmerged changes in its source branch,
not only the changes of its author. Therefore contributors must
coordinate deliveries to avoid unintentionally combining reviews.

Each delivery requires review by someone other than its author.
Merge commits preserve the shared history. Force pushes and
direct commits to develop or main are not used.

### Traceability and verification

Each correction references its consolidated finding identifier.

The refactor log records the affected classes, responsibility
change, applied pattern, contributor, commit, pull request and
verification evidence.

Before correcting code, the team records a baseline covering
all existing menu operations and relevant error scenarios.

After each correction, the affected scenarios are repeated
with identical initial data. Final integration repeats the
complete agreed verification matrix.

Unexpected changes to messages, calculations, stock, ordering
or stored records must be investigated before merging.

### Current status

The consolidation proposes V-01 through V-08 and their dependency order. Nicoll has recorded her owner decisions. Exact shared contracts, final team approval and the executed phase-two baseline remain pending. V-02 requires Isabel to provide Sale.calculateDiscountRatio before Jaime changes Return.

The audit and refactor branches will be retained until the team
clarifies the assignment's conflicting branch-retention instructions.