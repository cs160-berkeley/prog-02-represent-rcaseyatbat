package com.example.rcasey.findmyreps;

import android.media.Image;
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

import org.w3c.dom.Text;
//import android.content.DialogInterface.OnClickListener;



/**
 * Created by rcasey on 2/28/16.
 */
//
public class CongressAdapter extends BaseAdapter {

    Context context;

    String[] congressNames;
    int[] congressPhotos;
    String[] congressParty;
    String[] congressInfo;
    String[] congressEmails;
    String[] congressWebsites;
    Integer mZipCode;

    public CongressAdapter(Context context, String[] congressNames, int[] congressPhotos, String[] congressParty,
                           String[] congressInfo, String[] congressEmails, String[] congressWebsites, Integer zipCode) {

        this.context = context;
        this.congressNames = congressNames;
        this.congressPhotos = congressPhotos;
        this.congressParty = congressParty;
        this.congressInfo = congressInfo;
        this.congressEmails = congressEmails;
        this.congressWebsites = congressWebsites;
        this.mZipCode = zipCode;
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
        partyView.setText(congressParty[position]);

        TextView infoView = (TextView) congressRow.findViewById(R.id.info_text);
        infoView.setText(congressInfo[position]);

        TextView emailView = (TextView) congressRow.findViewById(R.id.email_text);
        emailView.setText(congressEmails[position]);

        TextView websiteView = (TextView) congressRow.findViewById(R.id.website_text);
        websiteView.setText(congressWebsites[position]);

        ImageView repImageView = (ImageView) congressRow.findViewById(R.id.rep_image);
        repImageView.setImageResource(congressPhotos[position]);

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

