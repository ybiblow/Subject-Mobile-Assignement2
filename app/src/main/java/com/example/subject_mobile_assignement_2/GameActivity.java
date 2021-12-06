package com.example.subject_mobile_assignement_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private LinearLayout linearLayoutArrows;
    float x;
    float y;
    float z;
    private String name = "";
    private int sensorEnabled = 0;
    private int numOfHearts = 3;
    private int randomNumber1;
    private int randomNumber2;
    private int distance = 0;
    private int numOfCoinsGathered = 0;
    private int stopHandler = 0;
    private int dropCoinFlag = 0;
    private double longitude = 0;
    private double latitude = 0;
    private Vibrator v;
    private Handler handler1;
    private Runnable runnable1;
    private Handler handler2;
    private Runnable runnable2;
    private LocationManager lm;
    private TextView textViewCoins;
    private TextView textViewDistance;
    private ArrayList<ImageView> coins;
    private ArrayList<ImageView> stones;
    private LocationListener locationListener;
    private ArrayList<ImageView> imageViewHearts;
    private ArrayList<ImageView> imageViewSpaceships;
    private Random random = new Random();
    private SensorManager sensorManager;
    private Sensor sensor;
    private MediaPlayer coinSoundMediaPlayer;
    private MediaPlayer crashSoundMediaPlayer;
    private MediaPlayer losingSoundMediaPlayer;
    private Dialog dialogPopup;
    private Button buttonSubmit;
    private EditText editTextGetName;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViews();
        addViewsToArrayLists();
        initLocation();
        checkSensorEnabled();
        init_Handlers_Runnables();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorEnabled == 1)
            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorEnabled == 1)
            sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    public void onBackPressed() {

        stopGame();
        //Record record = new Record(distance, numOfCoinsGathered, latitude, longitude, name);
        boolean isRecordInTopTen = isRecordInTopTen();
        if (isRecordInTopTen) {
            createPopup();
        } else {
            super.onBackPressed();
            exit();

        }


        /*Bundle bundle = new Bundle();
        bundle.putSerializable("RECORD", (Serializable) record);
        intent.putExtra("BUNDLE", bundle);*/

        //check that Leaderboard exists and check if the new record should be added to leaderboard array if so, add name to record.
    }

    private void exit() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isRecordInTopTen() {
        Record record = new Record(distance, numOfCoinsGathered, latitude, longitude, name);
        if (Leaderboard.getInstance().getRecordsArray() != null) {
            if (Leaderboard.getInstance().isRecordAddedToLeaderboard(record)) {
                return true;
//                Leaderboard.getInstance().addRecordToRecordsArray(getApplicationContext(), record);
            } else {
                return false;
            }
        }
        return false;
    }

    private void stopGame() {
        stopHandler = 1;
        handler1.removeCallbacks(runnable1);
        if (sensorEnabled == 1)
            handler2.removeCallbacks(runnable1);
        lm.removeUpdates(locationListener);
    }

    public void createPopup() {
        AppCompatActivity activity = this;
        dialogPopup = new Dialog(this);
        dialogPopup.setContentView(R.layout.get_name_popup);
        dialogPopup.show();
        editTextGetName = dialogPopup.findViewById(R.id.editTextGetName);
        buttonSubmit = dialogPopup.findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editTextGetName.getText().toString();
                Record record = new Record(distance, numOfCoinsGathered, latitude, longitude, name);
                Leaderboard.getInstance().addRecordToRecordsArray(activity, record);
                dialogPopup.dismiss();
                exit();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
            }
        }

    }

    private void checkSensorEnabled() {
        Intent intent = getIntent();
        sensorEnabled = intent.getIntExtra("SENSOR_ENABLED", 0);
        if (sensorEnabled == 1) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            linearLayoutArrows.setVisibility(View.INVISIBLE);
            Log.i("info", "Sensor Enabled: " + sensorEnabled);
        }
    }

    private void initLocation() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
        }
    }

    private void addViewsToArrayLists() {
        coins = new ArrayList<ImageView>();
        stones = new ArrayList<ImageView>();
        // add stones to arraylist
        for (int i = 0; i < 35; i++) {
            stones.add(findViewById(getResources().getIdentifier("imageViewStone" + i, "id", getPackageName())));
        }
        // add coins to arraylist
        for (int i = 0; i < 35; i++) {
            coins.add(findViewById(getResources().getIdentifier("imageViewCoin" + i, "id", getPackageName())));
        }
        // add spaceships to arraylist
        imageViewSpaceships = new ArrayList<ImageView>();
        for (int i = 0; i < 5; i++) {
            imageViewSpaceships.add(findViewById(getResources().getIdentifier("imageViewSpaceship" + i, "id", getPackageName())));
        }
        // add hearts to arraylist
        imageViewHearts = new ArrayList<ImageView>();
        for (int i = 0; i < 3; i++) {
            imageViewHearts.add(findViewById(getResources().getIdentifier("imageViewHeart" + i, "id", getPackageName())));
        }
    }

    private void findViews() {
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        textViewDistance = findViewById(R.id.textViewDistance);
        textViewCoins = findViewById(R.id.textViewCoins);
        linearLayoutArrows = findViewById(R.id.linearLayoutArrows);
        coinSoundMediaPlayer = MediaPlayer.create(this, R.raw.coin_sound);
        crashSoundMediaPlayer = MediaPlayer.create(this, R.raw.crash_sound);
        losingSoundMediaPlayer = MediaPlayer.create(this, R.raw.losing_sound);
    }

    private void init_Handlers_Runnables() {
        handler1 = new Handler();
        runnable1 = new Runnable() {
            @Override
            public void run() {
                dropObjects();
                if (stopHandler == 0) {
                    handler1.postDelayed(this, 1000);
                } else {
                    handler1.removeCallbacks(runnable1);
                }
            }
        };
        handler1.post(runnable1);
        if (sensorEnabled == 1) {
            handler2 = new Handler();
            runnable2 = new Runnable() {
                @Override
                public void run() {
                    // Code
                    float currentX = x;
                    // boolean ok = oldX < 0.4 && oldX > -0.4;
                    if (currentX > 1.5) {
                        //lock = 1;
                        moveSpaceship(0);
                    }
                    if (currentX < -1.5) {
                        //lock = 1;
                        moveSpaceship(1);
                    }
                    Log.i("info", "___________Accelerometer changed, new values are: (" + x + "," + y + "," + z + ")");
                    handler1.postDelayed(this, 300);
                    if (stopHandler != 0) {
                        handler1.removeCallbacks(runnable2);
                    }
                }
            };
            handler2.post(runnable2);
        }
    }

    private void dropObjects() {

        for (int i = 34; i >= 0; i--) {
            if (i > 29) {
                stones.get(i).setVisibility(View.INVISIBLE);
                coins.get(i).setVisibility(View.INVISIBLE);
            } else {
                if (stones.get(i).getVisibility() == View.VISIBLE) {
                    stones.get(i).setVisibility(View.INVISIBLE);
                    stones.get(i + 5).setVisibility(View.VISIBLE);
                }
                if (coins.get(i).getVisibility() == View.VISIBLE) {
                    coins.get(i).setVisibility(View.INVISIBLE);
                    coins.get(i + 5).setVisibility(View.VISIBLE);
                }
            }
        }

        randomNumber1 = random.nextInt(1000) % 5;
        stones.get(randomNumber1).setVisibility(View.VISIBLE);
        if (dropCoinFlag == 2) {
            //drop coin

            randomNumber2 = random.nextInt(1000) % 5;

            while (randomNumber2 == randomNumber1)
                randomNumber2 = random.nextInt(1000) % 5;

            coins.get(randomNumber2).setVisibility(View.VISIBLE);

            dropCoinFlag = -1;
        }
        dropCoinFlag++;
        checkCrash();
        checkCoin();
        increaseDistance();
    }

    private void increaseCoins() {
        numOfCoinsGathered++;
        String string = textViewDistance.getText().toString();
        String splitString[] = new String[2];
        splitString = string.split(": ");
        textViewCoins.setText("Coins" + ": " + numOfCoinsGathered + "$");

    }

    private void checkCoin() {
        int spaceshipTag = getVisibleSpaceshipTag();
        if (coins.get(spaceshipTag).getVisibility() == View.VISIBLE) {
            Log.i("info", "You collected a coin!");
            increaseCoins();
            coins.get(spaceshipTag).setVisibility(View.INVISIBLE);
            coinSoundMediaPlayer.start();
        }
    }

    private void checkCrash() {
        int spaceshipTag = getVisibleSpaceshipTag();
        if (stones.get(spaceshipTag).getVisibility() == View.VISIBLE) {
            Log.i("info", "You have been hit!");
            Toast.makeText(getApplicationContext(), "You have been hit!", Toast.LENGTH_SHORT).show();
            crashSoundMediaPlayer.start();
            vibrate();
            reduceHeart();
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
                losingSoundMediaPlayer.start();
                onBackPressed();
            }
        }
    }

    private void increaseDistance() {
        distance++;
        String string = textViewDistance.getText().toString();
        String splitString[] = new String[2];
        splitString = string.split(": ");
        textViewDistance.setText("Distance" + ": " + distance + "m");
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

    private int getVisibleSpaceshipTag() {
        for (ImageView spaceship : imageViewSpaceships) {
            if (spaceship.getVisibility() == View.VISIBLE)
                return Integer.parseInt(spaceship.getTag().toString());
        }
        return -1;
    }

    public void moveSpaceshipArrowClick(View view) {
        int direction = Integer.parseInt(view.getTag().toString());
        moveSpaceship(direction);
    }

    public void moveSpaceship(int lor) {
        int direction = lor;
        int spaceshipTag = getVisibleSpaceshipTag();

        if (direction == 0) {
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
        checkCoin();
    }


}
