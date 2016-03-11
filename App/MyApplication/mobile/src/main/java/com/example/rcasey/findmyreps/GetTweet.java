package com.example.rcasey.findmyreps;

import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.util.Log;

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by rcasey on 3/10/16.
 */
public class GetTweet extends AsyncTask<String, Void, String> {

    String mResult;
    String mTweet;


    @Override
    protected String doInBackground(String... urls) {
        // params comes from the execute() call: params[0] is the url.
        try {
            mResult = findTweet(urls[0]);
            Log.v("T", "found! " + mTweet);
            return mResult;
        } catch (IOException e) {
            mResult = "No Twitter Available";
            return mResult;
        }
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Log.v("T", "PostExecute! " + mTweet);

    }


    private String findTweet(final String twitID) throws IOException {


        TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> appSessionResult) {

                Log.v("T", "Entered session!! " );
                AppSession session = appSessionResult.data;
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);

                twitterApiClient.getStatusesService().userTimeline(null, twitID, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {

                    @Override
                    public void success(final Result<List<Tweet>> listResult) {
                        //adapter.setTweets(listResult.data);
                        for (Tweet Tweet : listResult.data) {
                            //tweetList.add(Tweet.text);
                            Log.v("T", "Got a Tweet! " + twitID);
                            Log.v("T", "Got a Tweet! " + Tweet.text);
                            Log.v("T", "Got a Tweet! " + Tweet.id);

                            mTweet = Tweet.text;
                            Log.v("T", "Now, MTweet! " + mTweet);
                            //TweetVec.add(Tweet.text);
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        //Toast.makeText(getActivity().getApplicationContext(), "Could not retrieve tweets", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                //Toast.makeText(getActivity().getApplicationContext(), "Could not get guest Twitter session", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        return "";
    }
}



