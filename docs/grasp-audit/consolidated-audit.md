# Consolidated GRASP Audit

## Status

Technical consolidation prepared for peer review.
This is a proposed decision record, not a claim of team approval.

This document does not authorize source-code corrections.

## Audited reference

Repository: https://github.com/iceledon/Gamezone-inova

Reference commit:
701255d4209e070146c32ee5a663b08d3b4b0bb4

Audit branch: feature/grasp-audit.

Reports must identify this reference or document the relevant
differences from their original baseline.

## 1. Source reports

| Report | Contributor | Consolidation status |
|---|---|---|
| developer1-audit.md | Jaime Granados | Updated reference reported; evidence validation pending |
| developer2-audit.md | Nicoll Gómez | Review and reference reconciliation pending |
| leader-audit.md | Isabel Celedon Martinez | Draft assessments published; peer validation pending |

Additional source:

- PR #34: Nicoll's validation and exception review.

The additional review does not replace the individual GRASP report.

## 2. Coverage

The audited commit contains 43 Java source files.

The table maps those classes to report responsibilities.
It does not certify complete method-level assessment or peer approval.

| Classes | Count | Primary report / responsibility |
|---|---|---|
| Product, VideoGame, Console | 3 | Jaime |
| Accessory, Controller, Cable, Memory | 4 | Jaime |
| Promotion, PercentageDiscount, CategoryDiscount, BulkPurchaseDiscount | 4 | Jaime |
| Warranty, BasicWarranty, ExtendedWarranty | 3 | Jaime |
| Return | 1 | Jaime |
| Person, Customer, Seller | 3 | Nicoll |
| Sale | 1 | Isabel, with Jaime's contributions referenced |
| ProductRepository | 1 | Jaime; identify Nicoll's exception integration |
| PersonRepository | 1 | Nicoll |
| SaleRepository | 1 | Isabel; identify Nicoll's exception integration |
| AccessoryRepository, PromotionRepository, ReturnRepository, WarrantyRepository | 4 | Nicoll |
| ProductService | 1 | Jaime for product management; Isabel for restoreStock and her registration correction; Nicoll for typed-validation integration |
| PersonService | 1 | Nicoll |
| SaleService | 1 | Isabel, with Nicoll's validation integration identified |
| AccessoryService, PromotionService, ReturnService, WarrantyService | 4 | Nicoll |
| ConsoleUI | 1 | Isabel, with module contributions from Jaime and Nicoll identified |
| Main | 1 | Isabel, with Nicoll's integration contributions identified |
| GameZoneException, BusinessRuleException, InvalidDataException, PersistenceException, ResourceNotFoundException | 5 | Jaime |
| ProductValidator, PersonValidator | 2 | Jaime |
| SaleValidator | 1 | Jaime for original methods; Nicoll for validateSaleProducts and its integration |
| Total | 43 | All source files mapped; detailed validation remains pending |

### Shared-method allocation

- Isabel provides the primary assessment of Sale.calculateSubtotal
  and Sale.calculateTotal.
- Jaime documents his receipt and discount-field contributions.
- ProductService.restoreStock belongs to Isabel's reviewed contribution.
- Nicoll's typed-validation and exception integration must be distinguished
  from the original implementation of shared classes.
- Sale.canBeReturned has an ownership discrepancy documented in
  Jaime's report. Its primary assessment responsibility must be
  resolved explicitly before closing coverage.

### Reference reconciliation

Jaime's updated report uses the agreed reference:

701255d4209e070146c32ee5a663b08d3b4b0bb4

Isabel's report uses the same reference.

Nicoll's main GRASP report still declares:

3dff9c12c7c523bd8658e448eeb236b11ec94818

Her separate validation review uses 701255d, but does not automatically
update the baseline or coverage of her main GRASP report.

Nicoll must reconcile the relevant differences and record the result.
Changing only the hash without reviewing those differences is insufficient.

### Coverage status

Class inventory mapping: completed.

Method-level coverage validation: pending.

Shared-method responsibility reconciliation: partially completed.

Peer approval of the three individual reports: pending.

## 3. Consolidated assessment proposed for review

The following conclusions consolidate existing report evidence.
They remain subject to peer approval of this delivery. Confirmation of one
finding must not be presented as approval of the complete consolidation.

