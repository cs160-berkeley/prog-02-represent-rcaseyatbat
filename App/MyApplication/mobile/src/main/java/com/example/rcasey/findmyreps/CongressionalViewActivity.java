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

public class CongressionalViewActivity extends AppCompatActivity {


    Button mButton;
    Integer mZip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional_view_activity);

        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                Log.v("T", "pressed back button");
                // Go back to the Main Activity Page View Page
                Intent sendMain = new Intent(getBaseContext(), MainActivity.class);
                startActivity(sendMain);

            }
        });

        final ListView congressList = (ListView) findViewById(R.id.CongresslistView);

        String[] congressNames = {"Barbara Boxer", "Diane Feinstein", "Steve Knight"};
        int[] congressPhotos = {R.drawable.boxer, R.drawable.feinstein, R.drawable.knight};
        String[] congressParty = {"Democrat", "Democrat", "Republican"};
        String[] congressInfo = {"Senator, CA", "Senator, CA", "Representative, CA"};
        String[] congressEmails = {"bboxer@us.gov", "dfeinstein@us.gov", "sknight@us.gov"};
        String[] congressWebsites = {"bboxer.org", "dfeinstein.org", "sknight.org"};

        //Integer[] catFullness = {11, 22, 33};

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final Integer zipCode = extras.getInt("ZIP");
        mZip = zipCode;

        TextView zipText = (TextView) findViewById(R.id.zip_text);
        zipText.setText("ZIP " + zipCode + " represented by:");

        //int[] catPhotos = {R.drawable.cat1, R.drawable.cat2, R.drawable.cat3, R.drawable.cat4, R.drawable.cat5};

        //Create the Adapter

        final CongressAdapter adapter = new CongressAdapter(this, congressNames, congressPhotos, congressParty,
                congressInfo, congressEmails, congressWebsites, mZip);

        //Set the Adapter

        congressList.setAdapter(adapter);

        /*
        congressList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text
                Log.v("T", "clicked item: " + position);

                Intent sendDetailed = new Intent(getBaseContext(), DetailedView.class);
                Log.v("T", "sending intent with ... " + zipCode + "   " + id);
                sendDetailed.putExtra("ZIP", zipCode.toString());
                sendDetailed.putExtra("Item", Long.toString(id));
                startActivity(sendDetailed);
            }
        });
        */
    }

}
