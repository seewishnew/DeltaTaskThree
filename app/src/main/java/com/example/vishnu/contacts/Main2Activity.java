package com.example.vishnu.contacts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    public String rel;
    private boolean update = false;
    private DataSource dataSource;


    public EditText name;
    public EditText phone;
    public EditText email;
    public EditText address;
    public EditText birthday;
    public Spinner relationship;
    public Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        update = getIntent().getBooleanExtra(MainActivity.UPDATE, false);

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
            Contact contact = dataSource.findSpecific(
                    getIntent().getStringExtra(MainActivity.PHONE_NO)
            );

            button = (Button) findViewById(R.id.next);
            button.setText("Update");

            name.setText(contact.getName());
            phone.setText(contact.getPhoneNo());
            email.setText(contact.getEmailID());
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

                rel=contact.getRelationship();
            }

            dataSource.close();
        }

    }

    public boolean isFieldEmpty(EditText editText){
        return (editText.getText().toString().length()==0) ? (true) : (false);
    }

    public void next(View view) {

        Log.d(LOG_TAG, "next " + update);

        boolean flag = true;
        Contact contact = new Contact();
        DataSource dataSource = new DataSource(this);
        dataSource.open();


        if (isFieldEmpty(name)) {
            name.setError("Enter a name");
            flag = false;
        } else
            contact.setName(name.getText().toString());

        Log.d(LOG_TAG, "" + update);


        if (isFieldEmpty(phone)) {
            if (isFieldEmpty(email)) {
                phone.setError("Enter phone number");
                flag = false;
            }

            else {
                contact.setEmailID(email.getText().toString());
            }
        }

        else {

            if (!update) {
                if (dataSource.isNumberUnique(phone.getText().toString())) {
                    contact.setPhoneNo(phone.getText().toString());

                    if (!isFieldEmpty(email))
                        contact.setEmailID(email.getText().toString());

                } else {
                    phone.setError("This number already exists");
                    flag = false;
                }


            }

            else{
                contact.setPhoneNo(phone.getText().toString());

                if(!isFieldEmpty(email))
                    contact.setEmailID(email.getText().toString());

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
                    id = dataSource.create(contact);
                } else {
                    id = dataSource.update(contact);
                }

                Intent intent = new Intent();
                intent.putExtra(RESULT, id);

                setResult(RESULT_OK, intent);

                finish();

            } else {
                phone.requestFocus();
                name.requestFocus();
            }


        }
    }

    public void addField(View view) {
        Intent intent = new Intent(this, AddField.class);

        startActivityForResult(intent, REQUEST_CODE);
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