package com.example.vishnu.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    public static final String SEARCH = "Search";
    public static final int REQUEST_CODE = 3;
    ListView listView;
    DataSource dataSource;


    private boolean isSearchOpen = true;
    private MenuItem item;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dataSource = new DataSource(this);
        dataSource.open();

        listView = (ListView) findViewById(R.id.searchResult);

        String query = getIntent().getStringExtra(MainActivity.QUERY);

        final List<Contact> contactList = dataSource.search(query);

        ContactArrayAdapter adapter = new ContactArrayAdapter(this, 0, contactList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, DetailsActivity.class);
                intent.putExtra(MainActivity.ID, contactList.get(position).getId());
                intent.putExtra(SEARCH, true);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
