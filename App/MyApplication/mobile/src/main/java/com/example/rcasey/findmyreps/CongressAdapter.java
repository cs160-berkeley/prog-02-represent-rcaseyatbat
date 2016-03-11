package com.example.rcasey.findmyreps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.StrictMode;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import android.view.View.OnClickListener;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;

import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;

import io.fabric.sdk.android.Fabric;
//import android.content.DialogInterface.OnClickListener;



/**
 * Created by rcasey on 2/28/16.
 */
//
public class CongressAdapter extends BaseAdapter {

    Context context;


    String[] congressNames;
    String[] congressBioguides;
    String[] congressParty;
    String[] congressInfo;
    String[] congressState;
    String[] congressEmails;
    String[] congressWebsites;
    String[] congressTwitterIDs;
    String[] congressTweets;
    Integer mZipCode;


    public CongressAdapter(Context context, String[] congressNames, String[] congressBioguides, String[] congressParty,
                           String[] congressInfo, String [] congressState, String[] congressEmails, String[] congressWebsites,
                           String[] congressTwitterIDs, String[] congressTweets, String zipCode) {


        this.context = context;
        this.congressNames = congressNames;
        this.congressBioguides = congressBioguides;
        this.congressParty = congressParty;
        this.congressInfo = congressInfo;
        this.congressState = congressState;
        this.congressEmails = congressEmails;
        this.congressWebsites = congressWebsites;
        this.congressTwitterIDs = congressTwitterIDs;
        this.congressTweets = congressTweets;
        try {
            this.mZipCode = Integer.parseInt(zipCode);
        } catch (Exception e) {
            this.mZipCode = 11111;
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int id = position;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View congressRow = inflater.inflate(R.layout.congress_rows, parent, false);

        TextView nameView = (TextView) congressRow.findViewById(R.id.name_text);
        nameView.setText(congressNames[position]);

        TextView partyView = (TextView) congressRow.findViewById(R.id.party_text);
        if (congressParty[position].equals("D")) {
            partyView.setText("Party: Democrat");
        } else if (congressParty[position].equals("R")) {
            partyView.setText("Party: Republican");
        } else {
            partyView.setText("Party: Independent");
        }

        TextView infoView = (TextView) congressRow.findViewById(R.id.info_text);
        if (congressInfo[position].equals("house")) {
            infoView.setText("House, " + congressState[position]);
        } else {
            infoView.setText("Senate, " + congressState[position]);
        }

        TextView emailView = (TextView) congressRow.findViewById(R.id.email_text);
        emailView.setText(congressEmails[position]);

        TextView websiteView = (TextView) congressRow.findViewById(R.id.website_text);
        websiteView.setText(congressWebsites[position]);

        TextView twitterIDView = (TextView) congressRow.findViewById(R.id.twitterID_text);
        twitterIDView.setText("Lastest tweet from @" + congressTwitterIDs[position]);

        TextView tweetView = (TextView) congressRow.findViewById(R.id.tweet_text);
        tweetView.setText(congressTweets[position]);

        ImageView repImageView = (ImageView) congressRow.findViewById(R.id.rep_image);
        String bioguideID = congressBioguides[position];
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String imageURL = "https://theunitedstates.io/images/congress/225x275/" + bioguideID + ".jpg";
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageURL).getContent());

            repImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.v("T", "Couldn't load image: " + e);
        }

        repImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                Log.v("T", "clicked image button... " + id);
                Intent sendDetailed = new Intent(context, DetailedView.class);
                sendDetailed.putExtra("ZIP", mZipCode.toString());
                sendDetailed.putExtra("Item", Long.toString(id));
                sendDetailed.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(sendDetailed);
            }
        });



        return congressRow;
    }

    @Override
    public String getItem(int position) {
        return congressNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return congressNames.length;
    }

}

