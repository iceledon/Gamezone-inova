# GRASP refactor implementation log

## 2026-10-23 - stock/type consumers and discount-ratio producer

Base revision: d2fee8f. Implementer: Isabel with substantive AI coding assistance.
Jaime's producer methods were already present before this delivery.

| Finding | Change | Status |
|---|---|---|
| V-01 | ProductService.restoreStock and AccessoryService.restoreStock delegate arithmetic to Product.increaseStock; persistence calls remain in place | Implemented; review pending |
| V-06 | AccessoryService.typeOf delegates to getAccessoryType for the existing concrete hierarchy | Implemented; review pending |
| V-02 | Sale.calculateDiscountRatio uses the original nonpositive-subtotal guard and discount/subtotal expression | Producer implemented; Jaime's Return consumer remains pending |

Validation: javac --release 17 succeeds. Replayed the seven menu scripts
covering 30 operations using their original per-module input-data fixtures.
All exit codes are zero; resulting data matches the original baseline.
Raw stdout differs from the user-captured historical files because of capture
encoding. A control replay of unchanged baseline classes under the same capture
method matches the current stdout byte-for-byte in all seven modules.
Original captures were retained. Evidence: evidence/r7/replay-stock-types-ratio.
A direct probe verifies zero-subtotal and 25-percent discount-ratio results.

Maven was not executed in this delivery. Remaining special-case replay and final
full integration checks are pending. No completed R7 claim is made here.

Implementation commit: e2aa8e7. Independent PR review pending.
## 2026-10-23 - V-04 and V-05

Implementer: Isabel, assuming Nicoll's implementation allocation with AI coding
assistance. Original finding and contribution authorship remain unchanged.

- V-04: Sale names RETURN_WINDOW_DAYS=30, adds canBeReturned(LocalDate), and
  retains canBeReturned() delegating with LocalDate.now. Inclusive day 30 and
  existing future-date behavior are unchanged.
- V-05: ReturnRepository has no service fields/imports. loadAll receives
  List<Sale> and separate product/accessory model collections. ReturnService
  supplies the current collections at loading time; Main constructs the
  repository with no service arguments. Resolution keeps first sale match,
  product-first item lookup, missing-item skipping and legacy defaults.
- No storage layout, refund arithmetic, return message or persistence exception
  handling was changed.

Validation: javac --release 17 succeeded. Baseline/current replay agrees exactly
on stdout and resulting data for returns, warranties, overlapping catalog IDs,
legacy optional fields and persisted-data reload. Evidence is under
 evidence/r7/replay-return-date-persistence; runner is
 evidence/r7/verify-return-date-persistence.ps1.
ReturnDateProbe verifies day 30/day 31, future dates and no-argument delegation.
Maven and final complete integration replay remain pending.

Implementation commit: 2afd6ca. Independent PR review pending.

## 2026-10-23 - V-07 monthly reporting

