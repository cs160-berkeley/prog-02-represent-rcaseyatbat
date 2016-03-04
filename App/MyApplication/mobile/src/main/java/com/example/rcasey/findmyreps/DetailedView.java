package com.example.rcasey.findmyreps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.ImageView;

import org.w3c.dom.Text;

public class DetailedView extends AppCompatActivity {

    Button mButton;
    TextView mTextView;
    TextView mNameText;
    TextView mPartyText;
    TextView mTermText;
    ImageView mImage;

    String rep_name = "";
    String rep_party = "";
    int rep_image = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        mButton = (Button) findViewById(R.id.back_button);
        mTextView = (TextView) findViewById(R.id.zip_text);
        mNameText = (TextView) findViewById(R.id.name_text);
        mPartyText = (TextView) findViewById(R.id.party_text);
        mTermText = (TextView) findViewById(R.id.term_text);
        mImage = (ImageView) findViewById(R.id.rep_image);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        final Integer selection = Integer.parseInt(extras.getString("Item"));
        final Integer zip = Integer.parseInt(extras.getString("ZIP"));
        Log.v("T", "Inside selection: " + selection);

        mTextView.setText("ZIP: " + zip.toString());

        if (selection  == 0) {
            //mNameText.setText();
            rep_name = "Barbara Boxer";
            rep_party = "Democrat";
            rep_image = R.drawable.boxer;

        } else if (selection == 1) {
            rep_name = "Diane Feinstein";
            rep_party = "Democrat";
            rep_image = R.drawable.feinstein;

        } else if (selection == 2) {
            rep_name = "Steve Knight";
            rep_party = "Republican";
            rep_image = R.drawable.knight;
        }

        mNameText.setText(rep_name);
        mPartyText.setText("Party: " + rep_party);
        mImage.setImageResource(rep_image);

        mButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                // Go back to the Congressional View Page
                Intent sendCongress = new Intent(getBaseContext(), CongressionalViewActivity.class);
                sendCongress.putExtra("ZIP", zip);
                startActivity(sendCongress);

            }
        });


        final ListView committee_list = (ListView) findViewById(R.id.committee_listView);

        // storing string resources into Array
        String[] items = {"Committee on the Environment","Leglislative Bill #2","Committee of Foreign Affairs","Leglislative Bill #3"};

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        committee_list.setAdapter(itemsAdapter);

    }
}
