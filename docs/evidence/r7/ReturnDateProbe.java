import java.time.LocalDate;
import java.util.List;
import com.gamezone.model.Sale;
public class ReturnDateProbe {
 public static void main(String[] args) {
  LocalDate saleDate=LocalDate.of(2026,10,1);
  Sale sale=new Sale("DATES",saleDate,null,null,List.of());
  if(!sale.canBeReturned(saleDate.plusDays(30))) throw new AssertionError("day 30");
  if(sale.canBeReturned(saleDate.plusDays(31))) throw new AssertionError("day 31");
  if(!sale.canBeReturned(saleDate.minusDays(1))) throw new AssertionError("future sale");
  if(sale.canBeReturned()!=sale.canBeReturned(LocalDate.now())) throw new AssertionError("no-arg delegate");
  System.out.println("Day 30 accepted; day 31 rejected; future-date behavior preserved; no-arg delegates.");
 }
}