Implementer: Isabel (assuming Nicoll's implementation work), with AI assistance.
MonthlyBalanceReport now owns monthly sales/refunds/net calculations using
model lists. ReturnService retains all three existing public signatures and
delegates with the current sales and returns, avoiding stale snapshots.
Iteration order, arithmetic, date filtering and UI presentation are unchanged.

Validation: javac --release 17 succeeds. A baseline/current script registers
the original return then compares October and September 2026, October 2025
and January 2027 reports (populated periods, different year and empty period).
stdout and resulting data match exactly. Evidence: evidence/r7/replay-monthly-report.
Implementation commit: 49a1dac. Maven/final integration and independent PR review are pending.

## 2026-10-23 - V-08 shared UI helper (partial implementation)

Isabel with AI assistance moved the five existing input/error implementations
into ConsoleSupport. ConsoleUI creates one shared instance from its existing
Scanner; temporary private wrappers delegate to it. No menu has been extracted
in this delivery. Public helper and proposed collaborator constructor contracts
are documented in grasp-audit/v08-integration-contract.md.

Validation: Java 17 compilation succeeded; baseline/current comparisons cover
all seven menu scripts and E01/E02/E03/E04/E10/E11. stdout, stderr and resulting
data match in each scenario. Evidence: evidence/r7/replay-ui-support;
runner: evidence/r7/verify-ui-support.ps1. Remaining menu extraction and final
integration verification are pending. Implementation commit: 0e6af5a; independent PR review pending.

## 2026-10-23 - V-08 five module menus

Isabel, assuming the agreed Nicoll implementation work and using AI coding
assistance, extracted ProductMenu, PersonMenu, SaleMenu, ReturnMenu and
WarrantyMenu. ConsoleUI retains main navigation, existing constructor overloads
and temporary promotion/accessory methods pending Jaime's collaborators.
All five menus receive the same ConsoleSupport and their required services.
registerSale/findAnyItemById move together; lookup/catch order, null branches,
receipt formatting and the original contribution authorship are preserved.

Java 17 compilation succeeds. All seven menu scripts and the six additional
UI groups (E01/E02/E03/E04/E10/E11) match baseline stdout/stderr and resulting
data. Evidence: evidence/r7/replay-five-menus; verify-five-menus.ps1.
V-08 remains partial until PromotionMenu/AccessoryMenu are integrated and
remaining wrappers removed. Final integration review is still required.
Implementation commit: 47096fa. Independent PR review pending.

## 2026-10-23 - integration preparation (not final validation)

Prepared verify-integration.ps1 to compile baseline/current code and compare
all recorded scenarios, including special probes, with isolated data copies.
PowerShell syntax parsing succeeds; the new combined runner has not executed.
README documents current architecture, evidence links and pending integration.
Maven is not available through Get-Command mvn on this workstation. No Maven
build or final integrated acceptance is claimed. Remaining collaborators from
Jaime and final after-diagram/review are still pending.

## 2026-10-23 - Developer 1 corrections (V-01, V-02, V-03, V-06, V-08)

Implementer: Jaime Granados, with AI coding assistance (recorded in
developer1-ai-log.md). Contracts: developer1-audit.md, section 13.

| Class | Change | Violation ID | Pattern applied | Commit | PR / Responsible |
|---|---|---|---|---|---|
| Product | Added increaseStock(int): adds the amount to quantity, without validation (same style as decreaseStock) | V-01 | Information Expert | 2460560 | PR pending / Jaime |
| Product, VideoGame, Console, Accessory | Added abstract getCategory(); each type returns VIDEOGAME, CONSOLE or ACCESSORY | V-03 | Polymorphism | ab79954 | PR pending / Jaime |
| CategoryDiscount | matchesTargetCategory asks the product for its category instead of instanceof checks | V-03 | Polymorphism | ab79954 | PR pending / Jaime |
| Accessory, Controller, Cable, Memory | Added abstract getAccessoryType(); each subtype returns CONTROLLER, CABLE or MEMORY | V-06 | Polymorphism | b0fd09e | PR pending / Jaime |
| PromotionMenu (new) | Promotion submenu moved from ConsoleUI; uses the shared ConsoleSupport | V-08 | High Cohesion | 991faab | PR pending / Jaime |
| AccessoryMenu (new) | Accessory submenu moved from ConsoleUI; uses the shared ConsoleSupport | V-08 | High Cohesion | 991faab | PR pending / Jaime |
| Return | calculateRefundAmount and generateReturnReceipt use Sale.calculateDiscountRatio(); private discountRatio() removed | V-02 | Information Expert | 19e6326 | PR pending / Jaime |

Behavior notes:

- getCategory is compared as product.getCategory().equals(targetCategory), so a
  null or unsupported target still gives false, and the match stays case-sensitive.
- getCategory and getAccessoryType are separate methods: a Cable has category
  ACCESSORY and accessory type CABLE.
- The menus keep every prompt, option, output and catch block. Navigation reads
  the option with support.ask("") after the same printed prompt, as the V-08
  contract allows. ConsoleUI was not edited; Isabel integrates the two menus.
- V-01 and V-06 consumers (restoreStock, typeOf) were changed in Isabel's
  delivery e2aa8e7; V-02 producer Sale.calculateDiscountRatio is also in e2aa8e7.

Validation: javac (Java 17) compiles the whole project. The 13 recorded baseline
scenarios (seven menu scripts and E01/E02/E03/E04/E10/E11) were replayed at
5af8633 with isolated copies of their input data. Exit codes are zero, resulting
data match the baseline in all scenarios and stdout matches, except the encoding
of the accented letter in "Garantia reembolsada" in the returns script, which
depends on the console capture method, not on the code. PromotionMenu and
AccessoryMenu were also checked wired into a temporary copy of ConsoleUI
(promotions, accessories, E03, E11, sales): identical stdout and data.
Maven was not run on this workstation.
