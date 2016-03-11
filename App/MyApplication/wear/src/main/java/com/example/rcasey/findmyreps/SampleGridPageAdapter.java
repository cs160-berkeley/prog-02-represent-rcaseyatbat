package com.example.rcasey.findmyreps;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;


class SampleGridPageAdapter extends GridPagerAdapter {

    Context context;
    Integer mZip;
    String mZipString;
    String mState;
    String mCounty;
    String[] mRep_Names;
    String[] mRep_Parties;
    String[] mRep_Chambers;
    String mObama;
    String mRomney;


    public SampleGridPageAdapter(Context context, Integer zip, String state, String county, String[] rep_names,
                                 String[] rep_parties, String[] rep_chambers, String obama, String romney) {

        this.context = context;
        this.mZip = zip;
        mZipString = zip.toString();
        if (zip < 10000) {
            mZipString = "0" + mZipString;
        }
        this.mState = state;
        this.mCounty = county;
        this.mRep_Names = rep_names;
        this.mRep_Parties = rep_parties;
        this.mRep_Chambers = rep_chambers;
        this.mObama = obama;
        this.mRomney = romney;

    }

    @Override
    public int getColumnCount(int arg0) {


        return 2;
    }

    // The number of rows is the number of representatives
    @Override
    public int getRowCount() {
        return mRep_Chambers.length;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int row, int col) {

        final View view;
        if (col == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.vote2012, container, false);

            final TextView zipText = (TextView) view.findViewById(R.id.zip_text);
            zipText.setText("(ZIP " + mZipString + ")");
            final TextView countyText = (TextView) view.findViewById(R.id.county_text);
            countyText.setText(mCounty + ", " + mState);
            final TextView obamaVoteText = (TextView) view.findViewById(R.id.obama_vote_text);
            obamaVoteText.setText(mObama);
            final TextView romneyVoteText = (TextView) view.findViewById(R.id.romney_vote_text);
            romneyVoteText.setText(mRomney);

        } else {
            view = LayoutInflater.from(context).inflate(R.layout.grid_view_pager_item, container, false);

            final TextView nameTextView = (TextView) view.findViewById(R.id.name_text);
            nameTextView.setText(mRep_Names[row]);
            final TextView partyTextView = (TextView) view.findViewById(R.id.party_text);
            partyTextView.setText(mRep_Parties[row]);
            final TextView SenatorTextView = (TextView) view.findViewById(R.id.senator_text);
            SenatorTextView.setText(mRep_Chambers[row] + ", " + mState + " (" + mZipString + ")");
            final TextView numberTextView = (TextView) view.findViewById(R.id.rep_number_text);
            numberTextView.setText("(Rep " + (row + 1) + " of " + getRowCount() + ")");
        }


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int row, int col, Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view==object;
    }


}

