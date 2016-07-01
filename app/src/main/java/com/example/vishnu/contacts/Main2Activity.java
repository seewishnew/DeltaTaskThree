package com.example.vishnu.contacts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1;
    public static final String RESULT = "RESULT";
    public static final String LOG_TAG = "Main2Activity";

    /*This holds the value of relationship*/
    public String rel;

    public Contact contact;


    /*Whether the activity needs to update an
    * existing element or create from scratch*/
    private boolean update = false;

    private DataSource dataSource;

    private static final String blockedCharacterSet = "-(*+.,)#";
//    private InputFilter filter = new InputFilter() {
//        @Override
//        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//
//            if(source!=null && blockedCharacterSet.contains(("" + source)))
//                return "";
//
//            return null;
//        }
//    };
//

    public EditText name;
    public EditText phone;
    public EditText email;
    public EditText address;
    public EditText birthday;
    public Spinner relationship;

    /*Button handle to change text to Update if
    * update is true*/
    public Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Find out whether this is an update or a create
        update = getIntent().getBooleanExtra(MainActivity.UPDATE, false);

        //Assign all the editText handlers to the respective views
        {

            name = (EditText) findViewById(R.id.name);


            phone = (EditText) findViewById(R.id.phoneNo);


            email = (EditText) findViewById(R.id.email);


            address = (EditText) findViewById(R.id.address);


            birthday = (EditText) findViewById(R.id.birthday);
    }

        if(update){
            dataSource = new DataSource(this);
            dataSource.open();

            final Contact contact = dataSource.findSpecific(
                    getIntent().getLongExtra(MainActivity.ID, 0)
            );

            //Log.d("DataSource", "after intent id:" + contact.getId());

            //set button text to update.
            button = (Button) findViewById(R.id.next);
            button.setText("Update");

            /*Fill in the existing values in the editText fields*/
            name.setText(contact.getName());
            phone.setText(contact.getPhoneNo());
            email.setText(contact.getEmailID());

            //Check whether these fields have been initialized before
            //displaying them.
            if(contact.hasAddress()){
                address.setVisibility(View.VISIBLE);
                address.setText(contact.getAddress());
            }
            if(contact.hasBirthday()){
                birthday.setVisibility(View.VISIBLE);
                birthday.setText(contact.getBirthday());
            }

            if(contact.hasRelationship()){
                relationship = (Spinner) findViewById(R.id.spinner);
                relationship.setVisibility(View.VISIBLE);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        this, R.array.relationship, android.R.layout.simple_spinner_item);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                relationship.setAdapter(adapter);

                //Set the spinner to display whatever the stored value of relationship is
                switch (contact.getRelationship()){
                    case "Family":
                        relationship.setSelection(1);
                        break;

                    case "Friends":
                        relationship.setSelection(2);
                        break;

                    case "Co-worker":
                        relationship.setSelection(3);
                        break;

                    case "Classmate":
                        relationship.setSelection(4);
                        break;

                    case "Boss":
                        relationship.setSelection(5);
                        break;

                    default: relationship.setSelection(0);
                        break;

                }

                //update rel on the relationship status,
                // because otherwise,
                // it will cause null problem errors
                //as the final button is the same
                rel=contact.getRelationship();

                relationship.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        switch (position) {
                            case 0:
                                rel = null;
                                break;
                            case 1:
                                rel = "Family";
                                break;
                            case 2:
                                rel = "Friends";
                                break;
                            case 3:
                                rel = "Co-worker";
                                break;
                            case 4:
                                rel = "Classmate";
                                break;
                            case 5:
                                rel = "Boss";
                                break;
                            default:
                                rel = null;
                                break;

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                        rel = null;

                    }

                });
            }

            dataSource.close();

            this.contact = contact;
        }

    }

    //Helper function to see if an edit text has any values entered
    public boolean isFieldEmpty(EditText editText){
        return (editText.getText().toString().length()==0) ? (true) : (false);
    }

    public void next(View view) {


        //for setting error in case fields like name and phone no are null
        boolean flag = true;

        if(!update)
            contact = new Contact();

        DataSource dataSource = new DataSource(this);
        dataSource.open();


        if (isFieldEmpty(name)) {
            name.setError("Enter a name");
            flag = false;
        }

        else
            contact.setName(name.getText().toString());

        if (isFieldEmpty(phone)) {
            if (isFieldEmpty(email)) {
                phone.setError("Enter phone number");
                flag = false;
            }

            else {
                //These scenarios are where phone number gets set to null
                //So that causes issues with its being primary key
                //Which means that another identifier is needed as primary key
               // Log.d(LOG_TAG, "running checkPhoneNo");
                if(checkPhoneNo(phone.getText().toString())) {
                    contact.setPhoneNo(phone.getText().toString());
                    contact.setEmailID(email.getText().toString());
                }

                else{
                    phone.setError("Illegal character input");
                    flag=false;
                }

            }
        }

        else {

           // Log.d(LOG_TAG, "running checkPhoneNo");
            if(checkPhoneNo(phone.getText().toString())) {

                //If the contact needs to be created...
                if (!update) {
                /*Although number is no longer the primary key, it still needs to be unique*/
                   // Log.d(LOG_TAG, "isNumberUnique running");
                    if (dataSource.isNumberUnique(phone.getText().toString())) {
                        contact.setPhoneNo(phone.getText().toString());

                        if (!isFieldEmpty(email))
                            contact.setEmailID(email.getText().toString());

                    } else {
                        phone.setError("This number already exists");
                        flag = false;
                    }

                }

                //Otherwise the contact needs to be updated.
                else {
                    if (dataSource.isNumberUniqueUpdate(phone.getText().toString())) {
                        contact.setPhoneNo(phone.getText().toString());

                        if (!isFieldEmpty(email)) {
                            contact.setEmailID(email.getText().toString());
                        }
                    } else {
                        phone.setError("Some other contact has same number");
                        flag = false;
                    }

                }
            }

            else{
                phone.setError("Illegal character input");
                flag=false;
            }
        }


        if (flag) {

                if (address.getVisibility() == View.VISIBLE) {
                    if (!isFieldEmpty(address))
                        contact.setAddress(address.getText().toString());
                }

                if (birthday.getVisibility() == View.VISIBLE)
                    if (!isFieldEmpty(birthday))
                        contact.setBirthday(birthday.getText().toString());

                contact.setRelationship(rel);


                long id = 0L;

                if (!update) {
                    contact = dataSource.create(contact);
                } else {
                    dataSource.update(contact);
                    id = contact.getId();
                }

                Intent intent = new Intent();
                intent.putExtra(RESULT, id);

//            Log.d(DetailsActivity.LOG_TAG, "" + contact.getRelationship());

                setResult(RESULT_OK, intent);

                dataSource.close();

                finish();

        }

        else {
                phone.requestFocus();
                name.requestFocus();
            }

    }


    public void addField(View view) {
        Intent intent = new Intent(this, AddField.class);

        startActivityForResult(intent, REQUEST_CODE);
    }

    public boolean checkPhoneNo(String No){

        boolean flag=true;

        for(char ch:blockedCharacterSet.toCharArray()){
            if(No.contains(""+ch))
            {
                //Log.d(LOG_TAG, "Number contains: " + ch);
                flag=false;
                return flag;
            }

        }

        return flag;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int pos = data.getIntExtra(AddField.CHOICE, -1);

                if (pos == -1)
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                else {
                    switch (pos) {
                        case 0:
                            EditText address = (EditText) findViewById(R.id.address);
                            address.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            EditText birthday = (EditText) findViewById(R.id.birthday);
                            birthday.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            relationship = (Spinner) findViewById(R.id.spinner);
                            relationship.setVisibility(View.VISIBLE);
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                    this, R.array.relationship, android.R.layout.simple_spinner_item);

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            relationship.setAdapter(adapter);
                            relationship.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    switch (position) {
                                        case 0:
                                            rel = null;
                                            break;
                                        case 1:
                                            rel = "Family";
                                            break;
                                        case 2:
                                            rel = "Friends";
                                            break;
                                        case 3:
                                            rel = "Co-worker";
                                            break;
                                        case 4:
                                            rel = "Classmate";
                                            break;
                                        case 5:
                                            rel = "Boss";
                                            break;
                                        default:
                                            rel = null;
                                            break;

                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                    rel = null;

                                }

                            });

                        default:
                            break;
                    }

                }
            }
        }
    }
}