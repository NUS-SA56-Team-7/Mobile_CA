package com.stephenphyo.ca_the_memory_game.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stephenphyo.ca_the_memory_game.R;
import com.stephenphyo.ca_the_memory_game.adapters.ListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BestScoreActivity extends AppCompatActivity {

    List<String> bestScoreList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_score);

        /*** Shared Preferences ***/
        SharedPreferences sharedPref = getSharedPreferences("saved_data", MODE_PRIVATE);
        if (sharedPref != null) {
            if (sharedPref.contains("best_scores")) {
                String savedBestScores = sharedPref.getString("best_scores", "");
                /*** If Saved Best Scores is more than 20, retrieve Best 20 Scores ***/
                if (deserializeSavedData(savedBestScores).size() > 20) {
                    bestScoreList =
                            deserializeSavedData(savedBestScores).subList(0, 20)
                                    .stream().map(score -> formatTimeString(score))
                                    .collect(Collectors.toList());
                }
                else {
                    bestScoreList =
                            deserializeSavedData(savedBestScores).stream()
                                    .map(score -> formatTimeString(score))
                                    .collect(Collectors.toList());
                }
            }
        }

        Log.d("Saved Best Scores", String.valueOf(bestScoreList));

        /*** List View ***/
        ListAdapter adapter = new ListAdapter(this, bestScoreList);

        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    public List<Integer> deserializeSavedData(String data){

        /*** Convert Saved Data String to List of Strings ***/
        List<String> savedDataList = Arrays.asList(data.split(","));

        /*** Convert Saved List of Stings (Seconds) to List of Integers (Seconds) ***/
        List<Integer> bestScoreList = new ArrayList<Integer>();
        for (String ele: savedDataList) {
            bestScoreList.add(Integer.parseInt(ele));
        }

        /*** Sort Scores (Seconds) ***/
        Collections.sort(bestScoreList);

        return bestScoreList;
    }

    public String formatTimeString(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

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
}