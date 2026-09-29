# A6 — Verificación del balance mensual

Se revisó el código del reporte de balance mensual. El ajuste solicitado ya está implementado.

- `ReturnService.calculateMonthlySales(month, year)` suma las ventas del mes mediante `Sale.calculateTotal()`, que representa el total final de cada venta.
- `ReturnService.calculateMonthlyReturns(month, year)` suma los montos reembolsados por devoluciones del mes.
- `ReturnService.generateMonthlyBalance(month, year)` calcula ventas menos devoluciones.
- `ConsoleUI.showMonthlyBalance()` muestra por separado el total de ventas, el total de devoluciones y el balance neto.

Verificación realizada: revisión de los métodos indicados en `ReturnService.java` y `ConsoleUI.java`. No se requirieron cambios funcionales para A6.