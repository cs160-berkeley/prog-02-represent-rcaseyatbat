package com.example.rcasey.findmyreps;

import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.models.Tweet;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by rcasey on 3/10/16.
 */
public class TwitterStreamTask extends AsyncTask<String, Void, String> {

    private static final String TWITTER_KEY = "0ZN9JyoD5UVVderOLlK1DEU0g";
    private static final String TWITTER_SECRET = "LafwHUMEX6TktwGAPyctkAYSism2EioZgoUpe4ViqoVgLv35Vm";

    final static String CONSUMER_KEY = TWITTER_KEY;
    final static String CONSUMER_SECRET = TWITTER_SECRET;
    final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
    final static String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";

    @Override
    protected String doInBackground(String... screenNames) {
        String result = null;

        if (screenNames.length > 0) {
            result = getTwitterStream(screenNames[0]);
        }
        return result;
    }

    // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
    @Override
    protected void onPostExecute(String result) {

        /*
        Twitter twits = jsonToTwitter(result);

        // lets write the results to the console as well
        for (Tweet tweet : twits) {
            Log.i(LOG_TAG, tweet.getText());
        }

        // send the tweets to the adapter for rendering
        ArrayAdapter<Tweet> adapter = new ArrayAdapter<Tweet>(activity, android.R.layout.simple_list_item_1, twits);
        setListAdapter(adapter);
        */
    }

    private String getTwitterStream(String screenName) {
        String results = null;

        // Step 1: Encode consumer key and secret
        try {
            // URL encode the consumer key and secret
            String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
            String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");

            // Concatenate the encoded consumer key, a colon character, and the
            // encoded consumer secret
            String combined = urlApiKey + ":" + urlApiSecret;

            // Base64 encode the string
            String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

            // Step 2: Obtain a bearer token
            /*
            HttpPost httpPost = new HttpPost(TwitterTokenURL);
            httpPost.setHeader("Authorization", "Basic " + base64Encoded);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
            */

            Log.v("T", "Creating HTTP...");

            URL url = new URL(TwitterTokenURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "Basic " + base64Encoded);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            Log.v("T", "Created HTTP...");
            //connection.setEntity()
            //connection.setDoOutput(true);
            //connection.setInstanceFollowRedirects(false);

            InputStream is = connection.getInputStream();

            // Convert the InputStream into a string
            String rawAuthorization = readIt(is);

            Log.v("T", rawAuthorization);


            /*
            //String rawAuthorization = getResponseBody(httpPost);
            Authenticated auth = jsonToAuthenticated(rawAuthorization);

            // Applications should verify that the value associated with the
            // token_type key of the returned object is bearer
            if (auth != null && auth.token_type.equals("bearer")) {

                // Step 3: Authenticate API requests with bearer token
                HttpGet httpGet = new HttpGet(TwitterStreamURL + screenName);

                // construct a normal HTTPS request and include an Authorization
                // header with the value of Bearer <>
                httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
                httpGet.setHeader("Content-Type", "application/json");
                // update the results with the body of the response
                results = getResponseBody(httpGet);
            }
            */
        } catch (UnsupportedEncodingException ex) {
        } catch (IllegalStateException ex1) {
        } catch (Exception e) {
            Log.v("T", "Exception..." + e);

        }
        return results;
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null)
        {
            sb.append(line + "\n");
        }
        String result = sb.toString();
        return result;
    }
}