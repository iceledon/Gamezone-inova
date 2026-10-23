import java.time.LocalDate;
import java.util.*;
import com.gamezone.model.*;
public class RefactorSemanticsProbe {
 public static void main(String[] args) {
  LocalDate date=LocalDate.of(2026,10,23);
  Product game=new VideoGame("G","Game",100,5,"PC","A","E");
  Product console=new Console("C","Console",500,3,"N","M",1);
  Product accessory=new Controller("A","Control",50,5,"Wireless");
  Sale sale=new Sale("S",date,null,null,List.of(game,console,accessory));
  for(String target:new String[]{"VIDEOGAME","CONSOLE","ACCESSORY","videogame","UNKNOWN",null}) {
   CategoryDiscount promo=new CategoryDiscount("P","Category",date,date,10,target);
   System.out.println("category="+target+" discount="+promo.calculateDiscount(sale));
   Sale nullItemSale=new Sale("N",date,null,null,Arrays.asList((Product)null));
   System.out.println("null-item category="+target+" discount="+promo.calculateDiscount(nullItemSale));
  }
  sale.setDiscountAmount(65);
  sale.addExtraCost(50);
  com.gamezone.model.Return partial=new com.gamezone.model.Return("R",date,sale,List.of(game),"Partial",0);
  System.out.println("partial refund="+partial.getRefundAmount());
  System.out.println(partial.generateReturnReceipt());
  com.gamezone.model.Return full=new com.gamezone.model.Return("ALL",date,sale,List.of(game,console,accessory),"All",50);
  System.out.println("full refund="+full.getRefundAmount());
 }
}