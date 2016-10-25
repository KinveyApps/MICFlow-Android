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
    private static final String APP_KEY = "kid_ZyrG_IFcGe";
    private static final String APP_SECRET = "f554f49974d14962bf72814268c2a7ba";
    private static final String redirectURI = "kinveyAuthDemo://";

    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";



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
//        kinveyClient.user().presentMICLoginActivity(redirectURI, new KinveyUserCallback(){
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
                //Time to render the login page for the user!
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

//                AsyncAppData<HeaderEntity> myEvents = kinveyClient.appData("headers", HeaderEntity.class);
//                myEvents.get(new KinveyListCallback<HeaderEntity>() {
//                    @Override
//                    public void onSuccess(HeaderEntity[] result) {
//                        Log.d("INFO", result[0].toString());
//                        loginStatus.setText("received " + result[0].toString());
//                    }
//
//                    @Override
//                    public void onFailure(Throwable error) {
//                        loginStatus.setText("failed to fetchByFilterCriteria" + error);
//                    }
//                });

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
