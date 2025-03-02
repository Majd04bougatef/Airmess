package util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class GeoCodeApi {
    final String url = "https://geocode.maps.co/reverse";
    static final String API_KEY = "67c224b504a2c875779869ayqdf2a7a";
    public static String getAddressFromLatLong(String lat, String lon) {
        String address = "";
        try {
            String url = "https://geocode.maps.co/reverse?lat=" + lat + "&lon=" + lon + "&api_key=" + API_KEY;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONObject jsonObj = new JSONObject(response.toString());
                address = jsonObj.getString("display_name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }
}
