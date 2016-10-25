package com.kinvey.sample.micflow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyMICCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;

public class MainActivity extends ActionBarActivity {

    private Client kinveyClient;
    private static final String APP_KEY = "kid_Wy7NMiwaTx";
    private static final String APP_SECRET = "18e581bc9c7046a5b1b20ae838105126";
    private static final String redirectURI = "training://";

    private static final String USERNAME = "custom";
    private static final String PASSWORD = "1234";



    private TextView loginStatus;
    private TextView errorView;
    private Button loginWithPage;
    private Button loginAutomated;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create the Kinvey Client with an AppKey and AppSecret
        kinveyClient = new Client.Builder(APP_KEY, APP_SECRET, this).build();

        bindViews();

        updateStatus();

    }

    protected void onResume(){
        super.onResume();
        updateStatus();
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        kinveyClient.user().onOAuthCallbackRecieved(intent);
    }

    private void bindViews(){
        loginStatus = (TextView) findViewById(R.id.login_status);
        loginWithPage = (Button) findViewById(R.id.loginpage);
        loginAutomated = (Button) findViewById(R.id.automated);
        logout = (Button) findViewById(R.id.logout);
        errorView = (TextView) findViewById(R.id.login_errors);

        loginWithPage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loginWithLoginPage();

            }
        });

        loginAutomated.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loginAutomated();

            }
        });

        logout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                logout();

            }
        });
    }


    private void loginWithLoginPage(){
        loading();
        kinveyClient.user().loginWithAuthorizationCodeLoginPage(redirectURI, new KinveyMICCallback() {

            @Override
            public void onSuccess(User user) {
                updateStatus();
            }

            @Override
            public void onFailure(Throwable error) {
                errorView.setText(error.getMessage());
            }

            @Override
            public void onReadyToRender(String myURLToRender) {
                //This renders the login page with the device's default browser
                Uri uri = Uri.parse(myURLToRender);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void loginAutomated(){
        loading();
        kinveyClient.user().setMICApiVersion("v1");
        kinveyClient.user().loginWithAuthorizationCodeAPI(USERNAME, PASSWORD, redirectURI, new KinveyUserCallback() {

            @Override
            public void onSuccess(User user) {
                updateStatus();
            }

            @Override
            public void onFailure(Throwable error) {
                errorView.setText(error.getMessage());
            }
        });

    }

    private void logout(){
        loading();
        try{
            kinveyClient.user().logout().execute();
        }catch(Exception e){
            errorView.setText(e.getMessage());
        }
        updateStatus();
    }

    private void loading(){
        loginStatus.setText("loading...");
    }

    private void updateStatus(){
        if (kinveyClient.user().isUserLoggedIn()){
            loginStatus.setText("User is logged in!");
        }else{
            loginStatus.setText("Not logged in yet!");
        }
        errorView.setText("No Errors!");
    }
}
