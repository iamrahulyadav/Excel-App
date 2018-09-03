package com.ansoft.excelapplication;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.ansoft.excelapplication.Data.VefListAdapter;
import com.ansoft.excelapplication.Data.Vefdata;
import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class VEFFragment extends Fragment {


    ArrayList<Vefdata> list;
    Workbook workbook;
    ListView listView;
    public VEFFragment() {
    }
    @SuppressLint("ValidFragment")
    public VEFFragment(Workbook workbook){
        this.workbook=workbook;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_vef, container, false);
        listView=(ListView)view.findViewById(R.id.listView);
        list=new ArrayList<>();
        for (int i=0; i<20; i++){
            int rowNumber=12+i;
            Cells cells = workbook.getWorksheets().get(3).getCells();
            Vefdata vefdata=new Vefdata();
            vefdata.setVoyageNumber(cells.get("C"+rowNumber).getStringValue());
            vefdata.setLoadPort(cells.get("E"+rowNumber).getStringValue());
            vefdata.setCargo(cells.get("F"+rowNumber).getStringValue());
            vefdata.setBargeVol(cells.get("G"+rowNumber).getStringValue());
            vefdata.setShoreVol(cells.get("H"+rowNumber).getStringValue());
            vefdata.setVefFactor(cells.get("I"+rowNumber).getStringValue());
            vefdata.setPass(cells.get("J"+rowNumber).getStringValue());
            list.add(vefdata);
        }
        VefListAdapter adapter=new VefListAdapter(list, getActivity());
        listView.setAdapter(adapter);
        return view;
    }

}
