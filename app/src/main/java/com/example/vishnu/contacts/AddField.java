package com.example.vishnu.contacts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AddField extends AppCompatActivity {

    public static final String CHOICE = "Choice";
    public static final String TAG = "AddField";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_field);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*0.7), (int)(height*0.5));

        ListView listView = (ListView) findViewById(R.id.field);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,
                R.array.fields,
                android.R.layout.simple_list_item_1);

        listView.setAdapter(adapter);

        final Intent intent = new Intent();

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Log.d(TAG, "" + position);
               intent.putExtra(CHOICE, position);
               setResult(RESULT_OK, intent);
               finish();
           }
       });
    }
}
