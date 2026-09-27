package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa la devolucion de uno o varios productos de una venta que ya existe.
 * <p>
 * Una devolucion siempre hace referencia a una venta anterior, pero no tiene que incluir
 * todos los productos de esa venta: el cliente puede quedarse con unos y devolver otros.
 * Por eso esta clase guarda su propia lista de productos devueltos, en vez de asumir que
 * son todos los de la venta original.
 */
public class Return {

    private String id;
    private LocalDate date;
    private Sale originalSale;
    private List<Product> returnedProducts;
    private String reason;
    private double refundAmount;

    /**
     * Crea una devolucion nueva con sus datos basicos.
     * El monto reembolsado no se recibe por parametro: se calcula solo, sumando el
     * precio de los productos devueltos, asi nadie puede pasar un monto que no cuadre.
     *
     * @param id identificador unico de la devolucion
     * @param date fecha en la que se registra la devolucion
     * @param originalSale venta a la que pertenece esta devolucion
     * @param returnedProducts productos que el cliente esta devolviendo
     * @param reason motivo que dio el cliente
     */
    public Return(String id, LocalDate date, Sale originalSale, List<Product> returnedProducts, String reason) {
        if (returnedProducts == null || returnedProducts.isEmpty()) {
            throw new IllegalArgumentException("La devolucion debe contener al menos un producto.");
        }
        this.id = id;
        this.date = date;
        this.originalSale = originalSale;
        this.returnedProducts = new ArrayList<>(returnedProducts);
        this.reason = reason;
        this.refundAmount = calculateRefundAmount();
    }

    /**
     * @return el identificador unico de la devolucion
     */
    public String getId() {
        return id;
    }

    /**
     * @return la fecha en la que se registro la devolucion
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @return la venta original a la que pertenece esta devolucion
     */
    public Sale getOriginalSale() {
        return originalSale;
    }

    /**
     * @return los productos que se devolvieron
     */
    public List<Product> getReturnedProducts() {
        return returnedProducts;
    }

    /**
     * @return el motivo que dio el cliente para devolver
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return el monto que se le reembolsa al cliente
     */
    public double getRefundAmount() {
        return refundAmount;
    }

    /**
     * Calcula cuanto hay que reembolsar de cada producto devuelto, aplicando la misma
     * proporcion de descuento que tuvo la venta original, para no reembolsar mas de lo que
     * el cliente realmente pago.
     *
     * @return el monto total a reembolsar
     */
    public double calculateRefundAmount() {
        double ratio = discountRatio();
        double total = 0.0;
        for (Product product : returnedProducts) {
            total = total + product.getPrice() * (1 - ratio);
        }
        this.refundAmount = total;
        return total;
    }

    /**
     * @return la fraccion del precio de cada producto que descuento la promocion de la
     *         venta original, o 0.0 si la venta no tuvo descuento o no tiene productos
     *         sobre los cuales calcular la proporcion
     */
    private double discountRatio() {
        double subtotal = originalSale.calculateSubtotal();
        if (subtotal <= 0) {
            return 0.0;
        }
        return originalSale.getDiscountAmount() / subtotal;
    }

    /**
     * Arma un texto en español con el detalle de la devolucion, para mostrarlo como
     * comprobante en el menu de consola.
     *
     * @return el comprobante de la devolucion en forma de texto
     */
    public String generateReturnReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Devolucion ").append(id).append(" | Fecha: ").append(date).append("\n");
        receipt.append("  Venta original: ").append(originalSale.getId()).append("\n");
        receipt.append("  Productos devueltos:\n");
        for (Product product : returnedProducts) {
            receipt.append("    - [").append(product.getId()).append("] ")
                    .append(product.getTitle()).append(" ($").append(product.getPrice()).append(")\n");
        }
        receipt.append("  Motivo: ").append(reason).append("\n");
        receipt.append("  Monto reembolsado: $").append(refundAmount);
        return receipt.toString();
    }
}
