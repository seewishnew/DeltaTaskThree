package com.example.vishnu.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends AppCompatActivity {

    public static final String LOG_TAG = "DetailsActivity";
    Contact contact;

    DataSource dataSource;

    ImageView imageView;
    TextView number;
    TextView email;
    TextView address;
    TextView birthday;
    TextView relationship;

    private boolean search = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        search = intent.getBooleanExtra(SearchActivity.SEARCH, false);
        dataSource = new DataSource(this);

        contact = dataSource.findSpecific(intent.getLongExtra(MainActivity.ID, 0));

        getSupportActionBar().setTitle(contact.getName());


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;


        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.getLayoutParams().height = (int) (heightPixels *0.5);

        int resID = getResources().getIdentifier(
                "image_" + contact.getPhoneNo(),
                "drawable",
                this.getPackageName());

        if(resID==0){
            imageView.setImageResource(R.mipmap.ic_contact_default);
        }

        else
            imageView.setImageResource(resID);

        if(contact.hasPhoneNo())
        {
            number = (TextView) findViewById(R.id.tvNo);
            number.setVisibility(View.VISIBLE);
            number.setText("Phone Number: " + contact.getPhoneNo());
        }

        if(contact.hasEmailID()){
            email = (TextView) findViewById(R.id.tvEmail);
            email.setVisibility(View.VISIBLE);
            email.setText("Email: " + contact.getEmailID());
        }

        if(contact.hasAddress()){
            address = (TextView) findViewById(R.id.tvAddress);
            address.setVisibility(View.VISIBLE);
            address.setText("Address: " + contact.getAddress());
        }

        if(contact.hasBirthday()){
            birthday = (TextView) findViewById(R.id.tvBirthday);
            birthday.setVisibility(View.VISIBLE);
            birthday.setText("Birthday: " + contact.getBirthday());
        }

        if(contact.hasRelationship())
        {
            relationship = (TextView) findViewById(R.id.tvRelationship);
            relationship.setVisibility(View.VISIBLE);
            relationship.setText("Relationship: " + contact.getRelationship());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(LOG_TAG, "In onOptionsItemSelected");

        switch (item.getItemId()){
            case android.R.id.home:
                if(search){
                    Log.d(LOG_TAG, "search is true");
                    setResult(RESULT_OK);
                    finish();
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(search)
        {
            setResult(RESULT_OK);
            finish();
        }
    }
}
