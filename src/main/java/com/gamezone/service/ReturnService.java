package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.persistence.ReturnRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reglas de negocio de las devoluciones: registrar, consultar y balance mensual.
 */
public class ReturnService {

    private final ReturnRepository repository;
    private final SaleService saleService;
    private final ProductService productService;
    private final List<Return> returns;

    /**
     * @param repository repositorio para guardar las devoluciones
     * @param saleService servicio para buscar las ventas
     * @param productService servicio para devolver el stock
     */
    public ReturnService(ReturnRepository repository, SaleService saleService, ProductService productService) {
        this.repository = repository;
        this.saleService = saleService;
        this.productService = productService;
        this.returns = new ArrayList<>(repository.loadAll());
    }

    /**
     * Registra una devolucion si la venta existe, esta a tiempo y los productos son de esa venta.
     *
     * @param saleId id de la venta original
     * @param productIds ids de los productos a devolver
     * @param reason motivo de la devolucion
     * @return la devolucion guardada
     */
    public Return registerReturn(String saleId, List<String> productIds, String reason) {
        Sale sale = saleService.findById(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("No existe una venta con el identificador " + saleId + ".");
        }
        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException("Ya paso el plazo de 30 dias para devolver productos de esta venta.");
        }

        List<Product> toReturn = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            String wanted = productIds.get(i);
            Product found = null;
            List<Product> fromSale = sale.getProducts();
            for (int j = 0; j < fromSale.size(); j++) {
                if (fromSale.get(j).getId().equals(wanted)) {
                    found = fromSale.get(j);
                }
            }
            if (found == null) {
                throw new IllegalArgumentException("El producto " + wanted + " no pertenece a la venta " + saleId + ".");
            }
            toReturn.add(found);
        }

        String newId = "D" + String.format("%03d", returns.size() + 1);
        Return newReturn = new Return(newId, LocalDate.now(), sale, toReturn, reason);

        for (int i = 0; i < toReturn.size(); i++) {
            productService.restoreStock(toReturn.get(i).getId(), 1);
        }

        returns.add(newReturn);
        repository.saveAll(returns);
        return newReturn;
    }

    /**
     * @return todas las devoluciones
     */
    public List<Return> viewAllReturns() {
        return new ArrayList<>(returns);
    }

    /**
     * Busca las devoluciones de un cliente por la venta original.
     *
     * @param customerId id del cliente
     * @return devoluciones de ese cliente
     */
    public List<Return> viewReturnsByCustomer(String customerId) {
        List<Return> result = new ArrayList<>();
        for (int i = 0; i < returns.size(); i++) {
            Return r = returns.get(i);
            if (r.getOriginalSale().getCustomer().getId().equals(customerId)) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * @param saleId id de la venta
     * @return devoluciones de esa venta
     */
    public List<Return> viewReturnsBySale(String saleId) {
        List<Return> result = new ArrayList<>();
        for (int i = 0; i < returns.size(); i++) {
            Return r = returns.get(i);
            if (r.getOriginalSale().getId().equals(saleId)) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Resta las devoluciones a las ventas del mes para dar el balance.
     *
     * @param month mes de 1 a 12
     * @param year anio
     * @return balance del mes
     */
    public double generateMonthlyBalance(int month, int year) {
        return calculateMonthlySales(month, year) - calculateMonthlyReturns(month, year);
    }

    /**
     * Suma el total de las ventas hechas en el mes.
     *
     * @param month mes de 1 a 12
     * @param year anio
     * @return total vendido en el mes
     */
    public double calculateMonthlySales(int month, int year) {
        double totalSales = 0.0;
        List<Sale> allSales = saleService.getAllSales();
        for (int i = 0; i < allSales.size(); i++) {
            Sale s = allSales.get(i);
            if (s.getDate().getMonthValue() == month && s.getDate().getYear() == year) {
                totalSales = totalSales + s.calculateTotal();
            }
        }
        return totalSales;
    }

    /**
     * Suma la plata reembolsada en el mes.
     *
     * @param month mes de 1 a 12
     * @param year anio
     * @return total devuelto en el mes
     */
    public double calculateMonthlyReturns(int month, int year) {
        double totalReturns = 0.0;
        for (int i = 0; i < returns.size(); i++) {
            Return r = returns.get(i);
            if (r.getDate().getMonthValue() == month && r.getDate().getYear() == year) {
                totalReturns = totalReturns + r.getRefundAmount();
            }
        }
        return totalReturns;
    }
}
