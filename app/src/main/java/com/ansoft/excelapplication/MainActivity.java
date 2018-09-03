package com.ansoft.excelapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ansoft.excelapplication.Data.Vefdata;
import com.aspose.cells.Cells;
import com.aspose.cells.DateTime;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.ansoft.excelapplication.PdfFragment.Slide.SUMMARY;
import static com.ansoft.excelapplication.PdfFragment.Slide.VUR;

public class MainActivity extends AppCompatActivity {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/vefdata";

    // Database credentials
    static final String USER = "root";
    static final String PASS = "";

    Workbook workbook = null;
    public static String FILE_NAME;

    RelativeLayout progress;
    TextView progressMsg;
    LinearLayout content;

    Button saveDataBtn, addVefBtn, savePdfBtn, emailReportBtn;
    TabLayout tabLayout;
    ViewPager viewPager;
    static String VUR_REPORT = "VUR_REPORT.pdf";
    static String SUMMARY_REPORT = "SUMMARY_REPORT.pdf";
    static String ROB_REPORT = "ROB_REPORT.pdf";
    static String VEF_REPORT = "VEF_REPORT.pdf";

    int count;
    int lastData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().hasExtra("FileName")) {
            FILE_NAME = getIntent().getStringExtra("FileName");
        }
        initView();
        setListener();
        getData();

    }

    class SectionPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragments;
        ArrayList<String> titles;

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            titles = new ArrayList<>();
        }

        public void add(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void getData() {
        showProgress("Getting data");
        runAsyncTask(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (FILE_NAME == null) {
                    PublicData data = new PublicData();
                    workbook = data.getWorkbook();
                    formatFormulae();
                    workbook.calculateFormula();
                } else {
                    populateFields();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                runAsyncTask(new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        savePDF(VUR);
                        savePDF(SUMMARY);
                        savePDF(PdfFragment.Slide.ROB);
                        savePDF(PdfFragment.Slide.VEF);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);

                        hideProgress();
                        showFields();
                    }

                });

            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean isEmpty(EditText editText) {
        return editText.getText().toString().isEmpty();
    }


    public static String postVEF(Vefdata vefdata) {
        Connection conn = null;
        Statement stmt = null;
        try {
            // STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // STEP 3: Open a connection
            Log.e("JDBC","Connecting to database...");
            conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 4: Execute a query
            Log.e("JDBC","Creating statement...");
            stmt = (Statement) conn.createStatement();
            Log.e("JDBC","Inserting records into the table...");

            String sql = "INSERT INTO vef " + "VALUES ('" + vefdata.getVoyageNumber() + "', '" + vefdata.getCargo()
                    + "', '" + vefdata.getLoadPort() + "', '" + vefdata.getShoreVol() + "', '" + vefdata.getBargeVol()
                    + "', '" + vefdata.getVefFactor() + "', '" + vefdata.getPass() + "')";
            int code=stmt.executeUpdate(sql);
            // STEP 6: Clean-up environment
            stmt.close();
            conn.close();
            return code+"";
        } catch (SQLException se) {
            // Handle errors for JDBC
            Log.e("SQLException", se.getMessage());
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
        return null;

    }


    private void setListener() {
        addVefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_add_vef, null);
                dialogBuilder.setView(dialogView);
                final EditText voyageNumber = (EditText) dialogView.findViewById(R.id.voyageNumberField);
                final EditText loadPortField = (EditText) dialogView.findViewById(R.id.loadPortField);
                final EditText cargoField = (EditText) dialogView.findViewById(R.id.cargoField);
                final EditText bargeVolumeField = (EditText) dialogView.findViewById(R.id.bargeVolumeField);
                final EditText shoreVolumeField = (EditText) dialogView.findViewById(R.id.shoreVolumeField);

                dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isEmpty(voyageNumber)
                                || isEmpty(loadPortField)
                                || isEmpty(cargoField)
                                || isEmpty(bargeVolumeField)
                                || isEmpty(shoreVolumeField)) {
                            Toast.makeText(MainActivity.this, "Please enter value in all fields", Toast.LENGTH_SHORT).show();
                        } else {

                            Cells cells = workbook.getWorksheets().get(3).getCells();

                            final Vefdata firebaseData = new Vefdata();
                            firebaseData.setVoyageNumber(cells.get("C31").getStringValue());
                            firebaseData.setLoadPort(cells.get("E31").getStringValue());
                            firebaseData.setCargo(cells.get("F31").getStringValue());
                            firebaseData.setBargeVol(cells.get("G31").getStringValue());
                            firebaseData.setShoreVol(cells.get("H31").getStringValue());
                            firebaseData.setVefFactor(cells.get("I31").getStringValue());
                            firebaseData.setPass(cells.get("J31").getStringValue());


                            Map<String, String> stars = new HashMap<>();
                            stars.put("Voyage Number", firebaseData.getVoyageNumber());
                            stars.put("Load Port", firebaseData.getLoadPort());
                            stars.put("Cargo", firebaseData.getCargo());
                            stars.put("Barge Volume", firebaseData.getBargeVol());
                            stars.put("Shore Volume", firebaseData.getShoreVol());
                            stars.put("VEF Factor", firebaseData.getVefFactor());
                            stars.put("PASS", firebaseData.getPass());



                            runAsyncTask(new AsyncTask<Void, Void, Void>() {

                                @Override
                                protected Void doInBackground(Void... params) {

                                    postVEF(firebaseData);
                                    //convertPDFtoImage(VUR);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void result) {
                                    super.onPostExecute(result);
                                    showFields();
                                }

                            });


                            ArrayList<Vefdata> list = new ArrayList<>();
                            for (int i = 0; i < 20; i++) {
                                int rowNumber = 12 + i;
                                Vefdata vefdata = new Vefdata();
                                vefdata.setVoyageNumber(cells.get("C" + rowNumber).getStringValue());
                                vefdata.setLoadPort(cells.get("E" + rowNumber).getStringValue());
                                vefdata.setCargo(cells.get("F" + rowNumber).getStringValue());
                                vefdata.setBargeVol(cells.get("G" + rowNumber).getStringValue());
                                vefdata.setShoreVol(cells.get("H" + rowNumber).getStringValue());
                                vefdata.setVefFactor(cells.get("I" + rowNumber).getStringValue());
                                vefdata.setPass(cells.get("J" + rowNumber).getStringValue());
                                list.add(vefdata);
                            }
                            /*

                            CellArea cellArea = new CellArea();
                            cellArea.StartRow = 12;
                            cellArea.StartColumn = 2;
                            cellArea.EndRow = 30;
                            cellArea.EndColumn = 6;
                            cells.moveRange(cellArea, 13, 2);*/

                            cells.get("C12").setValue(voyageNumber.getText().toString());
                            cells.get("E12").setValue(loadPortField.getText().toString());
                            cells.get("F12").setValue(cargoField.getText().toString());
                            cells.get("G12").setValue(bargeVolumeField.getText().toString());
                            cells.get("H12").setValue(shoreVolumeField.getText().toString());


                            for (int i = 1; i < 20; i++) {
                                int rowNumber = 12 + i;
                                Vefdata vefdata = list.get(i - 1);
                                cells.get("C" + rowNumber).setValue(vefdata.getVoyageNumber());
                                cells.get("E" + rowNumber).setValue(vefdata.getLoadPort());
                                cells.get("F" + rowNumber).setValue(vefdata.getCargo());
                                cells.get("G" + rowNumber).setValue(vefdata.getBargeVol());
                                cells.get("H" + rowNumber).setValue(vefdata.getShoreVol());
                            }

                            if (FILE_NAME == null) {
                                FILE_NAME = "Unsaved Report.xlsm";
                            }
                            saveWorkbook();
                            runAsyncTask(new AsyncTask<Void, Void, Void>() {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    savePDF(VUR);
                                    savePDF(SUMMARY);
                                    savePDF(PdfFragment.Slide.ROB);
                                    savePDF(PdfFragment.Slide.VEF);

                                    //convertPDFtoImage(VUR);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void result) {
                                    super.onPostExecute(result);
                                    showFields();
                                }

                            });

                        }

                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });

        savePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                show(new ReportSelectionListener() {
                    @Override
                    public void onSelected(final ArrayList<Integer> arrayList) {
                        DialogProperties properties = new DialogProperties();
                        properties.selection_mode = DialogConfigs.SINGLE_MODE;
                        properties.selection_type = DialogConfigs.DIR_SELECT;
                        properties.root = new File(DialogConfigs.DEFAULT_DIR);
                        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                        FilePickerDialog dialog = new FilePickerDialog(MainActivity.this, properties);
                        dialog.setTitle("Select the Directory");
                        dialog.show();
                        dialog.setDialogSelectionListener(new DialogSelectionListener() {
                            @Override
                            public void onSelectedFilePaths(String[] files) {

                                final String filePath = files[0];

                                showProgress("Saving as PDF");
                                runAsyncTask(new AsyncTask<Void, Void, Void>() {

                                    @Override
                                    protected Void doInBackground(Void... params) {

                                        Workbook wb2 = workbook;
                                        Cells cells = wb2.getWorksheets().get(0).getCells();
                                        cells.deleteColumn(4);
                                        for (int x : arrayList) {
                                            try {
                                                PdfSaveOptions options = new PdfSaveOptions(SaveFormat.PDF);
                                                options.setCreatedTime(DateTime.getNow());
                                                for (int i = 0; i < wb2.getWorksheets().getCount(); i++) {
                                                    if (i != x) {
                                                        wb2.getWorksheets().get(i).setVisible(false);
                                                    }
                                                }
                                                wb2.save(filePath + File.separator + "ChemicalProgram" + Calendar.getInstance().getTimeInMillis() + ".pdf", SaveFormat.PDF);
                                                for (int i = 0; i < wb2.getWorksheets().getCount(); i++) {
                                                    wb2.getWorksheets().get(i).setVisible(true);
                                                }

                                            } catch (Exception e) {
                                                Log.e("Chemical Program", "Set Creation Time For Output PDF", e);
                                            }
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void result) {

                                        super.onPostExecute(result);
                                        hideProgress();
                                    }

                                });
                            }


                        });
                    }
                });

            }
        });


        emailReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                show(new ReportSelectionListener() {
                    @Override
                    public void onSelected(final ArrayList<Integer> arrayList) {
                        final String filePath = getExternalFilesDir(null).getAbsolutePath();

                        final String ss = Calendar.getInstance().getTimeInMillis() + "";
                        showProgress("Saving as PDF");
                        runAsyncTask(new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {

                                Workbook wb2 = workbook;
                                Cells cells = wb2.getWorksheets().get(0).getCells();
                                cells.deleteColumn(4);
                                for (int x : arrayList) {
                                    try {
                                        PdfSaveOptions options = new PdfSaveOptions(SaveFormat.PDF);
                                        options.setCreatedTime(DateTime.getNow());
                                        for (int i = 0; i < wb2.getWorksheets().getCount(); i++) {
                                            if (i != x) {
                                                wb2.getWorksheets().get(i).setVisible(false);
                                            }
                                        }
                                        String reportName = "";
                                        switch (x) {
                                            case 0:
                                                reportName = "VUR";
                                                break;

                                            case 1:
                                                reportName = "Summary";
                                                break;

                                            case 2:
                                                reportName = "ROB_OBQ";
                                                break;

                                            case 3:
                                                reportName = "VEF";
                                                break;
                                        }
                                        wb2.save(filePath + File.separator + reportName + ss + ".pdf", SaveFormat.PDF);
                                        for (int i = 0; i < wb2.getWorksheets().getCount(); i++) {
                                            wb2.getWorksheets().get(i).setVisible(true);
                                        }
                                    } catch (Exception e) {
                                        Log.e("Chemical Program", "Set Creation Time For Output PDF", e);
                                    }
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                super.onPostExecute(result);
                                hideProgress();
                                Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                ArrayList<Uri> attachment = new ArrayList<Uri>();
                                for (int x : arrayList) {
                                    String reportName = "";
                                    switch (x) {
                                        case 0:
                                            reportName = "VUR";
                                            break;

                                        case 1:
                                            reportName = "Summary";
                                            break;

                                        case 2:
                                            reportName = "ROB_OBQ";
                                            break;

                                        case 3:
                                            reportName = "VEF";
                                            break;
                                    }
                                    Uri path = Uri.fromFile(new File(filePath + File.separator + reportName + ss + ".pdf"));
                                    attachment.add(path);
                                }
                                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachment);
                                emailIntent.setType("vnd.android.cursor.dir/email");
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Ullage Report");
                                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                            }

                        });
                    }
                });

            }
        });

        saveDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.save_alert, null);
                dialogBuilder.setView(view);
                final EditText fileName = (EditText) view.findViewById(R.id.fileName);

                if (FILE_NAME != null) {
                    fileName.setText(FILE_NAME.replace(".xlsm", ""));
                }
                dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (fileName.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter report name", Toast.LENGTH_SHORT).show();
                        } else {
                            FILE_NAME = fileName.getText().toString() + ".xlsm";
                            // formatFormulae();
                            saveWorkbook();
                            runAsyncTask(new AsyncTask<Void, Void, Void>() {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    savePDF(VUR);
                                    savePDF(SUMMARY);
                                    savePDF(PdfFragment.Slide.ROB);
                                    savePDF(PdfFragment.Slide.VEF);

                                    //convertPDFtoImage(VUR);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void result) {
                                    super.onPostExecute(result);
                                    showFields();
                                }

                            });
                        }

                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });
    }


    private void populateFields() {
        try {
            File file = new File(getExternalFilesDir(null), FILE_NAME);
            workbook = new Workbook(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public double cInches(String ss) {
        String[] sss = ss.split(" ");
        int feet = Integer.parseInt(sss[0].replace("'", ""));
        int inch;
        if (sss.length == 1) {
            return 0.0;
        }
        if (sss.length == 2) {
            int ll = 2;
            if (sss[1].charAt(1) == '\"') {
                ll = 1;
            }
            inch = Integer.parseInt(sss[1].substring(0, ll));
        } else {
            inch = Integer.parseInt(sss[1]);
        }
        double fraction = 0;
        if (sss.length == 3) {
            if (sss[2].length() > 2) {
                String[] denom = sss[2].substring(0, 3).split("/");
                fraction = Integer.parseInt(denom[0]) / Integer.parseInt(denom[1]);
            }
        }
        return feet * 12 + inch + fraction;
    }

    public String cFeet(double inches) {

        int feet = (int) inches / 12;
        int inch = (int) inches % 12;


        String stringValue = inches + "";
        String finalValue = stringValue.split("\\.")[1];

        String fraction = "";
        if (Integer.parseInt(finalValue) > 0 && Integer.parseInt(finalValue) < 125) {
            fraction = "1/8";
        } else if (Integer.parseInt(finalValue) > 125 && Integer.parseInt(finalValue) <= 250) {
            fraction = "2/4";
        } else if (Integer.parseInt(finalValue) > 250 && Integer.parseInt(finalValue) <= 375) {
            fraction = "3/8";
        } else if (Integer.parseInt(finalValue) > 375 && Integer.parseInt(finalValue) <= 500) {
            fraction = "1/2";
        } else if (Integer.parseInt(finalValue) > 500 && Integer.parseInt(finalValue) <= 625) {
            fraction = "5/8";
        } else if (Integer.parseInt(finalValue) > 625 && Integer.parseInt(finalValue) <= 750) {
            fraction = "3/4";
        } else if (Integer.parseInt(finalValue) > 750 && Integer.parseInt(finalValue) <= 875) {
            fraction = "7/8";
        } else if (Integer.parseInt(finalValue) > 875 && Integer.parseInt(finalValue) <= 1000) {
            fraction = "";
        }
        return feet + "\'" + " -" + inch + " " + fraction + "\"";
    }

    public void putgHardcodeValue() {
        Cells cells = workbook.getWorksheets().get(0).getCells();
        cells.get("D15").setValue("4'-4 1/2\"");
        cells.get("D16").setValue("4'-2 1/2\"");
        cells.get("D17").setValue("4'-6 7/8\"");
        cells.get("D18").setValue("4'-8 7/8\"");
        cells.get("E15").setValue("52.500");
        cells.get("E16").setValue("50.500");
        cells.get("E17").setValue("54.875");
        cells.get("E18").setValue("56.875");
        cells.get("K12").setValue("0.5");
        cells.get("F15").setValue("239343");
        cells.get("F16").setValue("241404");
        cells.get("F17").setValue("238378");
        cells.get("F18").setValue("235068");


        Cells cells2 = workbook.getWorksheets().get(2).getCells();
        cells2.get("K8").putValue("3.50");
        cells2.get("K10").putValue("0.01378");


        Cells cells3 = workbook.getWorksheets().get(1).getCells();
        cells3.get("E27").putValue("");
        cells3.get("E36").putValue("");
        cells3.get("I27").putValue("");
        cells3.get("I36").putValue("");

    }

    public void formatFormulae() {

        Cells cells2 = workbook.getWorksheets().get(1).getCells();
        double cine27 = cInches(cells2.get("D27").getStringValue());
        double cini27 = cInches(cells2.get("H27").getStringValue());
        double cine36 = cInches(cells2.get("D36").getStringValue());
        double cini36 = cInches(cells2.get("H36").getStringValue());


        cells2.get("E27").setValue(cine27);
        cells2.get("I27").setValue(cini27);
        cells2.get("E36").setValue(cine36);
        cells2.get("I27").setValue(cini36);

        Cells cells1 = workbook.getWorksheets().get(2).getCells();

        double cink6 = cInches(cells1.get("K6").getStringValue());
        double cink7 = cInches(cells1.get("K7").getStringValue());

        double k8value = getTrimvalue(cink6, cink7);
        cells1.get("K8").setValue(k8value);


        Cells cells = workbook.getWorksheets().get(0).getCells();

        double cink11 = cInches(cells.get("K11").getStringValue());
        double cink10 = cInches(cells.get("K10").getStringValue());
        Log.e("k11", cink11 + "");
        Log.e("k10", cink10 + "");
        double k12value = getTrimvalue(cink11, cink10);
        cells.get("K12").setValue(k12value);

        for (int i = 0; i < 4; i++) {
            int increment = 15 + i;
            if (cells.get("C" + increment).getStringValue() == "") {
                cells.get("C" + increment).setValue("");
            } else {
                double cInches = cInches(cells.get("C" + increment).getStringValue());
                double k12 = Double.parseDouble(cells.get("K12").getStringValue());
                double robc14 = workbook.getWorksheets().get(2).getCells().get("C" + (increment - 1)).getDoubleValue();
                double robe14 = workbook.getWorksheets().get(2).getCells().get("E" + (increment - 1)).getDoubleValue();
                double value = ((cInches - (k12 / 254 * (robc14 / 2 - robe14) * 12)) * 8) / 8;


                Log.e("VALUE", value + "");
                String stringValue = value + "";
                stringValue = stringValue.charAt(0) + "" + stringValue.charAt(1) + "" + stringValue.charAt(2) + "" + stringValue.charAt(3) + "" + stringValue.charAt(4) + "" + stringValue.charAt(5);
                String[] array = stringValue.split("\\.");
                String initialValue = array[0];
                String finalValue = array[1];
                Log.e("FINAL", finalValue);
                if (finalValue.length() == 2) {
                    finalValue = "0" + finalValue;
                }
                if (finalValue.length() == 3) {
                    if (Integer.parseInt(finalValue) > 0 && Integer.parseInt(finalValue) < 125) {
                        finalValue = 125 + "";
                    } else if (Integer.parseInt(finalValue) > 125 && Integer.parseInt(finalValue) <= 250) {
                        finalValue = 250 + "";
                    } else if (Integer.parseInt(finalValue) > 250 && Integer.parseInt(finalValue) <= 375) {
                        finalValue = 375 + "";
                    } else if (Integer.parseInt(finalValue) > 375 && Integer.parseInt(finalValue) <= 500) {
                        finalValue = 500 + "";
                    } else if (Integer.parseInt(finalValue) > 500 && Integer.parseInt(finalValue) <= 625) {
                        finalValue = 625 + "";
                    } else if (Integer.parseInt(finalValue) > 625 && Integer.parseInt(finalValue) <= 750) {
                        finalValue = 750 + "";
                    } else if (Integer.parseInt(finalValue) > 750 && Integer.parseInt(finalValue) <= 875) {
                        finalValue = 875 + "";
                    } else if (Integer.parseInt(finalValue) > 875 && Integer.parseInt(finalValue) <= 1000) {
                        initialValue = (Integer.parseInt(initialValue) + 1) + "";
                        finalValue = "000";

                    }
                }
                String totalValue = initialValue + "." + finalValue;

                cells.get("E" + increment).setValue(totalValue);
            }
            if (cells.get("C" + increment).getStringValue() == "") {
                cells.get("C" + increment).setValue("");
            }

            if (cells.get("C" + increment).getStringValue() == "") {
                cells.get("D" + increment).setValue("");
            } else {
                double cInches = Double.parseDouble(cells.get("E" + increment).getStringValue());
                cells.get("D" + increment).setValue(cFeet(cInches));
            }
            if (cells.get("C" + increment).getStringValue() == "") {
                cells.get("D" + increment).setValue("");
            }

        }


    }

    private double getTrimvalue(double cink6, double cink7) {
        return (cink6 - cink7) / 12;
    }

    public void toastMsg(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void show(final ReportSelectionListener reportSelectionListener) {
        //formatFormulae();
        //workbook.calculateFormula();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_select, null);
        dialogBuilder.setView(dialogView);
        final CheckBox vurBtn = (CheckBox) dialogView.findViewById(R.id.vurBtn);
        final CheckBox timeLogBtn = (CheckBox) dialogView.findViewById(R.id.timeLogBtn);
        final CheckBox robBtn = (CheckBox) dialogView.findViewById(R.id.robBtn);
        final CheckBox vefBtn = (CheckBox) dialogView.findViewById(R.id.vefBtn);
        dialogBuilder.setPositiveButton("NEXT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<Integer> reports = new ArrayList<Integer>();
                if (vurBtn.isChecked()) {
                    reports.add(0);
                }
                if (timeLogBtn.isChecked()) {
                    reports.add(1);
                }
                if (robBtn.isChecked()) {
                    reports.add(2);
                }
                if (vefBtn.isChecked()) {
                    reports.add(3);
                }
                if (reports.size() == 0) {
                    Toast.makeText(MainActivity.this, "Please select atleast one report", Toast.LENGTH_SHORT).show();
                } else {
                    reportSelectionListener.onSelected(reports);
                }
            }
        });
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public interface ReportSelectionListener {
        void onSelected(ArrayList<Integer> arrayList);
    }

    public void saveWorkbook() {

        showProgress("Saving changes");

        final File outFile = new File(getExternalFilesDir(null), FILE_NAME);
        runAsyncTask(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    workbook.calculateFormula();
                    workbook.save(outFile.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                super.onPostExecute(result);
                hideProgress();
                showFields();
            }

        });
    }

    public void showFields() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.add(new PdfFragment(VUR), "VUR");
        adapter.add(new PdfFragment(SUMMARY), "SUMMARY");
        adapter.add(new PdfFragment(PdfFragment.Slide.ROB), "ROB");
        adapter.add(new PdfFragment(PdfFragment.Slide.VEF), "VEF");
        /*
        adapter.add(new VURFragment(workbook), "VUR");
        adapter.add(new VEFFragment(workbook), "VEF");
        */
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    interface OnFinishListener {
        void onFinished();

        void onFailed();
    }

    private void initView() {
        progress = (RelativeLayout) findViewById(R.id.progress);
        content = (LinearLayout) findViewById(R.id.content);
        progressMsg = (TextView) findViewById(R.id.progressMsg);
        saveDataBtn = (Button) findViewById(R.id.saveDataBtn);
        addVefBtn = (Button) findViewById(R.id.addVefBtn);
        savePdfBtn = (Button) findViewById(R.id.saveReportBtn);
        emailReportBtn = (Button) findViewById(R.id.emailReportBtn);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        showProgress("Processing");
    }


    private static final <T> void runAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        asyncTask.execute(params);
    }

    public void showProgress(String message) {
        progress.setVisibility(View.VISIBLE);
        progressMsg.setText(message);
        content.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
    }

    public void hideProgress() {
        progress.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
    }


    public void convertPDFtoImage(PdfFragment.Slide slide) {
        File currentPDF = null;
        switch (slide) {
            case VUR:
                currentPDF = new File(getExternalFilesDir(null), "vur.png");
                break;
            case SUMMARY:
                currentPDF = new File(getExternalFilesDir(null), "summary.png");
                break;
            case ROB:
                currentPDF = new File(getExternalFilesDir(null), "rob.png");
                break;
            case VEF:
                currentPDF = new File(getExternalFilesDir(null), "vef.png");
                break;
        }
        FileInputStream template = null;
        try {
            template = new FileInputStream(currentPDF);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PDDocument pdf = null;
        try {
            pdf = PDDocument.load(template);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PDFRenderer renderer = new PDFRenderer(pdf);
        try {
            Bitmap b = renderer.renderImage(0);
            saveBitmapToFIle(b, currentPDF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveBitmapToFIle(Bitmap bmp, File file) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void savePDF(PdfFragment.Slide slide) {
        int x = 0;
        File currentPDF = null;
        switch (slide) {
            case VUR:
                x = 0;
                currentPDF = new File(getExternalFilesDir(null), MainActivity.VUR_REPORT);
                break;
            case SUMMARY:
                x = 1;
                currentPDF = new File(getExternalFilesDir(null), MainActivity.SUMMARY_REPORT);
                break;
            case ROB:
                x = 2;
                currentPDF = new File(getExternalFilesDir(null), MainActivity.ROB_REPORT);
                break;
            case VEF:
                x = 3;
                currentPDF = new File(getExternalFilesDir(null), MainActivity.VEF_REPORT);
                break;
        }
        try {
            PdfSaveOptions options = new PdfSaveOptions(SaveFormat.PDF);
            options.setCreatedTime(DateTime.getNow());
            for (int i = 0; i < workbook.getWorksheets().getCount(); i++) {
                if (i != x) {
                    workbook.getWorksheets().get(i).setVisible(false);
                }
            }

            workbook.save(currentPDF.getAbsolutePath(), SaveFormat.PDF);
            for (int i = 0; i < workbook.getWorksheets().getCount(); i++) {
                workbook.getWorksheets().get(i).setVisible(true);
            }

        } catch (Exception e) {
            Log.e("Chemical Program", "Set Creation Time For Output PDF", e);
        }
    }


}
