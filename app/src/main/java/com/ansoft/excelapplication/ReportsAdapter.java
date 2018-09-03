package com.ansoft.excelapplication;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bibek on 4/8/2017.
 */

public class ReportsAdapter extends BaseAdapter {

    ArrayList<String> data;
    Activity activity;

    public ReportsAdapter(ArrayList<String> data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView=activity.getLayoutInflater().inflate(R.layout.item_prev_report, parent, false);
        TextView title=(TextView)convertView.findViewById(R.id.titleeeee);
        title.setText(data.get(position));
        return convertView;
    }
}
