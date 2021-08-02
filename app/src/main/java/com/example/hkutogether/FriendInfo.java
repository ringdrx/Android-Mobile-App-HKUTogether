package com.example.hkutogether;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FriendInfo extends AppCompatActivity {
    String username, name, email, yearofstudy, otherinfo;
    TextView fitv;
    Button friendinfoback;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendinfo);
        Intent i = getIntent();
        username = i.getStringExtra("username");
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        yearofstudy = i.getStringExtra("yearofstudy");
        otherinfo = i.getStringExtra("otherinfo");

        friendinfoback= findViewById(R.id.friendinfoback);
        fitv = findViewById(R.id.friendInfoTextView);
        fitv.setText("Username: "+username+"\nName: "+name+"\nEmail: "+email+"\nYear of Study: "+yearofstudy+"\nOther Information: "+otherinfo);

        friendinfoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
