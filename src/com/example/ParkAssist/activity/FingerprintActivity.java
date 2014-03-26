    package com.example.ParkAssist.activity;

    import android.app.Activity;
    import android.content.Intent;
    import android.graphics.Color;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Adapter;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.GridView;
    import com.example.ParkAssist.R;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;
    
    /**
     * Created by rrajath on 3/2/14.
     */
    public class FingerprintActivity extends Activity {

        GridView gridView;
        final int GRID_COLUMNS = 12;
        static String[] numbers = new String[336];
        int k =0;
        int count =1;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fingerprintlayout);

            for (int i = 0; i < 336; i++) {
                if((i)-((3*k+1)) == 0){
                        numbers[i] = "";
                        k++;
                } else{
                        numbers[i] = String.valueOf(count++);
                }
            }

            List<String> list = new ArrayList<String>(Arrays.asList(numbers));
            gridView = (GridView) findViewById(R.id.gridview);
            gridView.setNumColumns(GRID_COLUMNS);


            // Create adapter to set value for grid viewup
            Adapter adapter = new ArrayAdapter<String>(this,
                    R.layout.list_item, list);

            gridView.setAdapter(new CustomGridViewAdapter(this, numbers));

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    // Calculating the grid co-ordinates
                    int x = position / GRID_COLUMNS;
                    int y = position % GRID_COLUMNS;

                    // PNPPNPPNPPNP
                    switch (y) {
                        case 1:
                        case 4:
                        case 7:
                        case 10:

                            Intent intent = new Intent(FingerprintActivity.this, ScanActivity.class);

                            // Passing the X and Y co-ordinates to ScanActivity
                            intent.putExtra("x", x);
                            intent.putExtra("y", y);
                            startActivity(intent);

                            v.setBackgroundColor(Color.GREEN);
                    }
                }
            });
        }
    }