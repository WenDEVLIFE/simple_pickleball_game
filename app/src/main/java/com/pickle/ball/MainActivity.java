package com.pickle.ball;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.pickle.ball.game.GameSurfaceView;

public class MainActivity extends AppCompatActivity {

    private GameSurfaceView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameView = findViewById(R.id.gameSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}
