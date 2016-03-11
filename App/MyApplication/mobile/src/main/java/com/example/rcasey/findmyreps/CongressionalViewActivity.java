package com.example.rcasey.findmyreps;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.widget.Button;


import java.io.*;
import android.os.StrictMode;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import io.fabric.sdk.android.Fabric;


public class CongressionalViewActivity extends AppCompatActivity {


    private Button mButton;
    private String mZip;

    private int mCount = 0;
    private int mLength = 0;

    private String[] mCongressNames;
    private String[] mCongressBioguides;
    private String[] mCongressParty;
    private String[] mCongressInfo;
    private String[] mCongressState;
    private String[] mCongressEmails;
    private String[] mCongressWebsites;
    private String[] mCongressTwitterNames;
    private String[] mCongressTweets;


    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "0ZN9JyoD5UVVderOLlK1DEU0g";
    private static final String TWITTER_SECRET = "LafwHUMEX6TktwGAPyctkAYSism2EioZgoUpe4ViqoVgLv35Vm";


    public void onDone(Vector <String> tweetVec) {

        if (mCount == mLength) {
            Log.v("T", "onDone: " + tweetVec);

            final ListView congressList = (ListView) findViewById(R.id.CongresslistView);

            mCongressTweets = new String[mLength];
            for (int i = 0; i < mLength; i++) {
                String id_and_tweet = tweetVec.get(i);
                String[] words = id_and_tweet.split("/", 2);
                String twitterID = words[0];
                String twitterTweet = words[1];

                for (int j = 0; j < mLength; j++) {
                    if (twitterID.equals(mCongressTwitterNames[j])) {
                        mCongressTweets[j] = twitterTweet;
                    }
                }
                //mCongressTweets[i] = tweetVec.get(i);
            }

            //Create the Adapter
            final CongressAdapter adapter = new CongressAdapter(this, mCongressNames, mCongressBioguides, mCongressParty,
                    mCongressInfo, mCongressState, mCongressEmails, mCongressWebsites, mCongressTwitterNames, mCongressTweets, mZip);

            //Set the Adapter
            congressList.setAdapter(adapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        //Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_congressional_view_activity);

        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                Log.v("T", "pressed back button");
                // Go back to the Main Activity Page View Page
                Intent sendMain = new Intent(getBaseContext(), MainActivity.class);
                sendMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(sendMain);
            }
        });

        final ListView congressList = (ListView) findViewById(R.id.CongresslistView);


        String[] congressNames = {};
        String[] congressBioguides = {};
        String[] congressParty = {};
        String[] congressInfo = {};
        String[] congressEmails = {};
        String[] congressWebsites = {};
        String[] congressTwitterNames = {};
        String[] congressState = {};

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final String zipCode = extras.getString("ZIP");
        mZip = zipCode;


        TextView zipText = (TextView) findViewById(R.id.zip_text);
        zipText.setText("ZIP " + zipCode + " represented by:");