| Pattern | Proposed verdict | Evidence / source |
|---|---|---|
| Information Expert | violation | V-01 stock restoration; V-02 sale discount ratio |
| Creator | correct | SaleService and repositories have initialization data; see the three individual reports |
| Controller | correct | UI interaction delegates application operations to services; leader section 3 and developer reports |
| Low Coupling | violation | V-05 ReturnRepository depends on services |
| High Cohesion | violation | V-08 ConsoleUI, confirmed by Jaime; V-07 reporting extraction remains a candidate |
| Polymorphism | violation | V-03 category matching; V-06 accessory type classification remains a candidate |
| Pure Fabrication | correct | Dedicated repositories and validators; this does not negate V-05's dependency problem |
| Indirection | violation | Same boundary problem as V-05; related pattern, not an additional finding |
| Protected Variations | violation | V-04 return-policy/date dependency; related effects of V-03 and V-06 are not counted again |

## 4. Identifier mapping and status

Consolidated IDs below are reserved for traceability. An ID does not imply
that an unconfirmed candidate has been accepted.

| Consolidated ID | Original source | Review status |
|---|---|---|
| V-01 | Isabel L-V01; Jaime section 12 / Boundary finding L-V01 | Jaime and Isabel confirmed the Product/ProductService split; accessory extension needs Nicoll's confirmation |
| V-02 | Jaime V-01: Return.discountRatio | Reported with evidence; proposed assignment below needs peer review |
| V-03 | Jaime V-02: CategoryDiscount.matchesTargetCategory | Reported with evidence; proposed correction needs peer review |
| V-04 | Jaime V-03: Sale.canBeReturned | Reported with evidence; ownership resolved below as a proposed coordination decision |
| V-05 | Nicoll V-02: ReturnRepository service dependency | Evidence checked in the common baseline; peer confirmation remains pending |
| V-06 | Nicoll candidate V-01: AccessoryService.typeOf | Candidate, requires Nicoll/Jaime confirmation |
| V-07 | Nicoll candidate V-03: monthly reporting in ReturnService | Candidate, requires Nicoll confirmation |
| V-08 | Isabel L-V02; Jaime Boundary finding L-V02 | Jaime confirmed High Cohesion concern and shared input/error helper requirement; complete cross-owner scope needs Nicoll review |

Original report identifiers are retained in source documents and cross-referenced
here. Do not rename individual IDs in a way that loses their provenance.

## 5. Actionable correction assignments proposed by the lead

These assignments turn the reported findings into reviewable work. They must be
approved with the consolidated audit before source changes begin.

### V-01 — Encapsulate stock increase

- Primary pattern: Information Expert.
- Evidence: ProductService.restoreStock and AccessoryService.restoreStock.
- Explanation: the service reads quantity, performs its addition and sets it back.
  Product owns that state and already provides decreaseStock.
- Consequence: callers reproduce the stock-increase calculation.
- Producer: Jaime adds Product.increaseStock(int).
- Consumer: Isabel delegates ProductService.restoreStock to that operation.
- Proposed additional consumer: Nicoll delegates AccessoryService.restoreStock,
  subject to her explicit confirmation of that extension.
- Constraint: preserve accepted amounts, integer arithmetic, setter behavior,
  persistence timing and messages. No new negative-amount, overflow or rollback fix.
- Order: producer first, then each owner changes their consumer.

### V-02 — Move sale discount ratio to its information owner

- Primary pattern: Information Expert; related pattern: Low Coupling.
- Evidence: Return.discountRatio obtains Sale's subtotal and discount amount.
- Consequence: Return contains the interpretation of data owned by Sale.
- Producer assignment: Isabel adds Sale.calculateDiscountRatio().
- Consumer assignment: Jaime makes Return delegate its ratio calculation to Sale.
- Preserve the exact guard: subtotal <= 0 produces 0.0; otherwise return the
  existing discountAmount / subtotal expression. Preserve refunds and receipts.
- This is an explicit cross-owner assignment for review, not a statement that
  Isabel authored all existing discount fields.
- Order: Isabel's method must be available before Jaime changes Return.

### V-03 — Delegate category identity to product types

- Primary pattern: Polymorphism; related pattern: Protected Variations.
- Evidence: CategoryDiscount.matchesTargetCategory contains type-specific branches.
- Consequence: a change to supported product categories can require modifying
  discount matching rather than obtaining the category from the product.
- Owner: Jaime updates Product, VideoGame, Console, Accessory and CategoryDiscount.
- Proposed operation: getCategory(), with existing labels VIDEOGAME, CONSOLE
  and ACCESSORY. Accessory subclasses inherit the accessory category.
- Preserve current null handling, case sensitivity, unsupported-category results
  and monetary arithmetic. Do not use the change to modify CSV formats.
