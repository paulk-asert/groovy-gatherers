import java.util.ArrayList;
import java.util.List;

public class JEP394 {
    public static void main(String[] args) {
        List<?> items = List.of("Pi", "Cat", "Camel", 3.14, new ArrayList<>());
        for (Object item : items) {
            if (item instanceof String s && s.length() < 3) {
                System.out.println(s.toUpperCase());
            }
            if (item instanceof String s && s.length() > 3) {
                System.out.println(s.toLowerCase());
            }
            if (!(item instanceof String s)) {
                return;
            }
            System.out.println(s.equalsIgnoreCase("cAT"));
        }
    }
}
// PI Pi cat 3.14 []
