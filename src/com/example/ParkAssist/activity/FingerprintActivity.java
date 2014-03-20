package com.example.ParkAssist.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import com.example.ParkAssist.R;
import com.example.ParkAssist.entity.Fingerprint;
import com.example.ParkAssist.entity.NavCell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rrajath on 3/2/14.
 */
public class FingerprintActivity extends Activity {

    GridView gridViewup;
    GridView gridViewdown;
    static String[] numbersdown = new String[336];
   static String[] numbersup = new String[28];
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprintlayout);

        for (int i = 0; i < 336; i++) {
            numbersdown[i] = String.valueOf(i+1);
        }
        for (int i = 0; i< 28 ;i++){
            numbersup[i] = String.valueOf(i+1);

        }

       List<String> listup = new ArrayList<String>(Arrays.asList(numbersup));
        List<String> listdown = new ArrayList<String>(Arrays.asList(numbersdown));
       // gridViewup = (GridView) findViewById(R.id.gridviewup);
       // gridViewup.setNumColumns(28);
        gridViewdown = (GridView) findViewById(R.id.gridviewdown);
        gridViewdown.setNumColumns(12);


        // Create adapter to set value for grid viewup
        ArrayAdapter<String> adapterup , adapterdown;
        adapterup = new ArrayAdapter<String>(this,
                R.layout.list_item, listup);
       adapterdown = new ArrayAdapter<String>(this,
                R.layout.list_item, listdown);

     //  gridViewup.setAdapter(adapterup);
        gridViewdown.setAdapter(new CustomGridViewAdapter(this, numbersdown));

        gridViewdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                NavCell navCell = new NavCell();
                int x = position/12;
                navCell.setXCord(x);
                int y = position%12;
                navCell.setYCord(y);
                Intent intent = new Intent(FingerprintActivity.this,ScanActivity. class );
                startActivity(intent);


               v.setBackgroundColor(Color.GREEN);


            }



     /*   gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(FingerprintActivity.this, ScanActivity.class);
                startActivity(intent);
                View child = ((ViewGroup)v).getChildAt(position);
                child.setBackgroundColor(Color.GREEN);

               // v.setBackgroundColor(Color.GREEN);
              //  gridView.invalidateViews();

           }
        });*/
    });
}}