- No automatic changes to console warranty checks: the leader separately assessed
  their current responsibility as acceptable.

### V-04 — Make return-date evaluation explicit without changing policy

- Primary pattern: Protected Variations.
- Evidence: Sale.canBeReturned reads LocalDate.now and embeds the 30-day period.
- Consequence: evaluating the same rule for a chosen date is difficult and the
  return-window value has no explicit name.
- Proposed owner: Nicoll, based on the original canBeReturned contribution
  identified in Jaime's report; Isabel reviews the change in her Sale class.
- Proposed change: named 30-day constant and canBeReturned(LocalDate referenceDate).
  The existing no-argument method delegates using LocalDate.now().
- Preserve inclusive boundary, existing treatment of future dates and the exact
  ReturnService message. Do not change the policy to a different number of days.
- Ownership needs Nicoll's acknowledgment before implementation.

### V-05 — Remove persistence-to-service dependency in returns

- Primary pattern: Low Coupling; related pattern: Indirection.
- Evidence: ReturnRepository imports/stores SaleService, ProductService and
  AccessoryService; fromLine resolves references through those services.
- Consequence: persistence reconstruction is coupled to service contracts.
- Owner: Nicoll changes ReturnRepository and ReturnService loading coordination.
- Proposed solution: repository loading receives already-loaded model collections.
  Resolve identifiers inside persistence without service fields or service calls.
- Integration owner: Isabel adjusts Main only after the new contracts exist.
- Preserve file layout, row order, product-first lookup, handling of missing items,
  warrantyRefund defaults, recalculated refund behavior and failure messages/types.
- Do not improve historical-price or refund persistence behavior in this refactor.
- Order: agree the repository/service contracts, implement them in a compiling
  delivery, then adjust Main in the coordinated integration delivery.

### V-06 — Accessory type classification (candidate)

- Primary pattern: Polymorphism; related pattern: Protected Variations.
- Evidence: AccessoryService.typeOf explicitly tests Controller, Cable and Memory.
- Proposed producer: Jaime provides a type-query contract in the accessory hierarchy.
- Proposed consumer: Nicoll delegates classification to the accessory.
- Preserve CONTROLLER/CABLE/MEMORY labels, case-insensitive filtering and the
  current fallback for unrecognized subtypes. Do not force a new null policy.
- Distinct from V-03: category identity and accessory subtype labels are different
  concepts and must not be collapsed into one incompatible method.
- Status: requires producer/consumer confirmation before entering approved scope.

### V-07 — Monthly reporting responsibility (candidate)

- Primary pattern: High Cohesion.
- Evidence: ReturnService handles return registration and queries as well as
  calculateMonthlySales, calculateMonthlyReturns and generateMonthlyBalance.
- Proposed owner: Nicoll evaluates/extracts a focused reporting collaborator.
- Keep existing ReturnService public entry points as delegating compatibility
  methods if needed so that callers are not forced to change together.
- Preserve date filtering, totals, iteration behavior, numerical arithmetic and
  exact monthly balance presentation. This is not a financial-rules correction.
- Status: Nicoll must confirm the finding or document why she rejects it.

### V-08 — Focus module-specific console interaction

- Primary pattern: High Cohesion.
- Evidence: ConsoleUI owns seven module submenus and their interaction methods;
  Jaime's report identifies independent promotion/accessory history changes.
- Consequence: independent module interaction changes share the same class.
- Integration owner: Isabel retains main navigation and coordinates UI extraction.
- Jaime owns/reviews extraction of his promotion and accessory interaction methods.
- Nicoll owns/reviews her contributed interaction methods, confirmed by blame.
- Each owner must agree exact method lists before moving another person's code.
- Share ask, askInt, askDouble, askDate and showError through a common UI helper;
  do not duplicate parsing or error presentation across extracted classes.
- Preserve all prompts, options, navigation loops, output order, exception handling,
  optional-module constructor behavior and input consumption.
- Order: agree the UI helper contract; provide it before dependent menu classes;
  integrate navigation only when those classes are available.
- Jaime confirmed the concern; Nicoll's method-level participation needs review.

### Fixed-source evidence index

All links below use the common audited commit. Detailed excerpts remain in the
individual source findings; the owning report must retain their precise context.

