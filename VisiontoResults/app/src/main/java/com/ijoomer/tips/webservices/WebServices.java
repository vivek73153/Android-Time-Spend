package com.ijoomer.tips.webservices;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ijoomer.tips.utilities.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tasol on 22/12/14.
 */
public class WebServices {

    private Context context;

    private HttpClient httpclient = new DefaultHttpClient();

    public WebServices(Context context){
        this.context=context;
    }

    public String wsSuggestTip(String name,String email,String tip,String twitterHandler){

        if(isInternetConnectionAvailable()) {

            HttpPost httppost = new HttpPost(Utility.WS_MAIN_URL + Utility.WS_SUGGEST_TIP_REQUEST);
            String responseText = "";
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair(Utility.WS_SUGGEST_TIP_KEY_NAME, name));
                nameValuePairs.add(new BasicNameValuePair(Utility.WS_SUGGEST_TIP_KEY_EMAIL_ID, email));
                nameValuePairs.add(new BasicNameValuePair(Utility.WS_SUGGEST_TIP_KEY_TIP, tip));
                nameValuePairs.add(new BasicNameValuePair(Utility.WS_SUGGEST_TIP_KEY_TWITTER_HANDLER, twitterHandler));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                responseText = EntityUtils.toString(response.getEntity());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseText;
        }else{
            return "404";
        }
    }

    public String wsCallMeBack(String name,String contactNo,String country,String preferredTime){
        if(isInternetConnectionAvailable()) {
            HttpPost httppost = new HttpPost(Utility.WS_MAIN_URL+Utility.WS_CALL_ME_BACK_REQUEST);
            String responseText="";
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair(Utility.WS_CALL_ME_BACK_KEY_NAME, name));
                nameValuePairs.add(new BasicNameValuePair(Utility.WS_CALL_ME_BACK_KEY_CONTACT, contactNo));
                nameValuePairs.add(new BasicNameValuePair(Utility.WS_CALL_ME_BACK_KEY_COUNTRY, country));
                nameValuePairs.add(new BasicNameValuePair(Utility.WS_CALL_ME_BACK_KEY_PREFERRED_TIME, preferredTime));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                responseText = EntityUtils.toString(response.getEntity());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseText;
        }else{
            return "404";
        }
    }

    public String wsVtreBook(String email){
        if(isInternetConnectionAvailable()) {
            HttpPost httppost = new HttpPost(Utility.WS_MAIN_URL+Utility.WS_VTREBOOK_REQUEST);
            String responseText="";
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair(Utility.WS_VTREBOOK_EMAIL, email));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                responseText = EntityUtils.toString(response.getEntity());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseText;
        }else{
            return "404";
        }
    }

    private boolean isInternetConnectionAvailable(){
        ConnectivityManager cm =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
