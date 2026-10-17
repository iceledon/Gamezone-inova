# Developer 2 — AI Usage Log

Author: Nicoll Jhoana Gómez Siosi.

## Requirement 7 — Phase 1

Date: 2026-10-17.
Tool: ChatGPT / Codex.
Baseline: 3dff9c12c7c523bd8658e448eeb236b11ec94818.

### Assistance received

AI provided step-by-step Git and editor instructions, explanations
of GRASP patterns, analysis suggestions based on the Java files
shared in the conversation, and English draft text for the audit
and question 4.

Most audit paragraphs and the proposed regression strategy were
drafted by AI. They require my review and team discussion before
being treated as final conclusions.

### Files involved

- docs/grasp-audit/developer2-audit.md
- docs/grasp-analysis.md
- docs/ai-usage/developer2-ai-log.md

### Verification performed

- Updated develop and recorded the baseline commit.
- Compiled using javac --release 17; exit code was 0.
- Checked Git status and the PersonRepository diff after correcting
  an accidental editor modification.
- Maven was unavailable; Maven verification remains pending.

Compilation does not establish complete functional correctness.
The proposed regression matrix has not been executed in this phase.

### Pending review

- Validate the evidence and final verdicts for all nine patterns.
- Discuss V-01, V-02 and V-03 with Isa and Jaime.
- Confirm warranty model behavior with Jaime.
- Discuss startup coordination and exception handling with Isa.
- Review and discuss the proposed response to question 4.

## Requirement 7 — Phase 2

Not started. Record actual assistance, decisions and verification
here when the team authorizes the refactoring phase.