import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import com.gamezone.model.Sale;
import com.gamezone.service.ProductService;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.exceptions.PersistenceException;
public class R7BaselineProbe {
 public static void main(String[] args) throws Exception {
  if (args[0].equals("dates")) {
   LocalDate now=LocalDate.now();
   for(int age:new int[]{30,31,-1}) {
    Sale sale=new Sale("PROBE",now.minusDays(age),null,null,List.of());
    boolean result=sale.canBeReturned();
    System.out.println("age="+age+" returnable="+result);
    if(result!=(age!=31)) throw new AssertionError("Return boundary changed");
   }
  } else {
   ProductService service=new ProductService(new ProductRepository());
   Path original=Path.of("data/products.txt");
   Files.move(original,Path.of("data/products-before-write.txt"));
   Files.createDirectory(original);
   boolean failed=false;
   try { service.registerVideoGame("R7WRITEFAIL","Write failure",100,1,"PC","Aventura","E"); }
   catch(PersistenceException e) {failed=true;System.out.println("write failure category="+e.getClass().getSimpleName());}
   boolean appears=service.listAll().stream().anyMatch(p->p.getId().equals("R7WRITEFAIL"));
   System.out.println("rejected product remains in memory="+appears);
   if(!failed||appears) throw new AssertionError("Unexpected write-failure behavior");
  }
 }
}