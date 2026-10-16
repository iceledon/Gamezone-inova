### Entry 1

**Date:** 2026-10-15
**Tool used:** Claude Code

**Reason for use:**
Understand Requirements 7 and 8 and plan my work as Developer 1 before starting the GRASP audit.

**Problem faced:**
The two PDFs are long and have two phases each, with rules about branches, Pull Requests and
who audits each class. I did not know if I could start changing code right away or what I had
to do first.

**Prompt used:**
Analyze and read the PDFs in mi_docs. Help me better understand Grasp patterns. I'll give you some examples with my code, and then you can help me check whether they're valid for our workshop scenario or not.

**Solution obtained and decision taken:**
The AI explained that Phase 2 (code changes) can only start after the consolidated audit is
merged into `develop`, because every refactor commit must use the final violation ID. It also
found that Requirement 6 (`feature/exception-handling`) is not merged into `develop` yet. I
decided to do the Phase 1 audit first, and to mark the `exceptions` and `validation` packages
as pending until Requirement 6 is merged.

### Entry 2

**Date:** 2026-10-15
**Tool used:** Claude Code

**Reason for use:**
Decide which parts of my code follow the GRASP patterns and which ones break them.

**Problem faced:**
I had many classes to audit (products, accessories, promotions, returns and warranties), and
each one had to be checked against the nine GRASP patterns. I needed a careful and precise
review of every class, with evidence from the code for each verdict.

**Prompt used:**
Make a list of the cases we've already chosen and help me find each component needed to solve the problems.

**Solution obtained and decision taken:**
The AI identified three violations: V-01 (Information Expert) in `Return.discountRatio()`,
V-02 (Polymorphism) in `CategoryDiscount.matchesTargetCategory()`, and V-03 (Protected
Variations) in `Sale.canBeReturned()`. The other six patterns were correct. I carefully
analyzed each violation in the code to understand it, and to understand how GRASP proposes
to solve it in Phase 2. I decided to upload the audit in six atomic commits, one for each
part of the report.