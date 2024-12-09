package com.capstone.education.edubright.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.capstone.education.edubright.R
import com.capstone.education.edubright.data.pref.UserModel
import com.capstone.education.edubright.data.pref.Result
import com.capstone.education.edubright.data.response.LoginRequest
import com.capstone.education.edubright.data.response.LoginResult
import com.capstone.education.edubright.databinding.ActivityLoginBinding
import com.capstone.education.edubright.view.ViewModelFactory
import com.capstone.education.edubright.view.main.MainActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var auth: FirebaseAuth
    private val credentialManager by lazy { CredentialManager.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        binding.googleSignInButton.setOnClickListener {
            authWithGoogleUsingCredentialManager()
        }

        factory = ViewModelFactory.getInstance(this)
        playAnimation()
        setupInput()

        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        loginViewModel.loginResult.observe(this) { result ->
            setupLogin(result)
        }
    }

    private fun authWithGoogleUsingCredentialManager() {
        lifecycleScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(this@LoginActivity, request)
                handleGoogleIdCredentialResult(result)
            } catch (e: NoCredentialException) {
                authWithGoogleUsingGoogleIdWithNoFilter()
            } catch (e: GetCredentialException) {
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun authWithGoogleUsingGoogleIdWithNoFilter() {
        lifecycleScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(this@LoginActivity, request)
                handleGoogleIdCredentialResult(result)
            } catch (e: GetCredentialException) {
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleGoogleIdCredentialResult(result: GetCredentialResponse) {
        val credential = result.credential
        if (credential is GoogleIdTokenCredential) {
            firebaseAuthWithGoogle(credential.idToken)
        } else {
            Toast.makeText(this, "Credential type not supported", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    loginViewModel.saveSession(UserModel(user?.displayName ?: "", user?.getIdToken(false)?.result?.token ?: "", true))
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupInput() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val email = p0.toString()
                if (email.isNotEmpty() && isValidEmail(email)) {
                    binding.emailEditTextLayout.error = null
                } else {
                    binding.emailEditTextLayout.error = getString(R.string.invalid_email)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = getString(R.string.fill_email)
                }

                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = getString(R.string.fill_password)
                }
                else -> {
                    loginViewModel.loginUser(LoginRequest(email, password))
                }
            }
        }
    }
    private fun setupLogin(user: Result<LoginResult?>) {
        when (user) {
            is Result.Loading -> {
                showLoading(true)
            }
            is Result.Success -> {
                val userData = user.data
                loginViewModel.saveSession(
                    UserModel(userData?.name ?: "", userData?.token ?: "", true))
                AlertDialog.Builder(this@LoginActivity).apply {
                    setTitle("Horayy!")
                    setMessage(getString(R.string.success_login))
                    setPositiveButton(getString(R.string.next)) { _, _ ->
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    create()
                    show()
                }
            }
            is Result.Error -> {
                showLoading(false)
                AlertDialog.Builder(this).apply {
                    setMessage(getString(R.string.login_failed))
                    setPositiveButton(getText(R.string.try_again)) { _, _ ->
                    }
                    show()
                    create()
                }
            }
        }
    }


    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val googleSignIn = ObjectAnimator.ofFloat(binding.googleSignInButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login,
                googleSignIn
            )
            startDelay = 100
        }.start()
    }
}