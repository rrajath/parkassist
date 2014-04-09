package com.example.ParkAssist.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.ParkAssist.R;
import com.example.ParkAssist.database.Datasource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by rrajath on 3/11/14.
 */
public class ManageDBActivity extends Activity {
    Datasource datasource;
    String table;
    Spinner spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.managedb);
        spinner = (Spinner) findViewById(R.id.spinner_tables);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tables, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void clearSequences(View view) {
        table = spinner.getSelectedItem().toString();
        datasource = new Datasource(this, table);
        datasource.open();
        datasource.deleteSequence(table);

        Toast.makeText(getApplicationContext(), table + " sequence deleted", Toast.LENGTH_LONG).show();
        datasource.close();
    }

    public void clearTables(View view) {
        table = spinner.getSelectedItem().toString();
        datasource = new Datasource(this, table);
        datasource.open();
        datasource.deleteTable(table);

        Toast.makeText(getApplicationContext(), table + " deleted", Toast.LENGTH_LONG).show();
        datasource.close();
    }

    public void viewDB(View view) {
        table = spinner.getSelectedItem().toString();
        Intent intent = new Intent(this, ViewDBActivity.class);
        intent.putExtra("table", table);
        startActivity(intent);
    }

    public void refreshDB(View view) {
        table = spinner.getSelectedItem().toString();
        datasource = new Datasource(this, table);
        datasource.open();
        datasource.refreshDB();
        Toast.makeText(getApplicationContext(), table + " re-created", Toast.LENGTH_LONG).show();
        datasource.close();
    }

    public void exportDB(View view) {
        table = spinner.getSelectedItem().toString();
        String filename = "";
        try {
            datasource = new Datasource(this, table);
            datasource.open();
            List rows = datasource.getAllRows();

            String path = getApplicationContext().getExternalFilesDir(null).getAbsolutePath();

            String data = "";
            for (Object row : rows) {
                String str = row.toString().trim();
                data += str.replace(";", ",").replace("    ", "") + "\n";
            }

            filename = path + "/" + table + ".txt";
            File file = new File(filename);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), table + " exported to " + filename, Toast.LENGTH_LONG).show();
    }
}
