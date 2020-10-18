package edu.android.thinkr

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.android.thinkr.models.User
import edu.android.thinkr.utils.Resource
import edu.android.thinkr.utils.Validation.isValidConfirmPassword
import edu.android.thinkr.utils.Validation.isValidEmail
import edu.android.thinkr.utils.Validation.isValidPassword
import edu.android.thinkr.utils.Validation.isValidPhoneNo
import edu.android.thinkr.utils.Validation.isValidUsername
import edu.android.thinkr.utils.showToast
import edu.android.thinkr.utils.takeWords
import edu.android.thinkr.viewModel.AppViewModel
import kotlinx.android.synthetic.main.activity_signup.*


class SignupActivity : AppCompatActivity() {
    private val TAG = "SignupActivity"
    private lateinit var email: TextInputEditText
    private lateinit var username: TextInputEditText
    private lateinit var phone: TextInputEditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signUpButton : Button
    private lateinit var signupProgress : ProgressBar
    private val auth = Firebase.auth



    private val viewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory(application).create(AppViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }
        setStatusBarWhite(this@SignupActivity)


        email = findViewById(R.id.tgt_email)
        username = findViewById(R.id.tgt_username)
        phone = findViewById(R.id.tgt_phone)
        password = findViewById(R.id.tgt_password)
        confirmPassword = findViewById(R.id.tgt_confirm_password)
        signUpButton = findViewById(R.id.button_signin)
        signupProgress = findViewById(R.id.signup_progress)
    }

    private fun setStatusBarWhite(activity: AppCompatActivity) {
        //Make status bar icons color dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.statusBarColor = Color.WHITE
        }
    }

    fun signUp(view: View) {
        if (!validateFields()) return
        viewModel.registerUser(email.takeWords(), password.takeWords()).observe(
            this, Observer<Resource<String>> {
                when(it){
                    is Resource.Loading -> showProgress()
                    is Resource.Success -> {
                        val user = User(auth.uid!!, username.takeWords(), email.takeWords(), phone.takeWords())
                        Log.e(TAG, user.toString())
                        addUserToDatabase(user)
                    }
                    is Resource.Failure ->{
                        showToast(it.message)
                        hideProgress()
                    }
                }
            }
        )
    }

    private fun addUserToDatabase(user: User) {
        viewModel.putUser(user).observe(this, Observer {
            when(it){
                is Resource.Loading -> showProgress()
                is Resource.Success ->{
                    hideProgress()
                    showToast(it.data)
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                is Resource.Failure ->{
                    showToast(it.message)
                    hideProgress()
                }
            }
        })
    }

    private fun validateFields(): Boolean {
        if (!isValidEmail(email.takeWords())) {
            signup_email_text_input_layout.error = "Enter valid Email"
            return false

        } else {
            signup_email_text_input_layout.error = null
        }

        if (!isValidUsername(username.takeWords())) {
            signup_username_text_input_layout.error = "Enter username"
            return false

        } else {
            signup_username_text_input_layout.error = null
        }

        if (!isValidPhoneNo(phone.takeWords())) {
            signup_phone_number_text_input_layout.error = "Enter valid phone number"
            return false

        } else {
            signup_phone_number_text_input_layout.error = null
        }

        if (!isValidPassword(password.takeWords())) {
            signup_password_text_input_layout.error = "Password of 6 characters & above required"
            return false

        } else {
            signup_password_text_input_layout.error = null
        }

        if (!isValidConfirmPassword(password.takeWords(), confirmPassword.takeWords())) {
            signup_confirm_password_text_input_layout.error = "Passwords do not match"
            return false

        } else {
            signup_confirm_password_text_input_layout.error = null
        }
        return true
    }

    private fun showProgress() {
        signUpButton.isEnabled = false
        signupProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        signUpButton.isEnabled = true
        signupProgress.visibility = View.INVISIBLE
    }
}

