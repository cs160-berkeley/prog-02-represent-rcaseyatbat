package com.example.rcasey.findmyreps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class DetailedView extends AppCompatActivity {

    Button mButton;
    TextView mTextView;
    TextView mNameText;
    TextView mPartyText;
    TextView mTermText;
    ImageView mImage;

    String mZipString;

    String rep_name = "";
    String rep_party = "";
    String rep_term = "2016-11-2";
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

        Log.v("T", "Inside selection: " + selection + "  for zip: " + zip);

        String zipString = zip.toString();
        if (zip < 10000) {
            zipString = "0" + zipString;
        }
        mZipString = zipString;

        mTextView.setText("ZIP: " + mZipString);

        String bioguideID = null;

        // Get the info from the Congressional Representative at index "selection"
        // Importantly, grab the bioguide_id
        try {
            String stringUrl = "http://congress.api.sunlightfoundation.com/legislators/locate?zip=" + mZipString + "&apikey=d95aad655d6e4990a811453fe43b134f";
            DownloadWebpageTask a = new DownloadWebpageTask();
            String result = a.execute(stringUrl).get();
            Log.v("T", "Read JSON from Sunlight: " + result);

            JSONObject jObject = new JSONObject(result);
            JSONArray jArray = jObject.getJSONArray("results");
            int count = jObject.getInt("count");
            JSONObject repData = jArray.getJSONObject(selection);
            bioguideID = repData.getString("bioguide_id");
            String first_name = repData.getString("first_name");
            String last_name = repData.getString("last_name");
            String party = repData.getString("party");
            String term_end = repData.getString("term_end");

            rep_name = first_name + " " + last_name;
            if (party.equals("D")) {
                rep_party = "Party: Democrat";
            } else if (party.equals("R")) {
                rep_party = "Party: Republican";
            } else {
                rep_party = "Party: Independent";
            }
            rep_term = "Term end: " + term_end;
            Log.v("T", "bioguide: " + bioguideID);

        } catch (Exception e) {
            Log.v("T", "Couldn't read Sunlight webpage: Exception: " + e);
        }


        // Find a list of bills that the member has sponsored
        String[] billList = {};
        try {
            String bioguideURL = "http://congress.api.sunlightfoundation.com/bills/search?sponsor_id=" + bioguideID + "&apikey=d95aad655d6e4990a811453fe43b134f";

            DownloadWebpageTask billsWebpage = new DownloadWebpageTask();
            String result = billsWebpage.execute(bioguideURL).get();
            Log.v("T", "Read JSON from Sunlight: " + result);

            JSONObject jObject = new JSONObject(result);
            JSONArray jArray = jObject.getJSONArray("results");
            int count = jObject.getInt("count");
            billList = new String[count];

            for (int i=0; i < jArray.length(); i++) {
                JSONObject billData = jArray.getJSONObject(i);

                String title = billData.getString("short_title");
                if (title.equals("null")) {
                    title = billData.getString("official_title").toUpperCase();
                }
                String date = billData.getString("introduced_on");
                //Log.v("T", "Read Bill: " + title);
                billList[i] = title + " (" + date + ")";
            }
        } catch (Exception e) {
            Log.v("T", "Couldn't read Sunlight bill data: Exception: " + e);
        }

        // Find the committees that the member serves on
        String[] committeeList = {};
        try {
            String committeeURL = "http://congress.api.sunlightfoundation.com/committees?member_ids=" + bioguideID + "&apikey=d95aad655d6e4990a811453fe43b134f";

            DownloadWebpageTask billsWebpage = new DownloadWebpageTask();
            String result = billsWebpage.execute(committeeURL).get();

            JSONObject jObject = new JSONObject(result);
            JSONArray jArray = jObject.getJSONArray("results");
            int count = jObject.getInt("count");
            committeeList = new String[count];

            for (int i=0; i < jArray.length(); i++) {
                JSONObject billData = jArray.getJSONObject(i);

                String name = billData.getString("name");
                committeeList[i] = name;
            }
        } catch (Exception e) {
            Log.v("T", "Couldn't read Sunlight bill data: Exception: " + e);
        }

        //image url
        //https://theunitedstates.io/images/congress/225x275/D000623.jpg


        mNameText.setText(rep_name);
        mPartyText.setText(rep_party);
        mTermText.setText(rep_term);
        //mImage.setImageResource(rep_image);

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String imageURL = "https://theunitedstates.io/images/congress/225x275/" + bioguideID + ".jpg";
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageURL).getContent());
            mImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.v("T", "Couldn't load image: " + e);
        }


        mButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                // Go back to the Congressional View Page
                Intent sendCongress = new Intent(getBaseContext(), CongressionalViewActivity.class);
                sendCongress.putExtra("ZIP", mZipString);
                sendCongress.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(sendCongress);

            }
        });


        final ListView committee_list = (ListView) findViewById(R.id.committee_listView);

        // Construct a final list of Committees and then Bills
        int finalList_length = committeeList.length + billList.length;
        String[] finalList = new String[finalList_length];

        for (int i=0; i < committeeList.length; i++) {
            finalList[i] = committeeList[i];
        }
        for (int j=0; j < billList.length; j++) {
            finalList[j + committeeList.length] = billList[j];
        }

        // Create and set the adapter
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalList);
        committee_list.setAdapter(itemsAdapter);

    }
}
