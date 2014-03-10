package com.example.ParkAssist;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by AMAN on 3/9/14.
 */


public class ViewActivity extends Activity {
    public ListView display_wifi_List;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);
        ArrayList<String> list = (ArrayList<String>) getIntent().getSerializableExtra("wifiScanList");
        display_wifi_List = (ListView)findViewById(R.id.wifiList);
        display_wifi_List.setAdapter(new ArrayAdapter<String>(ViewActivity.this , android.R.layout.simple_list_item_1,list));




    }
}