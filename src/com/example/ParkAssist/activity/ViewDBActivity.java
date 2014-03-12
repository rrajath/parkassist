package com.example.ParkAssist.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.ParkAssist.database.Datasource;
import com.example.ParkAssist.R;

import java.util.List;

/**
 * Created by rrajath on 3/11/14.
 */
public class ViewDBActivity extends Activity {

    Datasource datasource;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.database);

        // Get table name from ManageDB Activity
        Intent intent = getIntent();
        String table = "";
        table = intent.getStringExtra("table");

        // Display table name
        TextView tvTableName = (TextView) findViewById(R.id.tvCellDetails);
        tvTableName.setText(table);

        datasource = new Datasource(this, table);
        datasource.open();

        List records = datasource.getAllRows();

        StringBuilder builder = new StringBuilder();

        for (Object row : records) {
            builder.append(row.toString()).append("_");
        }

        String st = builder.toString();

        String[] rows = st.split("_");
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tab);
        tableLayout.setBackgroundColor(Color.WHITE);
        tableLayout.setStretchAllColumns(true);
        tableLayout.removeAllViews();

        for (int i = 0; i < rows.length; i++) {
            String row = rows[i];
            TableRow tableRow = new TableRow(getApplicationContext());
            tableRow.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tableRow.setBackgroundColor(Color.LTGRAY);
            final String[] cols = row.split(";");

            for (int j = 0; j < cols.length; j++) {
                final String col = cols[j];
                TextView columnsView = new TextView(getApplicationContext());
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                columnsView.setLayoutParams(params);
                columnsView.setTextColor(Color.BLACK);
                columnsView.setText(String.format("%17s", col));
                tableRow.addView(columnsView);
            }
            tableLayout.addView(tableRow);
        }

    }
}