- [service/ProductService.java](https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/service/ProductService.java)
- [service/AccessoryService.java](https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/service/AccessoryService.java)
- [model/Return.java](https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/model/Return.java)
- [model/Sale.java](https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/model/Sale.java)
- [model/CategoryDiscount.java](https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/model/CategoryDiscount.java)
- [persistence/ReturnRepository.java](https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/persistence/ReturnRepository.java)
- [service/ReturnService.java](https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/service/ReturnService.java)
- [ui/ConsoleUI.java](https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/ui/ConsoleUI.java)
- [Main.java](https://github.com/iceledon/Gamezone-inova/blob/701255d4209e070146c32ee5a663b08d3b4b0bb4/src/main/java/com/gamezone/Main.java)

## 6. Owner work queues and dependency order

| Stage | Contributor | Work |
|---|---|---|
| 0 | Isabel with both reviewers | Approve audit scope, decisions, contracts and remaining owner confirmations |
| 1 | Isabel | Create phase-two baseline and before diagram on feature/grasp-refactor; provide Sale.calculateDiscountRatio for V-02 |
| 2 | Jaime | Product.increaseStock for V-01; category operation for V-03; Return delegation after V-02 producer; accessory subtype query only if V-06 accepted |
| 3 | Nicoll | Accessory stock delegation if accepted; V-04/V-05 after acknowledgment; V-06/V-07 only if confirmed |
| 4 | Isabel and module owners | ProductService.restoreStock delegation; Main integration; coordinated V-08 extraction |
| 5 | All; Isabel coordinates | Full regression, after diagram, traceability log, documentation and reviewed release |

The ordering supersedes a simple developer-name order: V-02 needs an Isabel
producer before Jaime's consumer. Record this in grasp-analysis question 5.
Every delivery must compile. Coordinate any compatibility adapter needed to
avoid an intermediate broken constructor or method signature.

No repository-wide rewrite is authorized. Each change must cite one of the
accepted IDs and its responsible contributor(s).

## 7. Functional concerns excluded from behavior-preserving corrections

PR #34 reports invalid stock amounts, overflow, partial state after persistence
failures and non-finite stored monetary values. These are separate from the
responsibility-placement findings above.

Do not introduce new validation, different messages, new storage layouts,
refund recalculation rules or transaction/compensation behavior silently in R7.
Record verified functional defects for a separately agreed change. This does
not claim every reported concern has been reproduced by Isabel.

## 8. Evidence validation and baseline reconciliation

- Class inventory: 43 Java source files mapped in section 2.
- Individual source findings and their responsibility proposals were inspected.
- The common reference is 701255d4209e070146c32ee5a663b08d3b4b0bb4.
- Jaime updated his report to that reference.
- Nicoll's original report uses 3dff9c1. The src differences from 3dff9c1 to
  701255d are ConsoleUI and SaleValidator. Her existing service/repository
  evidence must be checked at the common reference, and she must acknowledge
  the updated validation/UI coverage. Her supplemental PR #34 is not a
  replacement for this acknowledgment.
- L-V01 and L-V02 have supporting confirmation from Jaime in PR #31.
- Full contributor and team approvals are NOT inferred from these confirmations.

## 9. Phase-two baseline and release gate

Before the first correction, create feature/grasp-refactor from approved develop.
Use docs/evidence/r7/ rather than overwriting R6 evidence. Record the exact source
revision, initial data, execution date, scripted inputs, output and resulting data.

Cover all operations in the seven menu areas, existing errors, return navigation,
exit and restart. Run the initial baseline twice to identify nondeterminism.
Use disposable working directories rather than resetting the project data.

Each delivery compares affected scenarios; final integration repeats all cases.
Track every accepted ID in docs/grasp-refactor-log.md and diagrams before/after.
Maven verification must be completed or explicitly recorded as unavailable;
a javac check must not be misreported as a Maven build.

## 10. Peer approval checklist

- [ ] Nicoll acknowledges the common reference and validation/UI coverage.
- [ ] Nicoll confirms or rejects V-06 and V-07 with justification.
- [ ] Nicoll acknowledges V-04 ownership and V-05 solution contract.
- [ ] Nicoll confirms the accessory consumer of V-01, or it remains excluded.
- [ ] UI contributors confirm V-08 method ownership and shared helper contract.
- [ ] Reviewers accept the definitive IDs, decisions and dependency order.
- [ ] Relevant individual reports and grasp-analysis are aligned with the decisions.
- [ ] Individual audit deliveries and consolidated delivery receive independent review.
- [ ] Audit phase is merged before baseline/refactor implementation begins.

The technical lead's proposed assignments are now concrete, but this checklist
must be completed with real reviewer responses. Do not mark it complete merely
to begin Requirement 8. Requirement 8's exact entry conditions require its plan.
