package com.asus.cordovageoloc;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CordovaGeoLoc extends CordovaPlugin {

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getLocationName")){
            String addr = getLocationName(this.cordova.getActivity().getApplicationContext(),args.getDouble(0),args.getDouble(1));
            if (!addr.isEmpty())
                callbackContext.success(addr);
            else
                callbackContext.error("");
        } else if(action.equals("dealerSearch")) {
            JSONObject jo = getLocationInfo(args.getString(0));
            String lat="25.124507", lng="121.4713891";
            String latlng[] = {lat,lng};

            getGeoPoint(jo, latlng);
            lat = latlng[0];
            lng = latlng[1];
            callbackContext.success(lat + "_" + lng);
        }
        return true;
    }

    //--------------------------------------------------------------------------
    // LOCAL METHODS
    //--------------------------------------------------------------------------
    public JSONObject getLocationInfo(String address) {
        HttpGet httpGet = new HttpGet("http://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&sensor=true");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void getGeoPoint(JSONObject jsonObject, String latlng[]) {

        try {
            latlng[0] = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getString("lat");

            latlng[1] = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getString("lng");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String getLocationName(Context context,Double lat,Double lng)
    {
        Geocoder geoCoder = new Geocoder(context,Locale.getDefault());

        try {
            List<Address> list = geoCoder.getFromLocation(lat, lng, 1);
            if (list != null & list.size() > 0) {
                Address address = list.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}