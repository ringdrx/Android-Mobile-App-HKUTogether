package com.example.hkutogether.ui.dashboard;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hkutogether.MainPage;
import com.example.hkutogether.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    String un = MainPage.un;
    String password, email, name, yearofstudy, otherinfo;
    EditText editusername, editname, editemail, edityearofstudy, editotherinfo;
    Button edit, save;

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_dashboard, container, false);


        editusername= root.findViewById(R.id.editusername);
        editname= root.findViewById(R.id.editname);
        editemail= root.findViewById(R.id.editemail);
        edityearofstudy= root.findViewById(R.id.edityearofstudy);
        editotherinfo= root.findViewById(R.id.editotherinfo);
        edit= root.findViewById(R.id.edit);
        save= root.findViewById(R.id.save);


        editusername.setText(un);

        save.setEnabled(false);



        final String url = "https://i.cs.hku.hk/~rxduan/comp3330/project.php";
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            boolean success;
            String jsonString;

            @Override
            protected String doInBackground(String... arg0) {
                success = true;
                jsonString = getJsonPage(url);
                if (jsonString.equals("Fail to login"))
                    success = false;
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (success) {
                    ArrayList<String> usernames = new ArrayList<String>();
                    try {
                        JSONObject rootJSONObj = new JSONObject(jsonString);
                        JSONArray jsonArray1 = rootJSONObj.optJSONArray("username");
                        for (int i=0; i<jsonArray1.length(); i++) {
                            usernames.add(jsonArray1.getString(i));
                        }
                        for (int i=0; i<jsonArray1.length(); i++) {
                            if (usernames.get(i).equals(un)){
                                password=rootJSONObj.optJSONArray("password").getString(i);
                                email=rootJSONObj.optJSONArray("email").getString(i);
                                name=rootJSONObj.optJSONArray("name").getString(i);
                                yearofstudy=rootJSONObj.optJSONArray("yearofstudy").getString(i);
                                otherinfo=rootJSONObj.optJSONArray("otherinfo").getString(i);

                                editusername.setText(un);
                                editname.setText(name);
                                editemail.setText(email);
                                edityearofstudy.setText(yearofstudy);
                                editotherinfo.setText(otherinfo);

                                editusername.setEnabled(false);
                                editname.setEnabled(false);
                                editemail.setEnabled(false);
                                edityearofstudy.setEnabled(false);
                                editotherinfo.setEnabled(false);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute("");

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editname.setEnabled(true);
                editemail.setEnabled(true);
                edityearofstudy.setEnabled(true);
                editotherinfo.setEnabled(true);
                save.setEnabled(true);
                edit.setEnabled(false);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Save Changes")
                        .setMessage("Are you sure you want to save the changes?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<String> validyearofstudy= new ArrayList<String>();
                                for (int i=1; i<10; i++) {
                                    validyearofstudy.add(""+i);
                                }
                                validyearofstudy.add("*");
                                if (editname.getText().toString().isEmpty()
                                        ||editemail.getText().toString().isEmpty()
                                        ||edityearofstudy.getText().toString().isEmpty()
                                        ||editotherinfo.getText().toString().isEmpty()){
                                    Toast.makeText(getContext(),"Please avoid empty input.",Toast.LENGTH_SHORT).show();
                                }
                                else if (!validyearofstudy.contains(edityearofstudy.getText().toString())){
                                    Toast.makeText(getContext(),"Invalid year of study.",Toast.LENGTH_SHORT).show();
                                }
                                else {

                                    final ProgressDialog pdialog = new ProgressDialog(getContext());
                                    pdialog.setCancelable(false);
                                    pdialog.setMessage("Connecting ...");
                                    pdialog.show();

                                    final String url = "https://i.cs.hku.hk/~rxduan/comp3330/project.php?action=update&username="
                                            +android.net.Uri.encode(un, "UTF-8")
                                            +"&name="
                                            +android.net.Uri.encode(editname.getText().toString(), "UTF-8")
                                            +"&email="
                                            +android.net.Uri.encode(editemail.getText().toString(), "UTF-8")
                                            +"&yearofstudy="
                                            +android.net.Uri.encode(edityearofstudy.getText().toString(), "UTF-8")
                                            +"&otherinfo="
                                            +android.net.Uri.encode(editotherinfo.getText().toString(), "UTF-8");


                                    AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                                        boolean success;
                                        String jsonString;
                                        @Override
                                        protected String doInBackground(String... arg0) {
                                            success = true;
                                            pdialog.setMessage("Checking status...");
                                            pdialog.show();
                                            jsonString = getJsonPage(url);
                                            if (jsonString.equals("Fail to login"))
                                                success = false;
                                            return null;
                                        }
                                        @Override
                                        protected void onPostExecute(String result) {
                                            if (success) {
                                                try {
                                                    JSONObject rootJSONObj = new JSONObject(jsonString);
                                                    JSONArray jsonArray = rootJSONObj.optJSONArray("username");
                                                    Toast.makeText(getContext(),"Data modification successful.",Toast.LENGTH_SHORT).show();
                                                    editname.setEnabled(false);
                                                    editemail.setEnabled(false);
                                                    edityearofstudy.setEnabled(false);
                                                    editotherinfo.setEnabled(false);
                                                    save.setEnabled(false);
                                                    edit.setEnabled(true);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            pdialog.hide();
                                        }
                                    }.execute("");
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        });

        return root;
    }
    public String getJsonPage(String url) {
        HttpURLConnection conn_object = null;
        final int HTML_BUFFER_SIZE = 2*1024*1024;
        char htmlBuffer[] = new char[HTML_BUFFER_SIZE];

        try {
            URL url_object = new URL(url);
            conn_object = (HttpURLConnection) url_object.openConnection();
            conn_object.setInstanceFollowRedirects(true);
            BufferedReader reader_list = new BufferedReader(new InputStreamReader(conn_object.getInputStream()));
            String HTMLSource = ReadBufferedHTML(reader_list, htmlBuffer, HTML_BUFFER_SIZE);
            reader_list.close();
            return HTMLSource;
        } catch (Exception e) {
            System.out.println("Exception caught!");
            return "Fail to login";
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            if (conn_object != null) {
                conn_object.disconnect();
            }
        }
    }
    public String ReadBufferedHTML(BufferedReader reader, char [] htmlBuffer, int bufSz) throws java.io.IOException {
        htmlBuffer[0] = '\0';
        int offset = 0;
        do {
            int cnt = reader.read(htmlBuffer, offset, bufSz - offset);
            if (cnt > 0) {
                offset += cnt;
            } else {
                break;
            }
        } while (true);
        return new String(htmlBuffer);
    }
}