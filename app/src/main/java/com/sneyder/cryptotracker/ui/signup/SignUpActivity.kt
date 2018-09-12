/*
 * Copyright (C) 2018 Sneyder Angulo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sneyder.cryptotracker.ui.signup

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
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
import com.google.firebase.iid.FirebaseInstanceId
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.data.model.TypeLogin
import com.sneyder.cryptotracker.data.model.UserRequest
import com.sneyder.cryptotracker.ui.base.DaggerActivity
import com.sneyder.cryptotracker.ui.main.MainActivity
import com.sneyder.utils.dialogs.ProgressDialog
import com.sneyder.utils.ifError
import com.sneyder.utils.ifLoading
import com.sneyder.utils.ifSuccess
import debug
import error
import generateUUID
import isValidEmail
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONException
import toast

class SignUpActivity : DaggerActivity() {

    companion object {

        fun starterIntent(context: Context): Intent {
            return Intent(context, SignUpActivity::class.java)
        }

        const val RC_SIGN_IN = 1

    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val signUpViewModel by lazy { getViewModel<SignUpViewModel>() }
    private val callbackManager by lazy { CallbackManager.Factory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // allow link in the privacy policy to be clickable
        policyTextView.movementMethod = LinkMovementMethod.getInstance()

        setUpGoogleLogin()
        setUpFacebookLogin()

        signUpGoogleButton.setOnClickListener { startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN) }
        signUpEmailButton.setOnClickListener { signUpWithEmail() }
        signUpFacebookButton.setOnClickListener { LoginManager.getInstance().logInWithReadPermissions(this, listOf("email")) }
        observeSigningUpResult()
    }

    private fun observeSigningUpResult() {
        signUpViewModel.user.observe(this, Observer {user ->
            user.ifLoading {
                signUpGoogleButton.isEnabled = false
                signUpEmailButton.isEnabled = false
                signUpFacebookButton.isEnabled = false
                showLoadingDialog()
            }
            user.ifSuccess {
                hideLoadingDialog()
                openMainActivity()
            }
            user.ifError { it ->
                hideLoadingDialog()
                it?.let{ toast(it) }
                signUpEmailButton.isEnabled = true
                signUpFacebookButton.isEnabled = true
                signUpGoogleButton.isEnabled = true
            }
        })
    }

    private var loadingDialog: ProgressDialog? = null

    private fun showLoadingDialog() {
        if(loadingDialog==null){
            loadingDialog = com.sneyder.utils.dialogs.ProgressDialog.newInstance(getString(R.string.sign_up_dialog_loading_title), getString(R.string.sign_up_dialog_loading_message)).apply {
                isCancelable = false
            }
            loadingDialog?.show(supportFragmentManager, "")
        }
    }

    private fun hideLoadingDialog(){
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun signUpWithEmail() {
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        if (username.isBlank()) {
            usernameEditText.error = getString(R.string.sign_up_warning_missing_username)
            return
        }
        if (email.isBlank() || !email.isValidEmail()) {
            emailEditText.error = getString(R.string.sign_up_warning_missing_email)
            return
        }
        if (password.isBlank()) {
            passwordEditText.error = getString(R.string.sign_up_warning_missing_password)
            return
        }
        signUpViewModel.signUp(UserRequest(userId = generateUUID(), password = password, email = email, accessToken = "",
                firebaseTokenId = FirebaseInstanceId.getInstance().token ?: "", displayName = username,
                typeLogin = TypeLogin.EMAIL.data))
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
                val name = jsonObject.getString("groupName")
                val email = jsonObject.getString("email")
                val id = jsonObject.getString("id")
                debug("onSuccess FacebookCallback token=${currentAccessToken.token}")
                debug("newMeRequest groupName=$name email=$email id=$id")
                LoginManager.getInstance().logOut() // Signed in successfully, logOut and open RegisterActivity.

                signUpViewModel.signUp(UserRequest(userId = id ?: "", password = "", email = email ?: "",
                        accessToken = currentAccessToken.token ?: "",
                        firebaseTokenId = FirebaseInstanceId.getInstance().token ?: "", displayName = name ?: "",
                        typeLogin = TypeLogin.FACEBOOK.data))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,groupName,email")
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
        if (requestCode == RC_SIGN_IN) {
            val result = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(result)
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleGoogleSignInResult(result: Task<GoogleSignInAccount>?) {
        try {
            val account: GoogleSignInAccount = result!!.getResult(ApiException::class.java)
            debug("handleGoogleSignInResult idToken = ${account.idToken} userId = ${account.id}")
            googleSignInClient.signOut() // Signed in successfully, logOut and open RegisterActivity.
            signUpViewModel.signUp(UserRequest(userId = account.id ?: "", password = "", email = account.email ?: "",
                    accessToken = account.idToken ?: "", firebaseTokenId = FirebaseInstanceId.getInstance().token ?: "",
                    displayName = account.displayName ?: "", typeLogin = TypeLogin.GOOGLE.data))
        } catch (e: ApiException) {
            // The ApiException syncStatus code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            toast(R.string.signup_message_unable_google_login)
            error("error handleGoogleSignInResult code = ${e.statusCode}")
            e.printStackTrace()
        } catch (e: Exception) {
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
            android.R.id.home -> {
                openMainActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openMainActivity() {
        startActivity(MainActivity.starterIntent(this))
        finish()
    }
}
