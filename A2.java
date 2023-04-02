import main.main;
import main.com.news;
import java.util.*;

public class A2 {
    public static void main(String[] args) {
        main newsapi = new main();
        String response = newsapi.extract();
        System.out.println(response);
        //List<news> articles = newsapi.dataProcessing(response);
        //newsapi.writeBatchToFile(articles);
    }
}