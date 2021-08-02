package com.example.hkutogether;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Register extends AppCompatActivity{
    private EditText username, password1, password2;
    private Button confirm;
    public ArrayList<String> existedUsernames;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.registerusername);
        password1 = findViewById(R.id.registerpassword1);
        password2 = findViewById(R.id.registerpassword2);
        confirm = findViewById(R.id.confirm);

        updateExistedUsernames();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String un = username.getText().toString();
                String pwd1 = password1.getText().toString();
                String pwd2 = password2.getText().toString();
                if (un.isEmpty()) Toast.makeText(Register.this,"Please enter your username",Toast.LENGTH_SHORT).show();
                else if (!pwd1.equals(pwd2)) Toast.makeText(Register.this,"Inconsistent password.\nPlease re-enter your password.",Toast.LENGTH_SHORT).show();
                else if (pwd1.isEmpty()) Toast.makeText(Register.this,"Password cannot be empty.\nPlease re-enter you password.",Toast.LENGTH_SHORT).show();
                else {
                    if (existedUsernames.contains(un)) Toast.makeText(Register.this,"Username existed.\nPlease pick another one.",Toast.LENGTH_SHORT).show();
                    else register(un, pwd1);
                }
            }
        });
    }

    public void updateExistedUsernames(){
        final ProgressDialog pdialog = new ProgressDialog(this);
        pdialog.setCancelable(false);
        pdialog.setMessage("Connecting ...");
        pdialog.show();
        final String url = "https://i.cs.hku.hk/~rxduan/comp3330/project.php";
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            boolean success;
            String jsonString;

            @Override
            protected String doInBackground(String... arg0) {
                // TODO Auto-generated method stub
                success = true;
                pdialog.setMessage("Checking database...");
                pdialog.show();
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
                        JSONArray jsonArray = rootJSONObj.optJSONArray("username");
                        for (int i=0; i<jsonArray.length(); i++) {
                            String username = jsonArray.getString(i);
                            usernames.add(username);
                        }
                        existedUsernames=usernames;
                        Toast.makeText(Register.this,"Connection successful.",Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    alert( "Error", "Fail to connect" );
                }
                pdialog.hide();
            }
        }.execute("");
    }
    public void register(final String un,final String pwd){
        final ProgressDialog pdialog = new ProgressDialog(this);
        pdialog.setCancelable(false);
        pdialog.setMessage("Connecting ...");
        pdialog.show();
        final String url = "https://i.cs.hku.hk/~rxduan/comp3330/project.php?action=insert&username="
                +android.net.Uri.encode(un, "UTF-8")
                +"&password="
                +android.net.Uri.encode(pwd, "UTF-8");
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            boolean success;
            String jsonString;

            @Override
            protected String doInBackground(String... arg0) {
                // TODO Auto-generated method stub
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
                        Toast.makeText(Register.this,"Registeration successful.",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                        i.putExtra("RegisteredUsername", un);
                        startActivity(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    alert( "Error", "Fail to connect" );
                }
                pdialog.hide();
            }
        }.execute("");
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
    protected void alert(String title, String mymessage){
        new AlertDialog.Builder(this)
                .setMessage(mymessage)
                .setTitle(title)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton){}
                        }
                )
                .show();
    }
}
