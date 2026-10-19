# Revisión de validaciones y excepciones de Nicoll

## Referencia de la revisión

Responsable: Nicoll Gómez.

Commit de referencia:
701255d4209e070146c32ee5a663b08d3b4b0bb4

La evaluación corresponde al código de este commit.
Los hallazgos son resultado de inspección estática; no se presentan
como correcciones implementadas ni como pruebas ejecutadas en esta revisión.

## Alcance y autoría

Se revisaron mis aportes de validaciones y excepciones en
SaleRepository, SaleService, ProductService y ConsoleUI.
También se inspeccionaron Sale, Main, SaleValidator,
ProductValidator y Product para comprobar su interacción.

Commits propios identificados:

- 075e35c703596e9d02fefaf877773e0e61008967:
  manejo de errores de persistencia en SaleRepository.
- b8a321e7ac20e08352b61c71f803689951514a22:
  separación de la validación de productos de la venta.
- 61f1393eb211866fa66dc69647b3f21342566559:
  integración de validaciones y excepciones en servicios e interfaz.
- ecfbf2ac8632d5a69a24f4cf4d1890280d29f933:
  rechazo de elementos nulos en la lista de una venta.

Jaime creó las clases iniciales de validadores y excepciones.
Mis aportes corresponden a su integración y a las mejoras indicadas.

La evaluación de los cálculos compartidos de Sale queda a cargo
de Jaime o Isabel, según el acuerdo del equipo. Este documento
no duplica esa evaluación. Su enlace se añadirá al consolidar.

## SaleValidator y SaleService

validateSaleProducts rechaza listas nulas o vacías mediante
BusinessRuleException y elementos nulos mediante InvalidDataException.
validateSaleData reutiliza esa comprobación y verifica que existan
las referencias al cliente y al vendedor.

SaleService.registerSale valida la lista antes de resolver los
productos. Después valida los participantes y comprueba el stock
sobre las cantidades agrupadas por identificador.

Esta separación concentra las comprobaciones reutilizables en
validadores y deja la coordinación de la venta en el servicio,
lo que favorece la responsabilidad única.

Limitación: validateSaleProducts no comprueba identificadores
vacíos ni el contenido interno de cada producto. Su contrato
no debe presentarse como una validación completa de esos datos.

Hallazgo: registerSale asigna garantías, actualiza inventario,
agrega la venta en memoria y finalmente guarda las ventas.
Una excepción de persistencia puede interrumpir la operación
después de algunos cambios, sin una compensación explícita.

Propuesta: acordar con los responsables del flujo de ventas
un mecanismo de confirmación o compensación que cubra todos
los cambios involucrados. Mover únicamente sales.add no resuelve
la actualización parcial del inventario o las garantías.

## SaleRepository

save y load convierten IOException en PersistenceException
y conservan la excepción original como causa.

load también convierte errores de números, fechas y acceso a
campos inexistentes en errores de persistencia. fromLine informa
las referencias no resueltas a clientes, vendedores y productos.

La conversión evita que las capas superiores tengan que manejar
directamente IOException para estas operaciones.

Limitación: esto no garantiza que cualquier registro inválido
sea rechazado. Por ejemplo, Double.parseDouble acepta NaN e
Infinity, y los métodos de Sale usados al cargar importes no
comprueban que sean finitos.

Propuesta: validar estructura, dominios e importes del registro
y añadir contexto, como el número de línea, a los errores.

Los comentarios @throws RuntimeException y
@throws PersistenceException se solapan; conviene precisar
la documentación de los fallos previstos.

## ProductService y restoreStock

Mi integración incorpora ProductValidator en el registro,
la búsqueda y la comprobación de stock. La versión revisada
incluye además una corrección posterior de Isabel para guardar
los nuevos productos antes de agregarlos al inventario en memoria.

Hallazgos en restoreStock:

- No rechaza amount igual a cero o negativo.
- La suma de cantidades puede desbordar el rango de int.
- Product.setQuantity asigna el valor sin validarlo.
- Cambia la cantidad antes de guardar; si falla la escritura,
  la cantidad en memoria no se restablece.

ProductValidator.validateStockAvailability tampoco rechaza
cantidades solicitadas negativas o iguales a cero.
En updateStock una cantidad negativa puede aumentar el inventario.

Propuesta: validar cantidades estrictamente positivas en las
operaciones del servicio, comprobar el desbordamiento y definir
la recuperación del estado ante fallos de persistencia.

Este hallazgo se comparte con Isabel, responsable de revisar
restoreStock, para evitar correcciones duplicadas.

## ConsoleUI y Main

Mi cambio en ConsoleUI.findAnyItemById adapta la búsqueda del
catálogo de productos a ResourceNotFoundException y conserva
la búsqueda alternativa de accesorios.

El método todavía contempla NoSuchElementException en accesorios.
Debe contrastarse ese manejo con el contrato de AccessoryService
antes de proponer su unificación.

En la versión revisada, registerSale delega los errores a showError.
El manejo diferenciado de errores y la validación de entradas
numéricas y fechas incluyen aportes posteriores de Isabel.

Main captura PersistenceException y otros RuntimeException,
muestra mensajes generales y termina con código 1.
Se revisa como punto de integración; no se atribuye su creación
a Nicoll en los cuatro commits identificados.

## Sale y observaciones de diseño

Sale no invoca SaleValidator en su constructor.
La validación realizada desde SaleService protege ese flujo,
pero no garantiza por sí sola que toda construcción directa
de Sale respete las mismas condiciones.

Los importes de addExtraCost y setDiscountAmount se asignan
sin validar su dominio. El tratamiento de estos límites debe
coordinarse con quien evalúe los cálculos compartidos de Sale.

La separación de validaciones favorece SRP, pero no demuestra
por sí sola el cumplimiento de todos los principios SOLID.
SaleService y ProductService dependen de clases concretas
de repositorios y servicios; esa dependencia debe considerarse
en la evaluación de DIP.

## Conclusión

Los aportes revisados integran validaciones reutilizables y
excepciones específicas. Quedan oportunidades de mejora en
cantidades de inventario, validación de registros persistidos
y recuperación ante operaciones parcialmente completadas.

Los hallazgos compartidos deben coordinarse con Jaime e Isabel
antes de modificar las mismas clases.