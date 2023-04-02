package main;
import main.com.news;
import java.net.*;
import java.net.http.HttpRequest;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.util.*;
import org.json.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.bson.Document;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class NewsAPI {
    StringBuilder result = new StringBuilder();
    String apiKey = "1592fea403564db889a8dc839612c2be";
    String keyword;
    List<news> articleBatch = new ArrayList<>();

    public String extract(){
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter specific keyword for the news: ");
            keyword = scanner.nextLine();
            
            String urlString = "https://newsapi.org/v2/everything?q=" + keyword + "&apiKey=" + apiKey;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
    
    private String RequestData(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return result.toString();
    }

    public List<news> dataProcessing(String response){
        try{
            JSONObject responseJson = new JSONObject();
            System.out.println(responseJson);
            JSONArray jsonArr = responseJson.getJSONArray("articles");
            
            System.out.println(jsonArr.toString());

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject articleObj = jsonArr.getJSONObject(i);
                String title = articleObj.getString("title");
                String content = articleObj.getString("content");
                if (title.toLowerCase().contains(keyword.toLowerCase()) || content.toLowerCase().contains(keyword.toLowerCase())) { 
                    articleBatch.add(new news(title, content));
                    break;
                }
            }
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return articleBatch;
    }

    public static void writeBatchToFile(List<news> articles){
        int fileNumber = 1;
        int Count = 0;
        FileWriter writer = null;
        try{
            for (news article : articles) {
                if (Count == 0) {
                    writer = new FileWriter("news" + fileNumber + ".txt");
                    fileNumber++;
                }
                writer.write(article.getTitle() + "\n");
                writer.write(article.getContent() + "\n\n");
                Count++;
                if (Count == 5) {
                    writer.close();
                    Count = 0;
                }
            }
            if (writer != null) {
                writer.close();
            }
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        
    }

    public void Database(){
        private static final String DB_NAME = "myMongoNews";
        private static final String COLLECTION_NAME = "news";

        try {
            // Connect to MongoDB server
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            MongoDatabase db = mongoClient.getDatabase(DB_NAME);

            // Create a collection if it doesn't exist
            if (!db.listCollectionNames().into(new ArrayList<String>()).contains(COLLECTION_NAME)) {
                db.createCollection(COLLECTION_NAME);
            }

            // Process each file in the directory
            File dir = new File("news_files");
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    BufferedReader reader = new BufferedReader(new FileReader(file));

                    // Clean and transform data
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Remove special characters, URLs, emoticons, etc.
                        line = line.replaceAll("[^\\p{L}\\p{Z}]", ""); // remove non-letters and non-spaces
                        line = line.replaceAll("\\b\\w{1,5}\\b", ""); // remove short words
                        line = line.replaceAll("http\\S+", ""); // remove URLs
                        line = line.replaceAll("[\\p{Punct}&&[^'-]]+", ""); // remove all punctuation except apostrophe and hyphen

                        // Write cleaned data to MongoDB
                        db.getCollection(COLLECTION_NAME).insertOne(new Document("text", line));
                    }
                    reader.close();
                }
            }

            mongoClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
