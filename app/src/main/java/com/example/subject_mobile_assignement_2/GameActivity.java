package com.example.subject_mobile_assignement_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private Vibrator v;
    private Handler handler;
    private Runnable runnable;
    private Random random = new Random();
    private TextView textViewDistance;
    private int numOfHearts;
    private int randomNumber;
    private int distance = 0;
    private ArrayList<ImageView> stones;
    private ArrayList<ImageView> imageViewSpaceships;
    private ArrayList<ImageView> imageViewHearts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        textViewDistance = findViewById(R.id.textViewDistance);

        stones = new ArrayList<ImageView>();
        for (int i = 0; i < 35; i++) {
            stones.add(findViewById(getResources().getIdentifier("imageViewStone" + i, "id", getPackageName())));
        }

        imageViewSpaceships = new ArrayList<ImageView>();
        for (int i = 0; i < 5; i++) {
            imageViewSpaceships.add(findViewById(getResources().getIdentifier("imageViewSpaceship" + i, "id", getPackageName())));
        }

        imageViewHearts = new ArrayList<ImageView>();
        for (int i = 0; i < 3; i++) {
            imageViewHearts.add(findViewById(getResources().getIdentifier("imageViewHeart" + i, "id", getPackageName())));
        }

        numOfHearts = 3;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                dropRocks();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
        finish();
    }

    private void dropRocks() {

        for (int i = 34; i >= 0; i--) {
            if (i > 29) {
                stones.get(i).setVisibility(View.INVISIBLE);
            } else if (stones.get(i).getVisibility() == View.VISIBLE) {
                stones.get(i).setVisibility(View.INVISIBLE);
                stones.get(i + 5).setVisibility(View.VISIBLE);
            }
        }

        randomNumber = random.nextInt(1000) % 5;
        stones.get(randomNumber).setVisibility(View.VISIBLE);
        checkCrash();
        increaseDistance();
    }

    private void increaseDistance() {
        distance++;
        String string = textViewDistance.getText().toString();
        String splitString[] = new String[2];
        splitString = string.split(": ");
        textViewDistance.setText("Distance" + ": " + distance + "m");

    }

    private void checkCrash() {
        int spaceshipTag = getVisibleSpaceshipTag();
        if (stones.get(spaceshipTag).getVisibility() == View.VISIBLE) {
            Log.i("info", "You have been hit!");
            Toast.makeText(getApplicationContext(), "You have been hit!", Toast.LENGTH_SHORT).show();
            vibrate();
            reduceHeart();
        }
    }

    private void vibrate() {
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    private void reduceHeart() {
        if (numOfHearts != 0) {
            for (ImageView heart : imageViewHearts) {
                if (heart.getVisibility() == View.VISIBLE) {
                    heart.setVisibility(View.INVISIBLE);
                    numOfHearts--;
                    break;
                }
            }
        } else {
            for (ImageView heart : imageViewHearts) {
                heart.setVisibility(View.VISIBLE);
                numOfHearts = 3;
            }
        }
    }

    private int getVisibleSpaceshipTag() {
        for (ImageView spaceship : imageViewSpaceships) {
            if (spaceship.getVisibility() == View.VISIBLE)
                return Integer.parseInt(spaceship.getTag().toString());
        }
        return -1;
    }

    public void moveSpaceship(View view) {
        int arrowTag = Integer.parseInt(view.getTag().toString());
        int spaceshipTag = getVisibleSpaceshipTag();

        if (arrowTag == 0) {
            //move left
            if (spaceshipTag % 5 != 0) {
                imageViewSpaceships.get(spaceshipTag % 5).setVisibility(View.INVISIBLE);
                imageViewSpaceships.get(spaceshipTag % 5 - 1).setVisibility(View.VISIBLE);
            }
        } else {
            //move right
            if (spaceshipTag % 5 != 4) {
                imageViewSpaceships.get(spaceshipTag % 5).setVisibility(View.INVISIBLE);
                imageViewSpaceships.get(spaceshipTag % 5 + 1).setVisibility(View.VISIBLE);
            }
        }
        checkCrash();
    }

}
