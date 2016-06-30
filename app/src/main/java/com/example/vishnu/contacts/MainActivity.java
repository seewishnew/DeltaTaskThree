package com.example.vishnu.contacts;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MainActivity";
    public static final int REQUEST_CODE_ADD = 1;
    public static final String UPDATE = "Update";
    public static final int REQUEST_CODE_UPDATE = 2;
    public static final String PHONE_NO = "Phone no";
    public static final String QUERY = "Query";
    public static final String ID = "ID";
    public static final int REQUEST_CODE_DETAILS = 4;
    public static final int MY_MULTIPLE_PERMISSION_REQUEST = 6;
    public static final int MY_REQUEST_CODE_MANAGE_DOCUMENTS = 7;

    public static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static boolean profileEditable = true;

    ListView listView;
    DataSource dataSource;

    /*To store the item that gets clicked in
    * Context Menu*/
    private Contact contactMenuBuf;

    private MenuItem item;

    /*Used to toggle search box in action bar*/
    private boolean isSearchOpen = false;

    /*Used to get the search string*/
    private EditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int permissionCheck1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        //int permissionCheck2 = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.MANAGE_DOCUMENTS);

        if(!(permissionCheck1 == PackageManager.PERMISSION_GRANTED)){

            profileEditable=false;

            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    MY_MULTIPLE_PERMISSION_REQUEST);

        }

        else
            profileEditable=true;


        listView = (ListView) findViewById(R.id.listView);
        dataSource = new DataSource(this);

        dataSource.open();

        /*If table is empty, simply create a new entry*/
        if(dataSource.isEmpty())
        {
            Contact JohnDoe = new Contact( 0, "John Doe", "123456789", "johndoe@example.com",
                    "#221 B Baker Street", "01/01/1970", "Acquiantance");
            dataSource.create(JohnDoe);
        }

        /*display checks whether or not the database is empty
        and then calls refreshList regardless*/
        display();

        //For onLongClick menu to pop up,
        //to display edit and delete options
        registerForContextMenu(listView);

        /*To display the details of the contact in a separate activity:
        * DetailsActivity*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contact contact = dataSource.findAll().get(position);

                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(ID, contact.getId());

                startActivityForResult(intent, REQUEST_CODE_DETAILS);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_MULTIPLE_PERMISSION_REQUEST:

                if(grantResults.length>0
                        && grantResults[0]==PackageManager.PERMISSION_GRANTED
                        ){
                    Log.d(LOG_TAG, "Multiple permissions granted!");
                        profileEditable=true;

                }

                else{
                    Log.d(LOG_TAG, "Multiple permisions not granted");
                    profileEditable=false;
                }
                break;

//            case MY_REQUEST_CODE_MANAGE_DOCUMENTS:
//
//                Log.d(LOG_TAG, "Inside ManageDocuments Permission");
//
//                if(grantResults.length>0
//                        && grantResults[0]==PackageManager.PERMISSION_GRANTED){
//
//                    profileEditable=true;
//
//                }
//
//                else
//                {
//                    Log.d(LOG_TAG, "Permission denied");
//                    profileEditable=false;
//                }
//
//                break;
        }
    }

    /*This function is there basically to populate the menu that will pop up when an element of the
        * listView is LongClicked.*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId()==R.id.listView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            String menuItems[] = getResources().getStringArray(R.array.modify);

            //Get the contact that was LongClicked.
            contactMenuBuf = dataSource.findAll().get(info.position);

            //populate menu
            for(int i=0; i<menuItems.length; ++i)
                menu.add(Menu.NONE,i,i,menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();

        //menuItemIndex holds the index of whichever element was picked from the popup menu
        int menuItemIndex = item.getItemId();

        switch (menuItemIndex){
            case 0:
                /*In this case, Edit was selected*/
                Log.d(LOG_TAG, "Select 0");
                Intent intent = new Intent(this, Main2Activity.class);

                /*putting in a boolean value that says that the contact needs to be edited*/
                intent.putExtra(UPDATE, true);
                /*passing the id of the contact that was selected*/
                intent.putExtra(ID, contactMenuBuf.getId());

                startActivityForResult(intent, REQUEST_CODE_UPDATE);
                break;
            case 1:
                /*In this case, Delete was selected*/
                Log.d(LOG_TAG, "Select 1");
                new AlertDialog.Builder(this)
                        .setTitle("Delete " + contactMenuBuf.getName()+"?")
                        .setMessage("Are you sure you want to delete " +
                                contactMenuBuf.getName() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dataSource.deleteContact(contactMenuBuf.getId());
                                display();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;

            default: break;
        }

        return super.onContextItemSelected(item);
    }

    private void display() {
        List<Contact> contactList = dataSource.findAll();

        if(contactList.size()>0);
        else
            Toast.makeText(this, "No contacts to display", Toast.LENGTH_SHORT).show();

        refreshList(contactList);
    }

    public void refreshList(List<Contact> contacts){

        ContactArrayAdapter adapter = new ContactArrayAdapter(this, 0, contacts);

        listView.setAdapter(adapter);
    }


    /*The search button is assigned to its handle*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        item = menu.findItem(R.id.search);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //if the search button was clicked
        if (id == R.id.search) {

            //to show and hide the editText and title
            final ActionBar actionBar = getSupportActionBar();

            //if Search is already open then it needs to be closed.
            if(isSearchOpen){

                //To hide the keyboard.
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow(editSearch.getWindowToken(),
                        InputMethodManager.HIDE_IMPLICIT_ONLY);

                //set the button icon back to search
                item.setIcon(R.mipmap.ic_search);

                //hide the editText
                actionBar.setDisplayShowCustomEnabled(false);
                //show the title instead
                actionBar.setDisplayShowTitleEnabled(true);

                //finally set isSearchOpen as false;
                isSearchOpen = false;

            }

            else{
                //Display custom view is enabled now
                actionBar.setDisplayShowCustomEnabled(true);
                //set Custom view as the editText in the layout file
                actionBar.setCustomView(R.layout.search_bar);
                //Hide the title
                actionBar.setDisplayShowTitleEnabled(false);

                //pop up the keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                        this.INPUT_METHOD_SERVICE
                );
                inputMethodManager.showSoftInput(editSearch, InputMethodManager.SHOW_IMPLICIT);

                //Set the button image to close
                item.setIcon(R.mipmap.ic_close);

                //get a handle for the search bar edit Text
                editSearch = (EditText) actionBar.getCustomView().findViewById(R.id.edtSearch);
                //This basically replaces the enter sign or whatever in the keyboard with
                //"Search" or the symbol associated with "Search", which is usually a magnifying
                //glass.
                editSearch.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);

                //When the user presses search, start a new intent to display the results.
                editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        //If the action is indeed what we're looking for; searching
                        if(actionId == EditorInfo.IME_ACTION_SEARCH){

                            Log.d(LOG_TAG, "Searching");
                            //get the search string
                            String query = editSearch.getText().toString();

                            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                            //pass the search string to the next activity
                            intent.putExtra(QUERY, query);

                            startActivity(intent);

                            return true;
                        }

                        return false;
                    }
                });

                editSearch.requestFocus();

                //Finally set isSearchOpen to true
                isSearchOpen = true;


            }
        }

        //if the add option was chosen from the menu.
        else if (id == R.id.add)
        {
            add();
        }

        return super.onOptionsItemSelected(item);
    }

    //To close resources
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }

    //to open resources that were closed on exit
    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
    }

    /*Takes it to Main2Activity, where new rows are added*/
    public void add(){
        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOG_TAG, "Inside onActivityResult " + requestCode + ", " +
                (resultCode==RESULT_OK));

        if(requestCode== REQUEST_CODE_ADD && resultCode == RESULT_OK)
        {
            long id = data.getLongExtra(Main2Activity.RESULT, -1);

            if(id==-1)
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

            display();
        }

        else if(requestCode == REQUEST_CODE_UPDATE && resultCode == RESULT_OK){

            long id = data.getLongExtra(Main2Activity.RESULT, 0);

            if(id == 0)
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            else;

            display();
        }

        else if(requestCode == REQUEST_CODE_DETAILS && resultCode==RESULT_OK){
            Log.d(LOG_TAG, "I'm here");
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            display();
        }
    }

}
