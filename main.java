package main;
import java.util.*;
import main.com.news;
import java.net.*;
import java.net.http.HttpRequest;
import java.io.*;
import java.util.*;
import org.json.*;

public class main {
    public String extract(){
        StringBuilder result = new StringBuilder();
        try {
            
            String apiKey = "1592fea403564db889a8dc839612c2be";
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter specific keyword for the news: ");
            String keyword = scanner.nextLine();
            
            String urlString = "https://newsapi.org/v2/everything?q=" + keyword + "&apiKey=" + apiKey;

            URL newURL = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) newURL.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            //response = RequestData(urlString);

            //System.out.println(response);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return result.toString();
    }
}