        try {
            String stringUrl = "http://congress.api.sunlightfoundation.com/legislators/locate?zip=" + zipCode + "&apikey=d95aad655d6e4990a811453fe43b134f";
            DownloadWebpageTask a = new DownloadWebpageTask();
            String result = a.execute(stringUrl).get();
            Log.v("T", "Read JSON from Sunlight: " + result);

            JSONObject jObject = new JSONObject(result);
            JSONArray jArray = jObject.getJSONArray("results");
            int count = jObject.getInt("count");
            mLength = count;
            congressNames = new String[count];
            congressBioguides = new String[count];
            congressParty = new String[count];
            congressInfo = new String[count];
            congressState = new String[count];
            congressEmails = new String[count];
            congressWebsites = new String[count];
            congressTwitterNames = new String[count];


            for (int i=0; i < jArray.length(); i++) {
                try {
                    JSONObject repData = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String bioguide = repData.getString("bioguide_id");
                    String first_name = repData.getString("first_name");
                    String last_name = repData.getString("last_name");
                    String party = repData.getString("party");
                    String chamber = repData.getString("chamber");
                    String state = repData.getString("state");
                    String email = repData.getString("oc_email");
                    String website = repData.getString("website");
                    String twitterName = repData.getString("twitter_id");

                    congressNames[i] = first_name + " " + last_name;
                    congressBioguides[i] = bioguide;
                    congressParty[i] = party;
                    congressInfo[i] = chamber;
                    congressState[i] = state;
                    congressEmails[i] = email;
                    congressWebsites[i] = website;
                    congressTwitterNames[i] = twitterName;


                } catch (JSONException e) {
                    // Oops
                }
            }

            mCongressNames = congressNames;
            mCongressBioguides = congressBioguides;
            mCongressParty = congressParty;
            mCongressInfo = congressInfo;
            mCongressState = congressState;
            mCongressEmails = congressEmails;
            mCongressWebsites = congressWebsites;
            mCongressTwitterNames = congressTwitterNames;

            //String[] tweetTexts = new String[congressTwitterNames.length];

            final String[] items = congressTwitterNames;
            TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {

                Vector<String> TweetVec = new Vector<>(4);

                @Override
                public void success(Result<AppSession> appSessionResult) {
                    int i = 0;
                    AppSession session = appSessionResult.data;
                    TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);

                    //public Vector<String> TweetVec2 = new Vector<>(4);

                    for (i = 0; i < items.length; i++) {
                        //Log.v("T", "Got a Tweet! " + congressTwitterNames );
                        final String twitID = items[i];
                        twitterApiClient.getStatusesService().userTimeline(null, twitID, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {

                            @Override
                            public void success(Result<List<Tweet>> listResult) {
                                //adapter.setTweets(listResult.data);
                                for (Tweet Tweet : listResult.data) {
                                    //tweetList.add(Tweet.text);
                                    Log.v("T", "Got a Tweet! " + twitID);
                                    Log.v("T", "Got a Tweet! " + Tweet.text);
                                    Log.v("T", "Got a Tweet! " + Tweet.id);
                                    TweetVec.add(twitID +"/"+Tweet.text);
                                    mCount += 1;
                                    onDone(TweetVec);

                                    //TweetVec.add(Tweet.text);
                                }
                            }

                            @Override
                            public void failure(TwitterException e) {
                                //Toast.makeText(getActivity().getApplicationContext(), "Could not retrieve tweets", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        });
                        Log.v("T", "Current Tweet Vec: " + TweetVec);
                    }

                    Log.v("T", "Tweet Vec Texts: " + TweetVec);

                }

                @Override
                public void failure(TwitterException e) {
                    //Toast.makeText(getActivity().getApplicationContext(), "Could not get guest Twitter session", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });


            /*
            GetTweet twittersync = new GetTweet();
            twittersync.execute(congressTwitterNames[0]).get();
            */


            /*
            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
            for (int i = 0; i < congressTwitterNames.length; i++) {
                final String twitterID = congressTwitterNames[i];

                Log.v("T", "Grabbing tweet for... " + twitterID );
                twitterApiClient.getStatusesService().userTimeline(null, twitterID, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {

                    @Override
                    public void success(Result<List<Tweet>> result) {
                        //Do something with result, which provides a Tweet inside of result.data

                        for(Tweet Tweet : result.data) {
                            //tweetList.add(Tweet.text);
                            Log.v("T", "Got a Tweet! " + twitterID );
                            Log.v("T", "Got a Tweet! " + Tweet.text );
                            Log.v("T", "Got a Tweet! " + Tweet.id);
                            //TweetVec.add(Tweet.text);
                        }
                    }

                    public void failure(TwitterException exception) {
                        //Do something on failure
                        Log.v("T", "Failed! " + exception );
                    }
                });
            }
            */


        } catch (Exception e) {
            Log.v("T", "Couldn't read Sunlight webpage: Exception: " + e);
        }


        /*
        //Create the Adapter
        final CongressAdapter adapter = new CongressAdapter(this, congressNames, congressPhotos, congressParty,
                congressInfo, congressEmails, congressWebsites, mZip);

        //Set the Adapter
        congressList.setAdapter(adapter);
        */

    }

}
