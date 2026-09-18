package com.gamezone.model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/**
* Represents the warranty coverage associated with one product sold within a sale.
* <p>
* Concrete subclasses define how long the coverage lasts, what it is called and how much
* extra it costs, while this class owns the data and behavior common to every warranty:
* the covered product and sale, the coverage period, and how to check whether it is
* still active on a given date.
*/
public abstract class Warranty {
private String id;
private Product product;
private Sale sale;
private LocalDate startDate;
private LocalDate endDate;
/**
* Creates a new warranty starting on the given date. The end date is computed right
* away by adding {@link #getDurationInMonths()} months to the start date, so every
* warranty object is already fully valid as soon as it is built.
*
* @param id unique identifier of the warranty
* @param product product covered by this warranty
* @param sale sale that generated this warranty
* @param startDate date the coverage begins (usually the sale date)
*/
protected Warranty(String id, Product product, Sale sale, LocalDate startDate) {
this.id = id;
this.product = product;
this.sale = sale;
this.startDate = startDate;
this.endDate = startDate.plusMonths(getDurationInMonths());
}

/**
* @return the unique identifier of the warranty
*/
public String getId() {
return id;
}
/**
* @return the product covered by this warranty
*/
public Product getProduct() {
return product;
}
/**
* @return the sale that generated this warranty
*/
public Sale getSale() {
return sale;
}
/**
* @return the date the coverage begins
*/
public LocalDate getStartDate() {
return startDate;
}
/**
* @return the date the coverage ends
*/
public LocalDate getEndDate() {
return endDate;
}
/**
* @return the duration of this type of warranty, in months
*/
public abstract int getDurationInMonths();
/**
* @return the display name of this type of warranty
*/
public abstract String getWarrantyType();
/**
* @return the extra amount this warranty adds to the total of the sale
*/
public abstract double getAdditionalCost();
/**
* Checks whether this warranty is still valid on the given date.
*
* @param date the date to check
* @return true if date falls within [startDate, endDate], false otherwise
*/
public boolean isActive(LocalDate date) {
    return !date.isBefore(startDate) && !date.isAfter(endDate);
}
/**
* Builds a human-readable certificate of this warranty, in Spanish, so it can be
* printed directly by the console interface.
*
* @return the formatted certificate text
*/
public String generateWarrantyCertificate() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    return String.format(
            "Certificado de garantia%n"
                    + " Garantia: %s (%s)%n"
                    + " Producto: [%s] %s%n"
                    + " Venta: %s%n"
                    + " Vigencia: %s al %s%n"
                    + " Costo adicional: $%.2f",
            id, getWarrantyType(),
            product.getId(), product.getTitle(),
            sale.getId()
            startDate.format(formatter), endDate.format(formatter),
            getAdditionalCost());
    }
}