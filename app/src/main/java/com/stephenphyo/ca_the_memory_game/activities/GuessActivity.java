package com.stephenphyo.ca_the_memory_game.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.stephenphyo.ca_the_memory_game.R;
import com.stephenphyo.ca_the_memory_game.adapters.GridAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GuessActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    /*** Constants & Variables ***/
    private int clickCount = 0;
    private int scoreCount = 0;
    int elapsedSeconds = 0;

    List<String> shuffledImages;
    List<String> showImages;
    List<Integer> picked_indices = new ArrayList<Integer>();
    List<Integer> matched_indices = new ArrayList<Integer>();

    /*** Views & Components ***/
    MediaPlayer noMatch;
    MediaPlayer soundBackgroundMusic;
    GridView gridView;
    GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        noMatch = MediaPlayer.create(this, R.raw.no_match);
        soundBackgroundMusic = MediaPlayer.create(this, R.raw.background_music);
        soundBackgroundMusic.start();
        soundBackgroundMusic.setLooping(true);

        /*** Retrieve Selected Images from Fetch Activity ***/
        Intent intent = getIntent();
        List<String> selectedImages = intent.getStringArrayListExtra("selected_images");

        selectedImages = repeatList(2, selectedImages);
        Collections.shuffle(selectedImages);

        shuffledImages = selectedImages;

        /*** Grid View ***/
        showImages = Collections.nCopies(12, "drawable:image");

        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int rowHeight = (((screenHeight * 10) / 11) / 4) - 40;
        adapter = new GridAdapter(this, showImages, rowHeight);

        gridView = findViewById(R.id.grid_guess);
        if (gridView != null) {
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(this);
        }

        runTimer();
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id) {

        if (!picked_indices.contains(pos) && !matched_indices.contains(pos)) {
            clickCount++;
            picked_indices.add(pos);

            adapter.updateData(pos, shuffledImages.get(pos));
            Log.d("Picked Data", String.valueOf(picked_indices));
        }

        if (!matched_indices.contains(pos)) {
            /*** Player has already selected 2 images ***/
            if (clickCount == 2) {
                String firstPick = shuffledImages.get(picked_indices.get(0));
                String secondPick = shuffledImages.get(picked_indices.get(1));
                gridView.setEnabled(false);

                if (firstPick.equals(secondPick)) {
                    scoreCount++;
                    gridView.setEnabled(true);

                    TextView textScore = findViewById(R.id.text_score);
                    textScore.setText(String.format("%d of 6 matches", scoreCount));

                    matched_indices.add(picked_indices.get(0));
                    picked_indices.remove(0);
                    matched_indices.add(picked_indices.get(0));
                    picked_indices.remove(0);
                }
                else {
                    noMatch.start();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            gridView.setEnabled(true);
                            adapter.updateData(picked_indices.get(0), "drawable:image");
                            adapter.updateData(picked_indices.get(1), "drawable:image");

                            picked_indices.remove(0);
                            picked_indices.remove(0);
                        }
                    };

                    Handler handler = new Handler();
                    handler.postDelayed(runnable, 2000);
                }

                clickCount = 0;
            }
        }

        if (scoreCount == 6) {
            SharedPreferences sharedPref = getSharedPreferences("saved_data", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            if (sharedPref != null) {
                /*** If Best Scores data is already existed in Shared Preferences ***/
                /*** Deserialize it, put new score, serialize it and save back ***/
                if (sharedPref.contains("best_scores")) {
                    List<Integer> savedBestScores =
                            deserializeSavedData(
                                    sharedPref.getString("best_scores", ""));
                    savedBestScores.add(elapsedSeconds);
                    editor.putString("best_scores", serializeSavedData(savedBestScores));
                    editor.apply();
                }
                /*** Else create new Best Scores data, serialize and save it ***/
                else {
                    List<Integer> savedBestScores = new ArrayList<Integer>();
                    savedBestScores.add(elapsedSeconds);
                    editor.putString("best_scores", serializeSavedData(savedBestScores));
                    editor.apply();
                }
            }

            Intent intent = new Intent(GuessActivity.this, GameWonActivity.class);
            intent.putExtra("elapsed_time", formatTimeString(elapsedSeconds));
            startActivity(intent);

            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        soundBackgroundMusic.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundBackgroundMusic.stop();
        soundBackgroundMusic.release();
        noMatch.release();
    }

    private void runTimer(){
        TextView textCountdown = findViewById(R.id.text_countdown);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                elapsedSeconds++;
                textCountdown.setText(formatTimeString(elapsedSeconds));

                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    private String formatTimeString(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        String hoursFormat;
        String minutesFormat;
        String secondsFormat;

        if (hours < 10) hoursFormat = "0" + String.valueOf(hours);
        else hoursFormat = String.valueOf(hours);

        if (minutes < 10) minutesFormat = "0" + String.valueOf(minutes);
        else minutesFormat = String.valueOf(minutes);

        if (seconds < 10) secondsFormat = "0" + String.valueOf(seconds);
        else secondsFormat = String.valueOf(seconds);

        return String.format("%s:%s:%s", hoursFormat, minutesFormat, secondsFormat);
    }

    private String serializeSavedData(List<Integer> bestScoreList) {

        String savedString = "";
        for (Integer history : bestScoreList) {
            savedString += history.toString() + ',';
        }
        return savedString;
    }

    private List<Integer> deserializeSavedData(String str){

        String[] savedArray = str.split(",");
        List<String> savedStringList = Arrays.asList(savedArray);

        List<Integer> savedIntegerList = new ArrayList<Integer>();

        for (String ele: savedStringList) {
            savedIntegerList.add(Integer.parseInt(ele));
        }

        Collections.sort(savedIntegerList);

        return savedIntegerList;
    }

    private List<String> repeatList(int times, List<String> originalList) {
        List<String> returnList =  Collections.nCopies(times, originalList)
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return returnList;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GuessActivity.this, FetchActivity.class);
        startActivity(intent);
        finish();
    }
}