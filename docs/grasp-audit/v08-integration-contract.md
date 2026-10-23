# V-08 integration contract

ConsoleSupport is implemented in com.gamezone.ui. Isabel provides it and
ConsoleUI creates one instance from its single Scanner(System.in).

Public API:
- ConsoleSupport(Scanner scanner)
- String ask(String prompt)
- int askInt(String prompt)
- double askDouble(String prompt)
- LocalDate askDate(String prompt)
- void showError(RuntimeException error)

No scanner creation/closing inside menu classes; no duplicated parsers/errors.
For existing navigation that prints its prompt separately, use support.ask("")
to read and trim exactly one line. Alternatively combine the identical prompt
with support.ask, ensuring it is printed exactly once.

Jaime's menu constructor contract proposed for immediate consumer integration:
- PromotionMenu(PromotionService promotionService, ConsoleSupport support)
- AccessoryMenu(AccessoryService accessoryService, ConsoleSupport support)
- Both expose public void show(); keep operation methods private.
- Isabel wires them into ConsoleUI; retain optional-service behavior and
  existing catches. Do not add null guards or retries.

To avoid shared-file conflicts, Jaime adds the two new menu classes without
editing ConsoleUI. Isabel removes the old methods and wires the main navigation
once those classes are published. Until then ConsoleUI wrappers delegate to
ConsoleSupport. Remaining module extraction is not completed by the helper.