package com.ansoft.excelapplication;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.aspose.cells.WorksheetCollection;


/**
 * A simple {@link Fragment} subclass.
 */
public class VURFragment extends Fragment {

    Workbook workbook;

    public static EditText portField, terminalField, voyageField, productField, gsField, draftField, aftField;

    public VURFragment() {
    }

    @SuppressLint("ValidFragment")
    public VURFragment(Workbook workbook) {
        this.workbook = workbook;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_vur, container, false);
        portField = (EditText) view.findViewById(R.id.portField);
        terminalField = (EditText) view.findViewById(R.id.terminalField);
        voyageField = (EditText) view.findViewById(R.id.voyageField);
        productField = (EditText) view.findViewById(R.id.productField);
        gsField = (EditText) view.findViewById(R.id.gsField);
        draftField = (EditText) view.findViewById(R.id.draftField);
        aftField = (EditText) view.findViewById(R.id.aftField);


        try {
            WorksheetCollection collection = workbook.getWorksheets();
            Cells cells = collection.get(0).getCells();
            portField.setText(cells.get("C9").getStringValue());
            terminalField.setText(cells.get("C10").getStringValue());
            voyageField.setText(cells.get("C11").getStringValue());
            productField.setText(cells.get("H9").getStringValue());
            gsField.setText(cells.get("H11").getStringValue());
            draftField.setText(cells.get("K10").getStringValue());
            aftField.setText(cells.get("K11").getStringValue());

        } catch (Exception e) {
        }

        return view;
    }

}
