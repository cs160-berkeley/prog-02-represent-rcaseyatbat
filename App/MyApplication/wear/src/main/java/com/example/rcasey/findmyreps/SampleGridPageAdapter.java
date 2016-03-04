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

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;


class SampleGridPageAdapter extends GridPagerAdapter {

    Context context;
    Integer mZip;


    public SampleGridPageAdapter(Context context, Integer zip) {

        this.context = context;
        this.mZip = zip;

    }

    @Override
    public int getColumnCount(int arg0) {
        return 2;
    }

    @Override
    public int getRowCount() {
        return 3;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int row, int col) {

        final View view;
        if (col == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.vote2012, container, false);

            final TextView zipText = (TextView) view.findViewById(R.id.zip_text);
            zipText.setText("(ZIP " + mZip + ")");

        } else {
            view = LayoutInflater.from(context).inflate(R.layout.grid_view_pager_item, container, false);

            if (row == 0) {
                final ImageView image = (ImageView) view.findViewById(R.id.imageView);
                image.setImageResource(R.drawable.boxer);

                final TextView nameTextView = (TextView) view.findViewById(R.id.name_text);
                nameTextView.setText("Barbara Boxer");
                final TextView partyTextView = (TextView) view.findViewById(R.id.party_text);
                partyTextView.setText("Democrat");
                final TextView SenatorTextView = (TextView) view.findViewById(R.id.senator_text);
                SenatorTextView.setText("Senator, CA" + " (" + mZip + ")");
                final TextView numberTextView = (TextView) view.findViewById(R.id.rep_number_text);
                numberTextView.setText("(Rep 1 of 3)");
            } else if (row == 1) {
                final ImageView image = (ImageView) view.findViewById(R.id.imageView);
                image.setImageResource(R.drawable.feinstein);

                final TextView nameTextView = (TextView) view.findViewById(R.id.name_text);
                nameTextView.setText("Diane Feinstein");
                final TextView partyTextView = (TextView) view.findViewById(R.id.party_text);
                partyTextView.setText("Democrat");
                final TextView SenatorTextView = (TextView) view.findViewById(R.id.senator_text);
                SenatorTextView.setText("Senator, CA" + " (" + mZip + ")");
                final TextView numberTextView = (TextView) view.findViewById(R.id.rep_number_text);
                numberTextView.setText("(Rep 2 of 3)");
            } else if (row == 2) {
                final ImageView image = (ImageView) view.findViewById(R.id.imageView);
                image.setImageResource(R.drawable.knight);

                final TextView nameTextView = (TextView) view.findViewById(R.id.name_text);
                nameTextView.setText("Steve Knight");
                final TextView partyTextView = (TextView) view.findViewById(R.id.party_text);
                partyTextView.setText("Republican");
                final TextView SenatorTextView = (TextView) view.findViewById(R.id.senator_text);
                SenatorTextView.setText("Representative, CA" + " (" + mZip + ")");
                final TextView numberTextView = (TextView) view.findViewById(R.id.rep_number_text);
                numberTextView.setText("(Rep 3 of 3)");

            }
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

