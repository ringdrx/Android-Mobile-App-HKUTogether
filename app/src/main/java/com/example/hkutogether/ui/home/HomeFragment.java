package com.example.hkutogether.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hkutogether.AddFriend;
import com.example.hkutogether.Googlemap;
import com.example.hkutogether.MainPage;
import com.example.hkutogether.R;
import com.example.hkutogether.Register;
import com.example.hkutogether.SwitchPassword;

public class HomeFragment extends Fragment {

    String un = MainPage.un;
    Button findfriend, changepassword, logout, addfriend;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        addfriend=root.findViewById(R.id.add);
        findfriend= root.findViewById(R.id.find);
        changepassword= root.findViewById(R.id.changepassword);
        logout= root.findViewById(R.id.logout);

        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), AddFriend.class);
                startActivity(i);
            }
        });

        findfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainPage.currentLocation!=null){
                    Intent i = new Intent(getContext(), Googlemap.class);
                    startActivity(i);
                } else { Toast.makeText(getContext(), "Location not available.",Toast.LENGTH_SHORT).show(); }
            }
        });

        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SwitchPassword.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Log Out")
                        .setMessage("Are you sure to log out?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MainPage.finishActivity=true;
                            }
                        }).setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        return root;
    }
}