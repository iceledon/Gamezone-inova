# Technical Lead GRASP Audit

## Author

Isabel Sofía Celedon Martinez — Technical Lead.

## Baseline

Audited develop commit: 701255d4209e070146c32ee5a663b08d3b4b0bb4

Audit branch: feature/grasp-audit.

Status: in progress. Assessments and peer review are pending.

ConsoleUI implements the ConsoleMenu responsibility described
in the assignment.

## Scope

The initial scope follows TEAM.md and the R7 work plan.
Method ownership must also be checked against Git history.

| Area | Class or method | Review status |
|---|---|---|
| Sales model | Sale | Pending |
| Sales persistence | SaleRepository | Pending |
| Sales coordination | SaleService | Pending |
| Console interface | ConsoleUI | Pending |
| Application startup | Main | Pending |
| Stock restoration | ProductService.restoreStock | Pending |

Methods contributed by other team members will be identified
separately before assigning audit responsibility.

### Ownership evidence from Git history

The following observations come from recent file history.
They identify contributions, not exclusive ownership of each class.

| Class | Contributions observed |
|---|---|
| Sale | Isabel contributed extraCost and preservation of canBeReturned; Jaime contributed receipt generation and promotion-related changes |
| SaleRepository | Isabel implemented persistence and promotion storage; Nicoll migrated persistence exceptions |
| SaleService | Isabel contributed promotion/warranty orchestration; Nicoll integrated typed validations |
| ConsoleUI | Isabel contributed error handling, input validation and message corrections; Jaime and Nicoll also contributed specific changes |
| Main | Isabel contributed startup protection and integration changes; Nicoll contributed warranty and return wiring |
| ProductService | Jaime contributed product management; Isabel added restoreStock and corrected registration persistence handling; Nicoll integrated typed validations |

Recent commit history is not sufficient to assign every method.
Method-level attribution will be checked at the audited commit.

The leader will distinguish her own contributions from shared
responsibilities and report cross-owner dependencies separately.

### Confirmed method ownership: ProductService.restoreStock

At audited commit `701255d4209e070146c32ee5a663b08d3b4b0bb4`,
git blame attributes the executable lines of
ProductService.restoreStock to Isabel Sofía Celedon Martinez,
commit `71a76d4c`.

The method occupies lines 179–185.

Evidence:
https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/service/ProductService.java#L179-L185

This confirms method ownership; it does not establish a GRASP verdict.

## 1. Information Expert

Assessment and evidence pending.

## 2. Creator

Assessment and evidence pending.

## 3. Controller

Assessment and evidence pending.

## 4. Low Coupling

Assessment and evidence pending.

## 5. High Cohesion

Assessment and evidence pending.

## 6. Polymorphism

Assessment and evidence pending.

## 7. Pure Fabrication

Assessment and evidence pending.

## 8. Indirection

Assessment and evidence pending.

## 9. Protected Variations

Assessment and evidence pending.

## Boundary reports

Pending review and team discussion.

## Out-of-scope functional findings

Pending review.

## Peer review

Pending.