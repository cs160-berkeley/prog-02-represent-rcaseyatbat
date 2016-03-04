package com.example.rcasey.findmyreps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private Button mSubmitButton;

    private EditText mZipCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final TextView mCurrentLoc = (TextView) findViewById(R.id.current_loc_text);

        mCurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int zipCode = 11111;
                Log.v("T", "Using current location...  ");

                Intent sendCongress = new Intent(getBaseContext(), CongressionalViewActivity.class);
                sendCongress.putExtra("ZIP", zipCode);
                startActivity(sendCongress);

                Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                sendIntent.putExtra("ZIP", zipCode);
                startService(sendIntent);
            }
        });



        mSubmitButton = (Button) findViewById(R.id.submit_button);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                mZipCode = (EditText) findViewById(R.id.zipCode);
                Integer zipCode = Integer.parseInt(mZipCode.getText().toString());
                Log.v("T", "Entered Zip Code: " + zipCode);

                Intent sendCongress = new Intent(getBaseContext(), CongressionalViewActivity.class);
                sendCongress.putExtra("ZIP", zipCode);
                startActivity(sendCongress);


                Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                sendIntent.putExtra("ZIP", zipCode);
                startService(sendIntent);

                Log.v("T", "pressed button");
            }
        });

    }
}
