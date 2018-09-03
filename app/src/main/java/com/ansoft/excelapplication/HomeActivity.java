package com.ansoft.excelapplication;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ansoft.excelapplication.Data.Ullagedata;
import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeActivity extends Activity {

    Workbook workbook = null;
    public static String FILE_NAME = "original.xlsm";
    RelativeLayout progress;
    TextView progressMsg;
    RelativeLayout content;
    Button createNewBtn;
    ListView prevList;
    ArrayList<String> previousWorkbooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        previousWorkbooks = new ArrayList<>();
        initView();
        getData();
    }

    public boolean isEmpty(EditText editText) {
        return editText.getText().toString().isEmpty();
    }


    public String getTime(TimePicker timePicker) {
        int hour, minute;
        boolean ispm;
        hour = timePicker.getCurrentHour();

        minute = timePicker.getCurrentMinute();

        if (hour > 12) {
            ispm = true;
            hour -= 12;
        } else {
            ispm = false;
        }

        String hourstr = hour + "";
        String minstr = minute + "";
        if (hourstr.length() == 1) {
            hourstr = "0" + hourstr;
        }
        if (minstr.length() == 1) {
            minstr = "0" + minstr;
        }

        if (ispm) {
            return hourstr + ":" + minstr + ":00 PM";
        } else {
            return hourstr + ":" + minstr + ":00 AM";
        }

    }

    public void showTimeDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.MyDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_time_report, null);
        dialogBuilder.setView(view);

        final TimePicker completeLD, commenceLD, nortendered, fuelingstop, fuelingstart, shorestop, shorestart;
        completeLD = (TimePicker) view.findViewById(R.id.completeLD);
        commenceLD = (TimePicker) view.findViewById(R.id.commenceLD);
        nortendered = (TimePicker) view.findViewById(R.id.nortendered);
        fuelingstop = (TimePicker) view.findViewById(R.id.fuelingstop);
        fuelingstart = (TimePicker) view.findViewById(R.id.fuelingstart);
        shorestop = (TimePicker) view.findViewById(R.id.shorestop);
        shorestart = (TimePicker) view.findViewById(R.id.shorestart);

        dialogBuilder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Cells cells = workbook.getWorksheets().get(1).getCells();

                cells.get("B4").setValue(new Time(commenceLD.getCurrentHour(), commenceLD.getCurrentMinute(), 0));
                cells.get("B5").setValue(new Time(completeLD.getCurrentHour(), completeLD.getCurrentMinute(), 0));
                cells.get("D11").setValue(new Time(nortendered.getCurrentHour(), nortendered.getCurrentMinute(), 0));
                cells.get("H16").setValue(new Time(fuelingstop.getCurrentHour(), fuelingstop.getCurrentMinute(), 0));
                cells.get("H17").setValue(new Time(fuelingstart.getCurrentHour(), fuelingstart.getCurrentMinute(), 0));
                cells.get("H18").setValue(new Time(shorestop.getCurrentHour(), shorestop.getCurrentMinute(), 0));
                cells.get("H19").setValue(new Time(shorestart.getCurrentHour(), shorestart.getCurrentMinute(), 0));


                /*
                cells.get("B4").setValue(getTime(commenceLD));
                cells.get("B5").setValue(getTime(completeLD));
                cells.get("D11").setValue(getTime(nortendered));
                cells.get("H16").setValue(getTime(fuelingstop));
                cells.get("H17").setValue(getTime(fuelingstart));
                cells.get("H18").setValue(getTime(shorestop));
                cells.get("H19").setValue(getTime(shorestart));
                */
                showUllageDialog();

            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    public void showVURDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.MyDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_vur, null);
        dialogBuilder.setView(view);

        final EditText portField, terminalField, voyageField, productField, gsField, draftField, aftField, draftsField, aftsField;
        Calendar calendar = Calendar.getInstance();

        portField = (EditText) view.findViewById(R.id.portField);
        terminalField = (EditText) view.findViewById(R.id.terminalField);
        voyageField = (EditText) view.findViewById(R.id.voyageField);
        productField = (EditText) view.findViewById(R.id.productField);
        gsField = (EditText) view.findViewById(R.id.gsField);
        draftField = (EditText) view.findViewById(R.id.draftField);
        aftField = (EditText) view.findViewById(R.id.aftField);
        draftsField = (EditText) view.findViewById(R.id.draftsField);
        aftsField = (EditText) view.findViewById(R.id.aftsField);
        dialogBuilder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isEmpty(portField)
                        || isEmpty(terminalField)
                        || isEmpty(voyageField)
                        || isEmpty(productField)
                        || isEmpty(gsField)
                        || isEmpty(draftField)
                        || isEmpty(aftField)) {
                    Toast.makeText(HomeActivity.this, "Please enter value in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    Cells cells = workbook.getWorksheets().get(0).getCells();
                    cells.get("C9").setValue(portField.getText().toString());
                    cells.get("C10").setValue(terminalField.getText().toString());
                    cells.get("C11").setValue(voyageField.getText().toString());
                    cells.get("H9").setValue(productField.getText().toString());
                    cells.get("H11").setValue(gsField.getText().toString());
                    String draft = "";
                    String aft = "";
                    if (draftsField.getText().toString().isEmpty()) {
                        draft = draftField.getText().toString() + "' " + "00\"";
                    } else {
                        if (draftsField.getText().toString().length() == 1) {
                            draft = draftField.getText().toString() + "' 0" + draftsField.getText().toString() + "\"";
                        } else {
                            draft = draftField.getText().toString() + "' " + draftsField.getText().toString() + "\"";
                        }
                    }


                    if (aftsField.getText().toString().isEmpty()) {
                        aft = aftField.getText().toString() + "' " + "00\"";
                    } else {
                        if (aftsField.getText().toString().length() == 1) {
                            aft = aftField.getText().toString() + "' 0" + aftsField.getText().toString() + "\"";
                        } else {
                            aft = aftField.getText().toString() + "' " + aftsField.getText().toString() + "\"";
                        }
                    }
                    Log.e("DRAFT", draft);
                    Log.e("AFT", aft);
                    cells.get("K10").setValue(draft);
                    cells.get("K11").setValue(aft);


                    Cells cells2 = workbook.getWorksheets().get(2).getCells();
                    cells2.get("K6").setValue(draft);
                    cells2.get("K7").setValue(aft);
                    showTimeDialog();
                }

            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void showUllageDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.MyDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_tank_data, null);
        dialogBuilder.setView(view);

        final EditText feet1p, inch1p, fraction1p, temp1p, fraction1p1;
        final EditText feet1s, inch1s, fraction1s, temp1s, fraction1s1;
        final EditText feet2p, inch2p, fraction2p, temp2p, fraction2p1;
        final EditText feet2s, inch2s, fraction2s, temp2s, fraction2s1;

        // 1P
        feet1p = (EditText) view.findViewById(R.id.feet1p);
        inch1p = (EditText) view.findViewById(R.id.inch1p);
        fraction1p = (EditText) view.findViewById(R.id.fraction1p);
        fraction1p1 = (EditText) view.findViewById(R.id.fraction1p1);
        temp1p = (EditText) view.findViewById(R.id.temp1p);


        // 1S
        feet1s = (EditText) view.findViewById(R.id.feet1s);
        inch1s = (EditText) view.findViewById(R.id.inch1s);
        fraction1s = (EditText) view.findViewById(R.id.fraction1s);
        fraction1s1 = (EditText) view.findViewById(R.id.fraction1s1);
        temp1s = (EditText) view.findViewById(R.id.temp1s);

        // 2P
        feet2p = (EditText) view.findViewById(R.id.feet2p);
        inch2p = (EditText) view.findViewById(R.id.inch2p);
        fraction2p = (EditText) view.findViewById(R.id.fraction2p);
        fraction2p1 = (EditText) view.findViewById(R.id.fraction2p1);
        temp2p = (EditText) view.findViewById(R.id.temp2p);


        // 2S
        feet2s = (EditText) view.findViewById(R.id.feet2s);
        inch2s = (EditText) view.findViewById(R.id.inch2s);
        fraction2s = (EditText) view.findViewById(R.id.fraction2s);
        fraction2s1 = (EditText) view.findViewById(R.id.fraction2s1);
        temp2s = (EditText) view.findViewById(R.id.temp2s);


        dialogBuilder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isEmpty(feet1p)
                        || isEmpty(feet1s)
                        || isEmpty(feet2p)
                        || isEmpty(feet2s)
                        || isEmpty(temp1p)
                        || isEmpty(temp1s)
                        || isEmpty(temp2p)
                        || isEmpty(temp2s)) {
                    Toast.makeText(HomeActivity.this, "Please enter value in all fields", Toast.LENGTH_SHORT).show();
                } else {


                    Ullagedata t1p, t1s, t2p, t2s;
                    t1p = new Ullagedata(feet1p, inch1p, fraction1p, fraction1p1, temp1p);
                    t1s = new Ullagedata(feet1s, inch1s, fraction1s, fraction1s1, temp1s);
                    t2p = new Ullagedata(feet2p, inch2p, fraction2p, fraction2p1, temp2p);
                    t2s = new Ullagedata(feet2s, inch2s, fraction2s, fraction2s1, temp2s);

                    Cells cells = workbook.getWorksheets().get(0).getCells();

                    cells.get("C15").setValue(t1p.getUllage());
                    cells.get("C16").setValue(t1s.getUllage());
                    cells.get("C17").setValue(t2p.getUllage());
                    cells.get("C18").setValue(t2s.getUllage());

                    cells.get("G15").setValue(t1p.getTemperature());
                    cells.get("G16").setValue(t1s.getTemperature());
                    cells.get("G17").setValue(t2p.getTemperature());
                    cells.get("G18").setValue(t2s.getTemperature());

                    PublicData data = new PublicData();
                    data.setWorkbook(workbook);
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        dialogBuilder.setNegativeButton("Previous", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isEmpty(feet1p)
                        || isEmpty(feet1s)
                        || isEmpty(feet2p)
                        || isEmpty(feet2s)
                        || isEmpty(temp1p)
                        || isEmpty(temp1s)
                        || isEmpty(temp2p)
                        || isEmpty(temp2s)) {
                    //Toast.makeText(HomeActivity.this, "Please enter value in all fields", Toast.LENGTH_SHORT).show();
                } else {


                    Ullagedata t1p, t1s, t2p, t2s;
                    t1p = new Ullagedata(feet1p, inch1p, fraction1p, fraction1p1, temp1p);
                    t1s = new Ullagedata(feet1s, inch1s, fraction1s, fraction1s1, temp1s);
                    t2s = new Ullagedata(feet2p, inch2p, fraction2p, fraction2p1, temp2p);
                    t2p = new Ullagedata(feet2s, inch2s, fraction2s, fraction2s1, temp2s);

                    Cells cells = workbook.getWorksheets().get(0).getCells();

                    cells.get("C15").setValue(t1p.getUllage());
                    cells.get("C16").setValue(t1s.getUllage());
                    cells.get("C17").setValue(t2p.getUllage());
                    cells.get("C18").setValue(t2s.getUllage());

                    cells.get("G15").setValue(t1p.getTemperature());
                    cells.get("G16").setValue(t1s.getTemperature());
                    cells.get("G17").setValue(t2p.getTemperature());
                    cells.get("G18").setValue(t2s.getTemperature());

                }
                showVURDialog();

            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void populateListView() {
        File directory = getExternalFilesDir(null);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith("xlsm") && (!files[i].getName().equalsIgnoreCase(FILE_NAME))) {
                previousWorkbooks.add(files[i].getName());
            }
            Log.d("Files", "FileName:" + files[i].getName());
        }
        if (previousWorkbooks.size() != 0) {
            prevList.setVisibility(View.VISIBLE);
            ReportsAdapter adapter = new ReportsAdapter(previousWorkbooks, HomeActivity.this);
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, previousWorkbooks);
            prevList.setAdapter(adapter);
            prevList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("FileName", previousWorkbooks.get(position));
                    startActivity(intent);
                    finish();
                }
            });

        }
    }

    private void getData() {
        if (fileExists()) {
            showProgress("Getting data");


            runAsyncTask(new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    populateWorkbook();
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {

                    super.onPostExecute(result);
                    hideProgress();
                    populateListView();
                }

            });
        } else {

            showProgress("Downloading File ...");
            runAsyncTask(new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    copyAssets(new MainActivity.OnFinishListener() {
                        @Override
                        public void onFinished() {
                            showProgress("Getting data");
                            runAsyncTask(new AsyncTask<Void, Void, Void>() {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    populateWorkbook();
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void result) {
                                    super.onPostExecute(result);
                                    hideProgress();
                                    populateListView();

                                }

                            });
                        }

                        @Override
                        public void onFailed() {
                            showProgress("Downloading Failed. Exit app and try again");
                        }
                    });

                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);

                }

            });
        }
    }


    private void populateWorkbook() {
        try {
            File file = new File(getExternalFilesDir(null), FILE_NAME);
            workbook = new Workbook(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public boolean fileExists() {

        File outFile = new File(getExternalFilesDir(null), FILE_NAME);
        return outFile.exists();
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        MainActivity.OnFinishListener onFinishListener;

        public DownloadTask(Context context, MainActivity.OnFinishListener listener) {
            this.context = context;
            this.onFinishListener=listener;
        }



        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                File localFile = new File(getExternalFilesDir(null), FILE_NAME);
                output = new FileOutputStream(localFile);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            onFinishListener.onFinished();
        }
    }

    private void copyAssets(final MainActivity.OnFinishListener listener) {
        final DownloadTask downloadTask = new DownloadTask(HomeActivity.this, listener);
        downloadTask.execute("http://myvef.com/chemical.xlsm");

        /*
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(getExternalFilesDir(null), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
        */
    }

    private void initView() {
        progress = (RelativeLayout) findViewById(R.id.progress);
        progressMsg = (TextView) findViewById(R.id.progressMsg);
        content = (RelativeLayout) findViewById(R.id.content);
        createNewBtn = (Button) findViewById(R.id.newReportBtn);
        createNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVURDialog();
            }
        });
        prevList = (ListView) findViewById(R.id.listView);
        showProgress("Processing");
    }


    public void showProgress(String message) {
        progress.setVisibility(View.VISIBLE);
        progressMsg.setText(message);
        content.setVisibility(View.GONE);
    }

    public void hideProgress() {
        progress.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }

    private static final <T> void runAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        asyncTask.execute(params);
    }
}
