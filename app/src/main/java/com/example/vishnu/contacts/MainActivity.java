package com.example.vishnu.contacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
    ListView listView;
    DataSource dataSource;


    private MenuItem item;
    private boolean isSearchOpen = false;
    private EditText editSearch;
    private String editPhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView);
        dataSource = new DataSource(this);

        dataSource.open();

        if(dataSource.isEmpty())
        {
            Contact JohnDoe = new Contact("John Doe", "123456789", "johndoe@example.com",
                    "#221 B Baker Street", "01/01/1970", "Acquiantance");
            dataSource.create(JohnDoe);
        }

        display();

        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phoneNo = dataSource.findAll().get(position).getPhoneNo();

                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(PHONE_NO, phoneNo);

                startActivity(intent);

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId()==R.id.listView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            String menuItems[] = getResources().getStringArray(R.array.modify);

            editPhoneNo = dataSource.findAll().get(info.position).getPhoneNo();

            for(int i=0; i<menuItems.length; ++i)
                menu.add(Menu.NONE,i,i,menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                                                    item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        switch (menuItemIndex){
            case 0: Log.d(LOG_TAG, "Select 0");
                Intent intent = new Intent(this, Main2Activity.class);
                intent.putExtra(UPDATE, true);
                intent.putExtra(PHONE_NO, editPhoneNo);
                startActivityForResult(intent, REQUEST_CODE_UPDATE);
                break;
            case 1: Log.d(LOG_TAG, "Select 1");
                dataSource.deleteContact(editPhoneNo);
                display();
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


        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {

            final ActionBar actionBar = getSupportActionBar();

            if(isSearchOpen){


                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow(editSearch.getWindowToken(),
                        InputMethodManager.HIDE_IMPLICIT_ONLY);

                item.setIcon(R.mipmap.ic_search);

                actionBar.setDisplayShowCustomEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);



                isSearchOpen = false;

            }

            else{

                actionBar.setDisplayShowCustomEnabled(true);
                actionBar.setCustomView(R.layout.search_bar);
                actionBar.setDisplayShowTitleEnabled(false);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                        this.INPUT_METHOD_SERVICE
                );
                inputMethodManager.showSoftInput(editSearch, InputMethodManager.SHOW_IMPLICIT);

                item.setIcon(R.mipmap.ic_close);
                editSearch = (EditText) actionBar.getCustomView().findViewById(R.id.edtSearch);
                editSearch.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);


                editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        if(actionId == EditorInfo.IME_ACTION_SEARCH){

                            Log.d(LOG_TAG, "Searching");
                            String query = editSearch.getText().toString();

                            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                            intent.putExtra(QUERY, query);

//                            InputMethodManager imm = (InputMethodManager)getSystemService(
//                                    INPUT_METHOD_SERVICE
//                            );
//
//                            imm.hideSoftInputFromWindow(editSearch.getWindowToken(),
//                                    InputMethodManager.HIDE_IMPLICIT_ONLY);
//
//                            isSearchOpen = false;

                            Log.d(LOG_TAG, "" + isSearchOpen);

                            startActivity(intent);

                            return true;
                        }

                        return false;
                    }
                });

                editSearch.requestFocus();

                isSearchOpen = true;


            }
        }

        else if (id == R.id.add)
        {
            add();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.dbHelper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
    }


    public void add(){
        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== REQUEST_CODE_ADD && resultCode == RESULT_OK)
        {
            long id = data.getLongExtra(Main2Activity.RESULT, -1);

            if(id==-1 || id==0)
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            else;
                //Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

            display();
        }
    }

}
