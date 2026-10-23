package com.gamezone.service;

import java.util.List;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;

/** Calculates monthly sales, refunds and net balance from domain records. */
public final class MonthlyBalanceReport {

    /** @return sales minus refunds for the selected month and year */
    public double generateMonthlyBalance(List<Sale> sales, List<Return> returns,
                                         int month, int year) {
        return calculateMonthlySales(sales, month, year)
                - calculateMonthlyReturns(returns, month, year);
    }

    /** @return sum of sale totals in their original iteration order */
    public double calculateMonthlySales(List<Sale> sales, int month, int year) {
        double totalSales = 0.0;
        for (int i = 0; i < sales.size(); i++) {
            Sale sale = sales.get(i);
            if (sale.getDate().getMonthValue() == month && sale.getDate().getYear() == year) {
                totalSales = totalSales + sale.calculateTotal();
            }
        }
        return totalSales;
    }

    /** @return sum of recorded refunds in their original iteration order */
    public double calculateMonthlyReturns(List<Return> returns, int month, int year) {
        double totalReturns = 0.0;
        for (int i = 0; i < returns.size(); i++) {
            Return returned = returns.get(i);
            if (returned.getDate().getMonthValue() == month && returned.getDate().getYear() == year) {
                totalReturns = totalReturns + returned.getRefundAmount();
            }
        }
        return totalReturns;
    }
}