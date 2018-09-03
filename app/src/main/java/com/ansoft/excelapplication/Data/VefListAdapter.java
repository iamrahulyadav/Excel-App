package com.ansoft.excelapplication.Data;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ansoft.excelapplication.R;

import java.util.ArrayList;

/**
 * Created by Abinash on 3/17/2017.
 */

public class VefListAdapter  extends BaseAdapter{

    ArrayList<Vefdata> data;
    Activity activity;

    public VefListAdapter(ArrayList<Vefdata> data, Activity activity) {
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
        View view=activity.getLayoutInflater().inflate(R.layout.item_vef_list, parent, false);
        final Vefdata vefdata=data.get(position);
        TextView voyageNumber=(TextView)view.findViewById(R.id.voyageNumber);
        TextView loadport=(TextView)view.findViewById(R.id.loadport);
        TextView cargo=(TextView)view.findViewById(R.id.cargo);
        TextView bargevol=(TextView)view.findViewById(R.id.bargevol);
        TextView shorevol=(TextView)view.findViewById(R.id.shorevol);
        TextView vefFactor=(TextView)view.findViewById(R.id.vefFactor);
        TextView pass=(TextView)view.findViewById(R.id.pass);

        voyageNumber.setText(voyageNumber.getText().toString()+vefdata.getVoyageNumber());
        loadport.setText(loadport.getText().toString()+vefdata.getLoadPort());
        cargo.setText(cargo.getText().toString()+vefdata.getCargo());
        bargevol.setText(bargevol.getText().toString()+vefdata.getBargeVol());
        shorevol.setText(shorevol.getText().toString()+vefdata.getShoreVol());
        vefFactor.setText(vefFactor.getText().toString()+vefdata.getVefFactor());
        pass.setText(pass.getText().toString()+vefdata.getPass());
        return view;
    }
}
