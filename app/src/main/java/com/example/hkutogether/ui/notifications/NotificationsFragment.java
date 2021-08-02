package com.example.hkutogether.ui.notifications;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hkutogether.FriendInfo;
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

public class NotificationsFragment extends Fragment {

    private Button friend1, friend2, friend3;
    private String currentfriend1, currentfriend2, currentfriend3;
    private TextView fltv;
    private String name1, email1, yearofstudy1, otherinfo1;
    private String name2, email2, yearofstudy2, otherinfo2;
    private String name3, email3, yearofstudy3, otherinfo3;

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        friend1 = root.findViewById(R.id.friend1);
        friend2 = root.findViewById(R.id.friend2);
        friend3 = root.findViewById(R.id.friend3);
        fltv = root.findViewById(R.id.friendlisttextview);
        fltv.setText("Your friend list:");
        friend1.setVisibility(View.INVISIBLE);
        friend2.setVisibility(View.INVISIBLE);
        friend3.setVisibility(View.INVISIBLE);

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
                        JSONArray jsonArray = rootJSONObj.optJSONArray("username");
                        for (int i=0; i<jsonArray.length(); i++) {
                            usernames.add(jsonArray.getString(i));
                        }
                        for (int i=0; i<jsonArray.length(); i++) {
                            if (usernames.get(i).equals(MainPage.un)) {
                                currentfriend1=rootJSONObj.optJSONArray("friend1").getString(i);
                                currentfriend2=rootJSONObj.optJSONArray("friend2").getString(i);
                                currentfriend3=rootJSONObj.optJSONArray("friend3").getString(i);
                            }
                        }
                        for (int i=0; i<jsonArray.length(); i++) {
                            if (currentfriend1.equals("*")){
                                fltv.setText("Your friend list is empty.");
                                break;
                            }
                            else {
                                fltv.setText("Your friend list:");
                                if (usernames.get(i).equals(currentfriend1) && !currentfriend1.equals("*")){
                                    friend1.setVisibility(View.VISIBLE);

                                    name1 = rootJSONObj.optJSONArray("name").getString(i);
                                    email1 = rootJSONObj.optJSONArray("email").getString(i);
                                    yearofstudy1 = rootJSONObj.optJSONArray("yearofstudy").getString(i);
                                    otherinfo1 = rootJSONObj.optJSONArray("otherinfo").getString(i);
                                    if (!name1.equals("*")){ friend1.setText(currentfriend1+" ("+name1+")"); }
                                    else { friend1.setText(currentfriend1); }

                                }
                                if (usernames.get(i).equals(currentfriend2) && !currentfriend2.equals("*")){
                                    friend2.setVisibility(View.VISIBLE);

                                    name2 = rootJSONObj.optJSONArray("name").getString(i);
                                    email2 = rootJSONObj.optJSONArray("email").getString(i);
                                    yearofstudy2 = rootJSONObj.optJSONArray("yearofstudy").getString(i);
                                    otherinfo2 = rootJSONObj.optJSONArray("otherinfo").getString(i);
                                    if (!name2.equals("*")){ friend2.setText(currentfriend2+" ("+name2+")"); }
                                    else { friend2.setText(currentfriend2); }
                                }
                                if (usernames.get(i).equals(currentfriend3) && !currentfriend3.equals("*")){
                                    friend3.setVisibility(View.VISIBLE);

                                    name3 = rootJSONObj.optJSONArray("name").getString(i);
                                    email3 = rootJSONObj.optJSONArray("email").getString(i);
                                    yearofstudy3 = rootJSONObj.optJSONArray("yearofstudy").getString(i);
                                    otherinfo3 = rootJSONObj.optJSONArray("otherinfo").getString(i);
                                    if (!name3.equals("*")){ friend3.setText(currentfriend3+" ("+name3+")"); }
                                    else { friend3.setText(currentfriend3); }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute("");

        friend1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), FriendInfo.class);
                i.putExtra("username", currentfriend1);
                i.putExtra("name", name1);
                i.putExtra("email", email1);
                i.putExtra("yearofstudy", yearofstudy1);
                i.putExtra("otherinfo", otherinfo1);
                startActivity(i);
            }
        });
        friend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), FriendInfo.class);
                i.putExtra("username", currentfriend2);
                i.putExtra("name", name2);
                i.putExtra("email", email2);
                i.putExtra("yearofstudy", yearofstudy2);
                i.putExtra("otherinfo", otherinfo2);
                startActivity(i);
            }
        });
        friend3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), FriendInfo.class);
                i.putExtra("username", currentfriend3);
                i.putExtra("name", name3);
                i.putExtra("email", email3);
                i.putExtra("yearofstudy", yearofstudy3);
                i.putExtra("otherinfo", otherinfo3);
                startActivity(i);
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