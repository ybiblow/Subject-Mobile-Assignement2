package com.example.subject_mobile_assignement_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.subject_mobile_assignement_2.fragments.ListFragment;
import com.example.subject_mobile_assignement_2.fragments.MapsFragment;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {
    private ArrayList<Integer> distances;
    private ListFragment listFragment;
    private MapsFragment mapsFragment;
    private Callback_List callbackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        listFragment = new ListFragment();
        mapsFragment = new MapsFragment();
        callbackList = new Callback_List() {
            @Override
            public void setMapLocation(double latitude, double longitude) {
                mapsFragment.changeMap(latitude, longitude);
            }
        };
        getSupportFragmentManager().beginTransaction().add(R.id.listFrame, listFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.mapsFrame, mapsFragment).commit();
        listFragment.setActivity(this);
        listFragment.setCallbackList(callbackList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}