package gr.uth.displayphotosv2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//a class which fetches data from Google Place API using HTTP request
public class PlaceApi {

    //returns a list of place suggestions when a user is typing in the input field
    public ArrayList<String> autoComplete(String input){
        ArrayList<String> arrayList = new ArrayList<>();
        HttpURLConnection connection = null;
        StringBuilder jsonResult = new StringBuilder();

        try {

            /*make a HTTP request to Google Place Api passing user's input and an API key which is required
              in order to make the request.*/
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
            sb.append("input="+input);
            sb.append("&key=AIzaSyAKo5KbtI9hxynE2YVrbI1fvkfvRkA7xXQ");
            URL url = new URL(sb.toString());
            connection=(HttpURLConnection)url.openConnection();

            //fetch the response of the request into a StringBuilder
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read=inputStreamReader.read(buff))!=-1){
                jsonResult.append(buff,0,read);

            }
            inputStreamReader.close();
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        //close the connection
        finally {
            if(connection!=null){
                connection.disconnect();
            }
        }

        try {
            //convert the response into a JSON object
            JSONObject jsonObject = new JSONObject(jsonResult.toString());
            /*fetch the "predictions" array from the previously created JSON object*/
            JSONArray prediction = jsonObject.getJSONArray("predictions");
            /*the "prediction" array contains JSON objects. Each object has
            a "description" item(key) which holds a location suggestion(value) as a String.
            Fetch all suggestions and add them to the arrayList*/
            for (int i=0;i<prediction.length();i++){
                arrayList.add(prediction.getJSONObject(i).getString("description"));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return arrayList;
    }
}
