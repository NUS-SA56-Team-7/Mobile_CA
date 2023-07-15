package com.stephenphyo.ca_the_memory_game.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stephenphyo.ca_the_memory_game.R;

public class GameWonActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_won);

        Button buttonPlayAgain = findViewById(R.id.button_play_again);
        buttonPlayAgain.setOnClickListener(this);

        Button buttonViewHistory = findViewById(R.id.button_view_history);
        buttonViewHistory.setOnClickListener(this);

        Intent intent = getIntent();
        String elapsedTime = intent.getStringExtra("elapsed_time");

        TextView textTimeElapsed = findViewById(R.id.text_time_elapsed);
        textTimeElapsed.setText(elapsedTime);
    }

    @Override
    public void onClick(View v){
        int id = v.getId();

        if (id == R.id.button_play_again) {
            Intent intent = new Intent(GameWonActivity.this, FetchActivity.class);
            startActivity(intent);
        }
        if (id == R.id.button_view_history) {
            Intent intent = new Intent(GameWonActivity.this, BestScoreActivity.class);
            startActivity(intent);
        }
    }
}