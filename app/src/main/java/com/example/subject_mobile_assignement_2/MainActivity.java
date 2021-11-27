package com.example.subject_mobile_assignement_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {

    private MaterialButton button_Play;
    private MaterialButton button_Leaderboard;
    private SwitchMaterial switch_Sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_Play = findViewById(R.id.button_Play);
        button_Leaderboard = findViewById(R.id.button_Leaderboard);
        switch_Sensor = findViewById(R.id.switch_Sensor);

        button_Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("info", "play button pressed");
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(intent);
            }
        });

        button_Leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("info", "button pressed");
            }
        });

        switch_Sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("info", "switch pressed");
            }
        });

    }
}