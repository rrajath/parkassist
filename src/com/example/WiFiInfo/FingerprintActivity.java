package com.example.WiFiInfo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rrajath on 3/2/14.
 */
public class FingerprintActivity extends Activity {

    GridView gridView;

    static String[] numbers = new String[112];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);

        for (int i = 0; i < 112; i++) {
            numbers[i] = String.valueOf(i+1);
        }

        List<String> list = new ArrayList<String>(Arrays.asList(numbers));

        gridView = (GridView) findViewById(R.id.gridview1);

        // Create adapter to set value for grid view
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,
                R.layout.list_item, list);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(FingerprintActivity.this, ScanActivity.class);
                startActivity(intent);

                v.setBackgroundColor(Color.GREEN);

           }
        });
    }
}