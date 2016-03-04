package com.example.rcasey.findmyreps;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import android.content.Intent;
import android.support.wearable.view.GridViewPager;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.content.Context;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;

import android.support.wearable.view.DotsPageIndicator;

import java.util.Random;


public class MainGridViewPager extends Activity implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mSensor;

    private double lastX_acceleration = 0.0;
    private double lastY_acceleration = 0.0;
    private double lastZ_acceleration = 0.0;

    private Integer mZipCode;

    private GridViewPager mPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_grid_view_pager);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final String zipString = extras.getString("ZIP");
        mZipCode = Integer.parseInt(zipString);


        mPager = (GridViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SampleGridPageAdapter(this, mZipCode));

        final DotsPageIndicator dotsIndicator = (DotsPageIndicator)findViewById(R.id.page_indicator);
        dotsIndicator.setPager(mPager);
        dotsIndicator.setDotColor(-16777216);
        dotsIndicator.setDotFadeWhenIdle(false);

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


    // call to update the data;
    public void updateData() {
        mPager.setAdapter(new SampleGridPageAdapter(this, mZipCode));
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


                // Generate a new 5 digit number between 10000 and 99999
                Random r = new Random();
                int new_random_zip = r.nextInt(99999 - 10000) + 10000;
                Log.d("T", "New random zip: " + new_random_zip);

                Log.d("T", "Sending New Zip Intent");
                Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtra("MESSAGE_TYPE", "ZIP");
                sendIntent.putExtra("ZIP", new_random_zip);
                mZipCode = new_random_zip;
                updateData();
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
}
