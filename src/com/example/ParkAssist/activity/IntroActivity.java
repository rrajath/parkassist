package com.example.ParkAssist.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.ParkAssist.R;
import com.example.ParkAssist.database.Datasource;
import com.example.ParkAssist.entity.ParkCell;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by rrajath on 3/4/14.
 */
public class IntroActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        // Logger Button
        Button bLogger = (Button) findViewById(R.id.bLogger);
        bLogger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, FingerprintActivity.class);
                startActivity(intent);
            }
        });

        // ManageDB Button
        Button bClearDB = (Button) findViewById(R.id.bManageDB);
        bClearDB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroActivity.this, ManageDBActivity.class);
                startActivity(intent);
            }
        });

        // Loading JSON data Button
        Button bLoadParkCellData = (Button) findViewById(R.id.bLoadParkCellData);
        bLoadParkCellData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LoadJSON loadJSON = new LoadJSON();
                ArrayList<ParkCell> parkCells = new ArrayList<ParkCell>();
                String url = "https://www.dropbox.com/s/7bnrr8ba5tcqsn9/parkCells.json";
                loadJSON.execute(url);

            }
        });
    }

    private class LoadJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpGet httppost = new HttpGet("http://p1cdn2static.sharpschool.com/UserFiles/Servers/Server_442934/File/Neurology/parkcells.json");

            // Depends on your web service
            httppost.setHeader("Content-type", "text/html");

            InputStream inputStream = null;
            String result = null;
            try {
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line).append("\n");
                }
                result = sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try{
                    if(inputStream != null)
                        inputStream.close();
                } catch(Exception ignored) {

                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonObject;
            JSONArray jsonArray = null;
            try {
                jsonObject = new JSONObject(result);
                jsonArray = jsonObject.getJSONArray("parking_cells");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ParkCell parkCell = new ParkCell();
            String cellType = "";
            Datasource datasource = new Datasource(getApplicationContext(), "parking_table");
            datasource.open();

            assert jsonArray != null;
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject object = jsonArray.getJSONObject(i);
                    parkCell.setXCord(object.getInt("x"));
                    parkCell.setYCord(object.getInt("y"));
                    parkCell.setNavCellId(object.getInt("nav_cell_id"));
                    cellType = object.getString("cell_type");
                    if (cellType.equals("P")) {
                        datasource.insertParkCell(parkCell);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            datasource.close();
        }
    }
}
