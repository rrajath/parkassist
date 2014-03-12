package com.example.ParkAssist;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
}
