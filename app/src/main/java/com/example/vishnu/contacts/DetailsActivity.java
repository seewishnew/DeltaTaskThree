package com.example.vishnu.contacts;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class DetailsActivity extends AppCompatActivity {

    public static final String LOG_TAG = "DetailsActivity";
    public static final int PICK_IMAGE_REQUEST = 5;
    public static final int REQUEST_CODE_EDIT_DETAILS = 8;
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
        imageView.setLongClickable(true);
        imageView.setClickable(true);


        if(MainActivity.profileEditable) {
            Log.d(LOG_TAG, "Entering onLongClick");
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Log.d(LOG_TAG, "Inside onLongClick");

                    final CharSequence items[] = {"Choose Photo", "Remove Photo", "Cancel"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                    builder.setTitle("Choose Photo");
                    builder.setItems(items,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (items[which].equals("Choose Photo")) {

                                        Intent intent;

                                        if(Build.VERSION.SDK_INT<19) {
                                            intent = new Intent();
                                            intent.setAction(Intent.ACTION_GET_CONTENT);
                                        }

                                        else{
                                            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        }

                                        intent.setType("image/*");

                                        startActivityForResult(Intent.createChooser(intent, "Choose from"),
                                                PICK_IMAGE_REQUEST);
                                    }

                                    else if (items[which].equals("Remove Photo")) {
                                        contact.setUri(null);
                                        dataSource.update(contact);
                                        imageView.setImageResource(R.mipmap.ic_contact_default);
                                    }

                                    else {
                                        dialog.dismiss();
                                    }
                                }
                            });

                    builder.show();

                    return false;
                }
            });
        }

        if(!(contact.hasUri() && MainActivity.profileEditable)){
            imageView.setImageResource(R.mipmap.ic_contact_default);
        }

        else {
            Bitmap bitmap = null;
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                        Uri.parse(contact.getUri()));
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        refreshDetails();

    }

    private void refreshDetails() {
        if(contact.hasPhoneNo())
        {
            number = (TextView) findViewById(R.id.tvNo);
            number.setVisibility(View.VISIBLE);
            number.setText("Phone Number: " + contact.getPhoneNo());
            if(MainActivity.callable){
                number.setTextColor(Color.GREEN);
//                ImageButton imageButton = (ImageButton) findViewById(R.id.callButton);
//                imageButton.setVisibility(View.VISIBLE);
            }
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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_details_options, menu);

        return super.onCreateOptionsMenu(menu);
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
                break;

            case R.id.edit_menu_option:
                Intent intent = new Intent(DetailsActivity.this, Main2Activity.class);
                intent.putExtra(MainActivity.ID, contact.getId());
                intent.putExtra(MainActivity.UPDATE, true);
                startActivityForResult(intent, REQUEST_CODE_EDIT_DETAILS);

                break;

            case R.id.delete_menu_option:
                new AlertDialog.Builder(this)
                        .setTitle("Delete " + contact.getName()+"?")
                        .setMessage("Are you sure you want to delete " +
                                contact.getName() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dataSource.deleteContact(contact.getId());
                                Log.d(LOG_TAG, "setting resultok");
                                setResult(RESULT_OK);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            if(data!=null && data.getData()!=null){
                Uri uri = data.getData();
                contact.setUri(uri.toString());

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                dataSource.update(contact);
            }
        }

        if(requestCode == REQUEST_CODE_EDIT_DETAILS && resultCode == RESULT_OK){
            contact = dataSource.findSpecific(data.getLongExtra(Main2Activity.RESULT, 0));
            Log.d(LOG_TAG, "refreshing details " + contact.getRelationship());
            refreshDetails();
        }
    }

    public void call(View view) {

        if(MainActivity.callable || PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE)
                )
        {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + contact.getPhoneNo()));
            startActivity(intent);
        }

        else{
            Toast.makeText(this, "Enable permission to make calls", Toast.LENGTH_SHORT).show();
        }
    }

    public void emailSomeone(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", contact.getEmailID(), null
        ));

        startActivity(Intent.createChooser(intent, "Choose an app: "));

    }
}
