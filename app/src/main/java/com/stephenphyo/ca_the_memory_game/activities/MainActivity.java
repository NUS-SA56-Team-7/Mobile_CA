package com.stephenphyo.ca_the_memory_game.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.stephenphyo.ca_the_memory_game.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonPlay = findViewById(R.id.button_play);
        buttonPlay.setOnClickListener(this);

        Button buttonViewHistory = findViewById(R.id.button_view_history);
        buttonViewHistory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.button_play) {
            Intent intent = new Intent(this, FetchActivity.class);
            startActivity(intent);
        }

        if (id == R.id.button_view_history) {
            Intent intent = new Intent(this, BestScoreActivity.class);
            startActivity(intent);
        }
    }
}