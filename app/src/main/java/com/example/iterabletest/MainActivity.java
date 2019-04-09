package com.example.iterabletest;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iterable.iterableapi.IterableApi;
import com.iterable.iterableapi.IterableConfig;
import com.iterable.iterableapi.IterableHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements IterableHelper.IterableActionHandler {

    private Button updateUser;
    private Button customerEvent;

    private EditText fName;
//    EditText email;

    private String firstname;

    private static final String INTEGRATION_NAME = "TestPushIntegration";
    private static final String API_KEY = "349dcc9373c74c6699c5d1204a271695";
    private static final String TAG = MainActivity.class.getName();

    String USER_EMAIL = "jshsaylee@gmail.com";
    String  SECRET_CODE = "Code_123";
    String platform = "Android";
    String url = "https://iterable.com/sa-test/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize
        IterableConfig config = new IterableConfig.Builder().setPushIntegrationName(INTEGRATION_NAME).build();
        IterableApi.initialize(MainActivity.this, API_KEY, config);

        // set email as an identifier
        IterableApi.getInstance().setEmail(USER_EMAIL);


        fName = (EditText) findViewById(R.id.firstname);
//        email = (EditText) findViewById(R.id.email);

        updateUser = (Button) findViewById(R.id.update);
        customerEvent = (Button) findViewById(R.id.custom_event);

        updateUser.setOnClickListener(updateListener);
        customerEvent.setOnClickListener(customerEventListener);

        // trying to see inapp messages in log
//        IterableApi.getInstance().getInAppMessages(1, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayInAppMessages();
    }

    View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            firstname = fName.getText().toString();

            JSONObject user = new JSONObject();
            JSONObject dataField = new JSONObject();

            if(firstname == null || firstname.isEmpty()){
                notifyUser(MainActivity.this, "Please enter your name to update profile");
            }else{
                try {
                    user.put("email", USER_EMAIL);

                    dataField.put("firstName", firstname);
                    dataField.put("isRegisteredUser", true);
                    dataField.put("SA_User_Test_Key", "completed");

                    user.put("dataFields", dataField);
                    // update user profile
                    IterableApi.getInstance().updateUser(user);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    View.OnClickListener customerEventListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(firstname != null){
                if(!firstname.isEmpty()) {
                    sendCustomEvent(firstname);

                    // in app messages
                    displayInAppMessages();
                }else
                    notifyUser(MainActivity.this, "Please enter your name to send custom event");
            }else{
                notifyUser(MainActivity.this, "Please enter your name to send custom event");
            }
        }
    };

    public void sendCustomEvent(String name){
        JSONObject customObj = new JSONObject();

        try {
            customObj.put("platform", platform);
            customObj.put("isTestEvent", true);
            customObj.put("url", url+name);
            customObj.put("secret_code_key", SECRET_CODE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Event json: "+customObj);

        // track custom event
        IterableApi.getInstance().track("mobileSATestEvent", customObj);
    }

    public void notifyUser(Context c, String message){
        Toast.makeText(c, message, Toast.LENGTH_LONG).show();
    }

    public void displayInAppMessages(){
        IterableApi.getInstance().spawnInAppNotification(MainActivity.this, this);
    }
    @Override
    public void execute(String data) {
        // print in app messages
        Log.d(TAG, data);
    }

}



