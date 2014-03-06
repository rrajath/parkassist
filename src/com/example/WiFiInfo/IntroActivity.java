package com.example.WiFiInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by rrajath on 3/4/14.
 */
public class IntroActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        Button bLogger = (Button) findViewById(R.id.bLogger);
        bLogger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, FingerprintActivity.class);
                startActivity(intent);
            }
        });
    }
}
