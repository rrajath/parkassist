package com.example.ParkAssist.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.ParkAssist.R;

/**
 * Created by AMAN on 3/2/14.
 */
public class LoggerActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logger);
        Button logger = (Button) findViewById(R.id.bLogger);
        logger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoggerActivity.this, FingerprintActivity.class);
                startActivity(intent);

            }
        });


    }

    public void clearDB(View view) {
    }

}