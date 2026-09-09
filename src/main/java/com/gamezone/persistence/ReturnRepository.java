package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda y lee las devoluciones del archivo data/returns.txt.
 * <p>
 * En el archivo solo se guardan los ids de la venta y de los productos,
 * no los objetos completos. Por eso usa los servicios para buscar esos ids.
 */
public class ReturnRepository {

    private static final Path FILE_PATH = Path.of("data", "returns.txt");
    private static final String SEPARATOR = ";";
    private static final String PRODUCT_SEPARATOR = ",";

    private final SaleService saleService;
    private final ProductService productService;

    /**
     * @param saleService servicio para buscar la venta original
     * @param productService servicio para buscar los productos
     */
    public ReturnRepository(SaleService saleService, ProductService productService) {
        this.saleService = saleService;
        this.productService = productService;
    }

    /**
     * Guarda toda la lista de devoluciones en el archivo.
     *
     * @param returns lista completa para guardar
     */
    public void saveAll(List<Return> returns) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH, StandardCharsets.UTF_8)) {
                for (Return oneReturn : returns) {
                    writer.write(toLine(oneReturn));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de devoluciones.", e);
        }
    }

    /**
     * Lee las devoluciones guardadas. Si no hay archivo devuelve lista vacia.
     *
     * @return lista de devoluciones
     */
    public List<Return> loadAll() {
        List<Return> returns = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return returns;
        }
        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    returns.add(fromLine(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo de devoluciones.", e);
        }
        return returns;
    }

    /**
     * Pasa una devolucion a texto para guardarla.
     *
     * @param oneReturn devolucion a convertir
     * @return linea de texto
     */
    private String toLine(Return oneReturn) {
        String productIds = "";
        List<Product> products = oneReturn.getReturnedProducts();
        for (int i = 0; i < products.size(); i++) {
            if (i > 0) {
                productIds = productIds + PRODUCT_SEPARATOR;
            }
            productIds = productIds + products.get(i).getId();
        }
        return oneReturn.getId() + SEPARATOR
                + oneReturn.getDate().toString() + SEPARATOR
                + oneReturn.getOriginalSale().getId() + SEPARATOR
                + productIds + SEPARATOR
                + oneReturn.getReason() + SEPARATOR
                + oneReturn.getRefundAmount();
    }

    /**
     * Arma una devolucion desde una linea de texto.
     *
     * @param line linea del archivo
     * @return devolucion armada
     */
    private Return fromLine(String line) {
        String[] fields = line.split(SEPARATOR, -1);
        String id = fields[0];
        LocalDate date = LocalDate.parse(fields[1]);
        Sale sale = saleService.findById(fields[2]);
        String reason = fields[4];

        List<Product> found = new ArrayList<>();
        String[] ids = fields[3].split(PRODUCT_SEPARATOR);
        for (int i = 0; i < ids.length; i++) {
            List<Product> all = productService.listAll();
            for (int j = 0; j < all.size(); j++) {
                if (all.get(j).getId().equals(ids[i])) {
                    found.add(all.get(j));
                }
            }
        }
        return new Return(id, date, sale, found, reason);
    }
}
