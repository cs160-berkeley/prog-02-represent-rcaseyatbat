package com.example.rcasey.findmyreps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;
import java.util.Vector;


import android.location.Location;
import android.location.Geocoder;
import android.location.Address;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.User;
import io.fabric.sdk.android.Fabric;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.*;
import io.fabric.sdk.android.Fabric;
import android.view.ViewGroup;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "0ZN9JyoD5UVVderOLlK1DEU0g";
    private static final String TWITTER_SECRET = "LafwHUMEX6TktwGAPyctkAYSism2EioZgoUpe4ViqoVgLv35Vm";


    private Button mSubmitButton;

    private EditText mZipCode;
    private String mCurrentZip;
    private String mCurrentCounty;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    protected double mLastLatitude;
    protected double mLastLongtide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);


        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();

        /*
        StatusesService statusesService = twitterApiClient.getStatusesService();
        statusesService.show(524971209851543553L, null, null, null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                //Do something with result, which provides a Tweet inside of result.data
            }

            public void failure(TwitterException exception) {
                //Do something on failure
            }
        });
        */


        String twitterID = "SenatorBoxer";
        twitterApiClient.getStatusesService().userTimeline(null, twitterID, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                //Do something with result, which provides a Tweet inside of result.data


                for(Tweet Tweet : result.data) {
                    //tweetList.add(Tweet.text);
                    Log.v("T", "Got a Tweet! " + Tweet.text );
                    Log.v("T", "Got a Tweet! " + Tweet.id );
                }
            }

            public void failure(TwitterException exception) {
                //Do something on failure
                Log.v("T", "Failed for MainActivity! " + exception);
            }
        });




        /*
        // TODO: Use a more specific parent
        final ViewGroup parentView = (ViewGroup) getWindow().getDecorView().getRootView();
        // TODO: Base this Tweet ID on some data from elsewhere in your app
        long tweetId = 631879971628183552L;
        tweetId = 524971209851543553L;
        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                TweetView tweetView = new TweetView(MainActivity.this, result.data);
                parentView.addView(tweetView);
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Load Tweet failure", exception);
            }
        });
        */


        // check if the intent is here


        try {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();

            String new_zip = extras.getString("ZIP");
            mCurrentZip = new_zip;

            // Regenerate ZIPs until we find a valid one.
            while (true) {
                String county_state = getCountyName(mCurrentZip);
                Log.v("T", "County: " + county_state);
                String rep_data = getRepData(mCurrentZip);
                Log.v("T", "Reps: " + rep_data);
                String finalString = mCurrentZip + "/" + county_state + "/" + rep_data;

                // Check if either count is 0, or county_name is missing. This makes it invalid.
                String[] county_words = county_state.split("/");
                String[] rep_words = rep_data.split("/");

                if (rep_words[0].equals("0") || county_words[0].equals("NoCountyName")) {
                    Log.v("T", "Need to REGENERATE RANDOM ZIP: " );
                    mCurrentZip = getRandomZip();
                    Log.v("T", "Trying with: " + mCurrentZip );
                } else {
                    break;
                }

            }

            Intent sendCongress = new Intent(getBaseContext(), CongressionalViewActivity.class);
            sendCongress.putExtra("ZIP", mCurrentZip);
            startActivity(sendCongress);

            String county_state = getCountyName(mCurrentZip);
            Log.v("T", "County: " + county_state);
            String rep_data = getRepData(mCurrentZip);
            Log.v("T", "Reps: " + rep_data);
            String finalString = mCurrentZip + "/" + county_state + "/" + rep_data;

            Log.v("T", "Sending new string: " + finalString);

            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            sendIntent.putExtra("ZIP", finalString);
            startService(sendIntent);


        } catch (Exception e) {
            // in this case, just continue
            Log.v("T", "Original Page: No New Zip Data");
        }



        final TextView mCurrentLoc = (TextView) findViewById(R.id.current_loc_text);

        mCurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("T", "Using current location...  " + mCurrentZip);

                Intent sendCongress = new Intent(getBaseContext(), CongressionalViewActivity.class);
                sendCongress.putExtra("ZIP", mCurrentZip);
                startActivity(sendCongress);


                String county_state = getCountyName(mCurrentZip);
                Log.v("T", "County: " + county_state);
                String rep_data = getRepData(mCurrentZip);
                Log.v("T", "Reps: " + rep_data);
                String finalString = mCurrentZip + "/" + county_state + "/" + rep_data;

                Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                sendIntent.putExtra("ZIP", finalString);
                startService(sendIntent);
            }
        });



        mSubmitButton = (Button) findViewById(R.id.submit_button);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                mZipCode = (EditText) findViewById(R.id.zipCode);
                //Integer zipCode = Integer.parseInt(mZipCode.getText().toString());
                String mEnteredZip = mZipCode.getText().toString();
                Log.v("T", "Entered Zip Code: " + mEnteredZip);

                Intent sendCongress = new Intent(getBaseContext(), CongressionalViewActivity.class);
                sendCongress.putExtra("ZIP", mEnteredZip);
                startActivity(sendCongress);

                String county_state = getCountyName(mEnteredZip);
                Log.v("T", "County: " + county_state);
                String rep_data = getRepData(mEnteredZip);
                Log.v("T", "Reps: " + rep_data);
                String finalString = mEnteredZip + "/" + county_state + "/" + rep_data;

                Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                sendIntent.putExtra("ZIP", finalString);
                startService(sendIntent);

                Log.v("T", "pressed button");
            }
        });

        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.

        //
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //String.format("%s: %f", mLatitudeLabel, mLastLocation.getLatitude());
            //String.format("%s: %f", mLongitudeLabel, mLastLocation.getLongitude());

            mLastLatitude = mLastLocation.getLatitude();
            mLastLongtide = mLastLocation.getLongitude();

            Log.v("T", "Latitude: " + mLastLatitude);
            Log.v("T", "Longitude: " + mLastLongtide);

            Geocoder geocoder = new Geocoder(getApplicationContext());
            // lat,lng, your current location
            try {
                List<Address> addresses = geocoder.getFromLocation(mLastLatitude, mLastLongtide, 1);
                // Grab the zip code of the first address
                String zipString = addresses.get(0).getPostalCode();
                Log.v("ZIP: ", zipString);
                mCurrentZip = zipString;
            } catch (Exception e) {
                Log.v("T", "Couldn't get ZIP Code");
            }

        } else {
            Toast.makeText(this, "Cant detect location", Toast.LENGTH_LONG).show();
        }
        //
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.v("T", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.v("Main Activity: ", "Connection suspended");
        mGoogleApiClient.connect();
    }


    public String getCountyName (String zip) {

        String county_name = "NoCountyName";
        String state_name = "";
        try {
            String countyURL = "http://maps.googleapis.com/maps/api/geocode/json?address=" + zip + "&sensor=true";

            DownloadWebpageTask countyWebpage = new DownloadWebpageTask();
            String result = countyWebpage.execute(countyURL).get();

            JSONObject jObject = new JSONObject(result);
            JSONArray jArray = jObject.getJSONArray("results");
            JSONObject mainAddress = jArray.getJSONObject(0);
            JSONArray addressComps = mainAddress.getJSONArray("address_components");

            for (int i=0; i < addressComps.length(); i++) {

                JSONObject addressComp = addressComps.getJSONObject(i);

                JSONArray types = addressComp.getJSONArray("types");
                String type_string = types.getString(0);

                if (type_string.equals("administrative_area_level_2")) {
                    county_name = addressComp.getString("short_name");
                } else if (type_string.equals("administrative_area_level_1")) {
                    state_name = addressComp.getString("short_name");
                    break;
                }
            }

            String finalName = county_name + "/" + state_name;
            return finalName;

        } catch (Exception e) {
            Log.v("T", "Couldn't read Sunlight bill data: Exception: " + e);
        }
        return "";
    }

    public String getRandomZip() {

        String zip = "91354";

        Random r = new Random();
        int random_i = r.nextInt(43582) + 1; // make sure we don't select the header.
        Log.v("T", "Random_i: " + random_i);
        try {
            InputStream is = getAssets().open("us_postal_codes.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);

            String line = "";

            int count = 0;

            while ((line = br.readLine()) != null) {

                if (count < random_i) {
                    count += 1;
                } else {
                    String[] words = line.split(",");
                    return words[0];
                }
            }
        } catch (Exception e) {

        }
        return zip;
    }

    public String getRepData(String zip) {

        try {
            String stringUrl = "http://congress.api.sunlightfoundation.com/legislators/locate?zip=" + zip + "&apikey=d95aad655d6e4990a811453fe43b134f";
            DownloadWebpageTask a = new DownloadWebpageTask();
            String result = a.execute(stringUrl).get();
            Log.v("T", "Read JSON from Sunlight: " + result);

            JSONObject jObject = new JSONObject(result);
            JSONArray jArray = jObject.getJSONArray("results");
            int count = jObject.getInt("count");

            // going to have count
            // then Name/Party/Info

            String[] RepresentativeData = new String[count * 3];


            for (int i=0; i < jArray.length(); i++) {
                try {
                    JSONObject repData = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String first_name = repData.getString("first_name");
                    String last_name = repData.getString("last_name");
                    String party = repData.getString("party");
                    String chamber = repData.getString("chamber");

                    RepresentativeData[3 * i + 0] = first_name + " " + last_name;
                    RepresentativeData[3 * i + 1] = party;
                    RepresentativeData[3 * i + 2] = chamber;

                } catch (JSONException e) {
                    // Oops
                }
            }

            String finalString = count + "";
            for (int j=0; j < RepresentativeData.length; j++) {
                finalString += "/" + RepresentativeData[j];
            }
            return finalString;

        } catch (Exception e) {
            Log.v("T", "Couldn't read Sunlight webpage: Exception: " + e);
        }
        return "";
    }


}



