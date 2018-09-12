package com.sneyder.cryptotracker.ui.login

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.data.model.TypeLogin
import com.sneyder.cryptotracker.ui.base.DaggerActivity
import com.sneyder.cryptotracker.ui.main.MainActivity
import com.sneyder.cryptotracker.ui.signup.SignUpActivity
import com.sneyder.utils.dialogs.ProgressDialog
import com.sneyder.utils.ifError
import com.sneyder.utils.ifLoading
import com.sneyder.utils.ifSuccess
import debug
import error
import isValidEmail
import kotlinx.android.synthetic.main.activity_log_in.*
import org.json.JSONException
import toast

class LogInActivity : DaggerActivity() {

    companion object {

        fun starterIntent(context: Context): Intent {
            return Intent(context, LogInActivity::class.java)
        }

        const val RC_LOG_IN = 1

    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val logInViewModel by lazy { getViewModel<LogInViewModel>() }
    private val callbackManager by lazy { CallbackManager.Factory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpGoogleLogin()
        setUpFacebookLogin()
        logInEmailButton.setOnClickListener { logInWithEmail() }
        logInGoogleButton.setOnClickListener { startActivityForResult(googleSignInClient.signInIntent, RC_LOG_IN) }
        logInFacebookButton.setOnClickListener { LoginManager.getInstance().logInWithReadPermissions(this, listOf("email")) }
        signUpButton.setOnClickListener {
            startActivity(SignUpActivity.starterIntent(this))
            finish()
        }
        observeLoggingResult()
    }

    private fun observeLoggingResult() {
        logInViewModel.user.observe(this, Observer {user ->
            user.ifLoading {
                logInGoogleButton.isEnabled = false
                logInEmailButton.isEnabled = false
                logInFacebookButton.isEnabled = false
                showLoadingDialog()
            }
            user.ifSuccess {
                hideLoadingDialog()
                openMainActivity()
            }
            user.ifError { it ->
                hideLoadingDialog()
                it?.let{ toast(it) }
                logInEmailButton.isEnabled = true
                logInFacebookButton.isEnabled = true
                logInGoogleButton.isEnabled = true
            }
        })
    }

    private var loadingDialog: ProgressDialog? = null

    private fun showLoadingDialog() {
        debug("showLoadingDialog $loadingDialog")
        if(loadingDialog==null){
            loadingDialog = com.sneyder.utils.dialogs.ProgressDialog.newInstance(getString(R.string.log_in_dialog_loading_title), getString(R.string.log_in_dialog_loading_message)).apply {
                isCancelable = false
            }
            loadingDialog?.show(supportFragmentManager, "")
        }
    }

    private fun hideLoadingDialog(){
        debug("hideLoadingDialog $loadingDialog")
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun logInWithEmail(): Boolean {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        if (email.isBlank() || !email.isValidEmail()) {
            emailEditText.error = getString(R.string.login_warning_invalid_email)
            return true
        }
        if (password.isBlank()) {
            passwordEditText.error = getString(R.string.login_warning_missing_password)
            return true
        }
        logInViewModel.logInUser(email, password, TypeLogin.EMAIL.data)
        return false
    }

    private fun setUpFacebookLogin() {
        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookSignInResult(loginResult.accessToken)
            }
            override fun onCancel() {
                debug("onCancel FacebookCallback")
            }
            override fun onError(exception: FacebookException) {
                error("onError FacebookCallback ${exception.message}")
            }
        })
    }

    private fun handleFacebookSignInResult(currentAccessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(currentAccessToken) { jsonObject, _ ->
            try {
                val email = jsonObject.getString("email")
                val id = jsonObject.getString("id")
                debug("onSuccess FacebookCallback token=${currentAccessToken.token}")
                debug("newMeRequest email=$email id=$id")
                LoginManager.getInstance().logOut() // Logged in successfully, logOut and open RegisterActivity.
                logInViewModel.logInUser(email = email, typeLogin =  TypeLogin.FACEBOOK.data, accessToken = currentAccessToken.token, userId = id, password = "")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,email")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun setUpGoogleLogin() {
        // Configure sign-in to request the myUserInfo's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_LOG_IN) {
            val result = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(result)
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleGoogleSignInResult(result: Task<GoogleSignInAccount>?) {
        try {
            val account: GoogleSignInAccount = result!!.getResult(ApiException::class.java)
            val idToken = account.idToken!!
            debug("handleGoogleSignInResult idToken = $idToken userId = ${account.id}")
            logInViewModel.logInUser(email = account.email!!, password = "", typeLogin = TypeLogin.GOOGLE.data, accessToken = idToken, userId = account.id!!)
            googleSignInClient.signOut() // logged  in successfully, logOut and open RegisterActivity.
        } catch (e: ApiException) {
            // The ApiException syncStatus code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            toast(R.string.signup_message_unable_google_login)
            error("error handleGoogleSignInResult code = ${e.statusCode}")
            e.printStackTrace()
        } catch (e: Exception){
            toast(R.string.signup_message_unable_google_login)
            error("error handleGoogleSignInResult e = ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            openMainActivity()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> openMainActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * start MainActivity and finish this one
     */
    private fun openMainActivity() {
        startActivity(MainActivity.starterIntent(this))
        finish()
    }
}

