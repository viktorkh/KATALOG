import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchServices {

    private static final String API_KEY = "YOUR_API_KEY";
    private static final String BASE_URL = "https://cloudbilling.googleapis.com/v2beta/services";
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        try {
            JsonArray allServices = new JsonArray();
            String pageToken = null;

            do {
                // בונים את ה-URL עם ה-pageToken אם יש
                String urlString = BASE_URL + "?key=" + API_KEY;
                if (pageToken != null) {
                    urlString += "&pageToken=" + pageToken;
                }

                // יצירת חיבור ל-API
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // קריאת התשובה מה-API
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                // סגירת החיבורים
                in.close();
                conn.disconnect();

                // המרת התשובה ל-JSON
                JsonObject jsonResponse = gson.fromJson(content.toString(), JsonObject.class);

                // הוספת השירותים לרשימה הכוללת
                JsonArray services = jsonResponse.getAsJsonArray("services");
                if (services != null) {
                    for (JsonElement service : services) {
                        allServices.add(service);
                    }
                }

                // קבלת ה-pageToken הבא
                pageToken = jsonResponse.has("nextPageToken") ? jsonResponse.get("nextPageToken").getAsString() : null;

            } while (pageToken != null); // ממשיכים עד שאין יותר דפים

            // שמירת הנתונים לקובץ JSON
            FileWriter file = new FileWriter("services_data.json");
            gson.toJson(allServices, file);
            file.flush();
            file.close();

            System.out.println("הנתונים נשמרו בקובץ services_data.json");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
