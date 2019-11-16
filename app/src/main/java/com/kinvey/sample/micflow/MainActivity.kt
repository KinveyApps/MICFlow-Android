package com.kinvey.sample.micflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.api.client.http.HttpTransport
import com.kinvey.android.Client
import com.kinvey.android.Client.Builder
import com.kinvey.android.callback.KinveyUserCallback
import com.kinvey.android.model.User
import com.kinvey.android.store.UserStore
import com.kinvey.java.AbstractClient
import com.kinvey.java.core.KinveyClientCallback
import com.kinvey.java.dto.BaseUser
import com.kinvey.sample.micflow.Constants.APP_CLIENT_ID
import com.kinvey.sample.micflow.Constants.PASSWORD
import com.kinvey.sample.micflow.Constants.REDIRECT_URI
import com.kinvey.sample.micflow.Constants.USERNAME
import com.kinvey.sample.micflow.R.layout
import kotlinx.android.synthetic.main.activity_main.*
import java.util.logging.Level
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    private var kinveyClient: Client<User>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        Logger.getLogger(HttpTransport::class.java.name).level = Level.ALL
        kinveyClient = Builder<User>(this).build()
        bindViews()
        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun bindViews() {
        loginPageBtn?.setOnClickListener { loginWithLoginPage() }
        automatedBtn?.setOnClickListener { loginAutomated() }
        logoutBtn?.setOnClickListener { logout() }
    }

    private fun loginWithLoginPage() {
        loading()
        UserStore.presentMICLoginActivity(kinveyClient as Client<*>, APP_CLIENT_ID, REDIRECT_URI,
        object : KinveyUserCallback<User> {
             override fun onSuccess(result: User) {
                 updateStatus()
             }
             override fun onFailure(error: Throwable) {
                 loginErrorsText?.text = error.message
             }
        })
    }

    private fun loginAutomated() {
        loading()
        UserStore.loginWithMIC(kinveyClient as AbstractClient<User>, USERNAME, PASSWORD, APP_CLIENT_ID,
        object: KinveyUserCallback<User> {
             override fun onSuccess(result: User) {
                 updateStatus()
             }
             override fun onFailure(error: Throwable) {
                 loginErrorsText?.text = error.message
             }
        })
    }

    private fun logout() {
        loading()
        UserStore.logout(kinveyClient as AbstractClient<BaseUser>,
            object : KinveyClientCallback<Void?> {
                override fun onSuccess(result: Void?) {
                    updateStatus()
                }
                override fun onFailure(error: Throwable) {
                    loginErrorsText?.text = error.message
                }
            })
        updateStatus()
    }

    private fun loading() {
        loginStatusText?.text = "loading..."
    }

    private fun updateStatus() {
        if (kinveyClient?.clientUser != null) {
            loginStatusText?.text = "User is logged in!"
        } else {
            loginStatusText?.text = "Not logged in yet!"
        }
        loginErrorsText?.text = "No Errors!"
    }
}