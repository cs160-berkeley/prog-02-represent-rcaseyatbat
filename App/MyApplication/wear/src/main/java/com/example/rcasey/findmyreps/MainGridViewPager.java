package com.example.rcasey.findmyreps;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import android.content.Intent;
import android.support.wearable.view.GridViewPager;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.content.Context;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;

import android.support.wearable.view.DotsPageIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

import android.location.Geocoder;


public class MainGridViewPager extends Activity implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mSensor;

    private double lastX_acceleration = 0.0;
    private double lastY_acceleration = 0.0;
    private double lastZ_acceleration = 0.0;

    private Integer mZipCode;
    private String mZipString;
    private String mState;
    private String mCountyName;
    private String[] mRepNames;
    private String[] mRepParties;
    private String[] mRepChambers;

    private String mObama = "N/A";
    private String mRomney = "N/A";

    private GridViewPager mPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_grid_view_pager);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        final String wholeString = extras.getString("ZIP");
        String[] words = wholeString.split("/");
        String zipString = words[0];
        String countyName = words[1];
        String stateName = words[2];
        int count = Integer.parseInt(words[3]);

        String[] rep_names = new String[count];
        String[] rep_parties = new String[count];
        String[] rep_chambers = new String[count];
        for (int i=0; i < count; i++) {
            String rep_name = words[3 * i + 4];
            String rep_party = words[3 * i + 5];
            String rep_chamber = words[3 * i + 6];
            rep_names[i] = rep_name;
            if (rep_party.equals("D")) {
                rep_parties[i] = "Democrat";
            } else if (rep_party.equals("R")) {
                rep_parties[i] = "Republican";
            } else {
                rep_parties[i] = "Independent";
            }
            if (rep_chamber.equals("house")) {
                rep_chambers[i] = "House";
            } else {
                rep_chambers[i] = "Senate";
            }
        }



        mZipCode = Integer.parseInt(zipString);
        mZipString = zipString;
        if (mZipCode < 10000) {
            mZipString = "0" + mZipString;
        }
        mCountyName = countyName;
        mState = stateName;
        mRepNames = rep_names;
        mRepParties = rep_parties;
        mRepChambers = rep_chambers;

        // Call this function to grab the 2012 Vote Data;
        getVoteData(mState, mCountyName);


        mPager = (GridViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SampleGridPageAdapter(this, mZipCode, mState, mCountyName, mRepNames, mRepParties, mRepChambers, mObama, mRomney));

        final DotsPageIndicator dotsIndicator = (DotsPageIndicator)findViewById(R.id.page_indicator);
        dotsIndicator.setPager(mPager);
        dotsIndicator.setDotFadeWhenIdle(false);

        Geocoder geocoder = new Geocoder(getApplicationContext());
        Log.v("T", "Current zip: " + mZipString);


        mPager.setOnPageChangeListener(
                new GridViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageSelected(int i, int j) {
                        Log.v("T", "Selected: " + i + " , " + j);
                        dotsIndicator.onPageSelected(i, j);

                        // if we swiped vertically, send the current selection to the phone
                        if (j == 0) {
                            Log.d("T", "Sending New Selection: " + i);
                            Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                            sendIntent.putExtra("MESSAGE_TYPE", "SELECTION");
                            sendIntent.putExtra("I", i);
                            sendIntent.putExtra("ZIP", mZipCode);
                            startService(sendIntent);
                        }

                    }

                    @Override
                    public void onPageScrolled(int row, int column, float rowOffset, float columnOffset, int rowOffsetPixels, int columnOffsetPixels) {
                        dotsIndicator.onPageScrolled(row, column, rowOffset, columnOffset, rowOffsetPixels, columnOffsetPixels);
                        return;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        dotsIndicator.onPageScrollStateChanged(state);
                        return;
                    }


                });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        // Do something with this sensor value.

        // don't update based on the first reading.
        if (lastX_acceleration == 0 && lastY_acceleration == 0 && lastZ_acceleration == 0) {
            lastX_acceleration = event.values[0];
            lastY_acceleration = event.values[1];
            lastZ_acceleration = event.values[2];
            return;
        }

        // only update if we have large accelerations that are different from the previous reading
        if (event.values[0] >= 100 && event.values[1] >= 100) {
            if (lastX_acceleration != event.values[0] || lastY_acceleration != event.values[1] ||
                    lastZ_acceleration != event.values[2]) {

                Log.v("T", "last was: " + lastX_acceleration + "  " + lastY_acceleration + "  " + lastZ_acceleration);
                // Record the last known accelerometer readings, so we know if the next shake is
                // different.
                lastX_acceleration = event.values[0];
                lastY_acceleration = event.values[1];
                lastZ_acceleration = event.values[2];

                Log.v("T", "changed to: " + event.values[0] + "  " + event.values[1] + "  " + event.values[2]);

                String new_random_zip = getRandomZip();
                Log.d("T", "New random zip: " + new_random_zip);

                Log.d("T", "Sending New Zip Intent");
                Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtra("MESSAGE_TYPE", "ZIP");
                sendIntent.putExtra("ZIP", new_random_zip);
                //mZipCode = Integer.parseInt(new_random_zip);

                startService(sendIntent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
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

    public void getVoteData (String aState, String aCounty) {

        try {
            InputStream stream = getAssets().open("election-county-2012.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String jsonString = new String(buffer, "UTF-8");
            //Log.v("T", "Json result: " + jsonString);
            //Log.v("T", "Json size: " + size);
            JSONArray jArray = new JSONArray(jsonString);


            aCounty = aCounty.substring(0, aCounty.length() - 7);
            Log.v("T", "Looking for: " + aState + "  " + aCounty + "!");

            JSONObject voteData = null;
            for (int i=0; i < jArray.length(); i++) {
                voteData = jArray.getJSONObject(i);
                String state = voteData.getString("state-postal");
                String county_name = voteData.getString("county-name");

                if (county_name.contains(aCounty) && state.equals(aState)) {
                    Log.v("T", "FOUND CITY");
                    double obama_vote = voteData.getDouble("obama-percentage");
                    double romney_vote = voteData.getDouble("romney-percentage");
                    mObama = obama_vote + "%";
                    mRomney = romney_vote + "%";
                    Log.v("T", "Vote data: " + obama_vote + "  " + romney_vote);
                    break;
                }
            }

        } catch (Exception e) {
            Log.v("T", "Could not open vote data: " + e);
        }


    }
}
