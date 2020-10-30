package edu.android.thinkr

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import edu.android.thinkr.utils.Resource
import edu.android.thinkr.utils.Validation.isValidEmail
import edu.android.thinkr.utils.Validation.isValidPassword
import edu.android.thinkr.utils.showToast
import edu.android.thinkr.utils.takeWords
import edu.android.thinkr.viewModel.AppViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var username : TextInputEditText
    private lateinit var password : EditText
    private lateinit var loginProgress : ProgressBar
    private lateinit var loginButton : Button
    private lateinit var firebaseAuthStateListener : FirebaseAuth.AuthStateListener

    private val viewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory(application).create(AppViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setStatusBarTransparent(this@LoginActivity)

        username = findViewById(R.id.tgt_username)
        password = findViewById(R.id.tgt_password)
        loginProgress = findViewById(R.id.login_progress)
        loginButton = findViewById(R.id.button_signin)

        firebaseAuthStateListener = FirebaseAuth.AuthStateListener {
            val user : FirebaseUser? = it.currentUser
            if (user != null){
                Snackbar.make(findViewById(R.id.login_layout), "Logging you in now!...", Snackbar.LENGTH_LONG).show()
                val handler = Handler()
                handler.postDelayed(Runnable {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, 3000)
            }
        }
    }

    override fun onResume() {
        super.onResume()
            FirebaseAuth.getInstance().addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
            FirebaseAuth.getInstance().removeAuthStateListener(firebaseAuthStateListener)
    }

    private fun setStatusBarTransparent(activity: AppCompatActivity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.statusBarColor = Color.WHITE
        }
    }

    fun onClick(view: View) {
        if(view.id == R.id.button_signup){
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
        } else if(view.id == R.id.button_forgot_password){
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
    }

    fun initiateSignUp(view: View) {
        if (!validateFields()) return
        viewModel.loginUser(username.takeWords(), password.takeWords()).observe(this, Observer {
            when(it){
                is Resource.Loading -> showProgress()
                is Resource.Success ->{
                    hideProgress()
                    showToast(it.data)
                }
                is Resource.Failure ->{
                    showToast(it.message)
                    hideProgress()
                }
            }
        })
    }

    private fun validateFields(): Boolean {

        if (!isValidEmail(username.takeWords())){
            login_email_text_input_layout.error = "Enter valid Email"
            return false
        } else {
            login_email_text_input_layout.error = null
        }

        if (!isValidPassword(password.takeWords())){
            login_password_text_input_layout.error = "Password of 6 characters & above required"
            return false
        }else {
            login_password_text_input_layout.error = null
        }
        return true
    }

    private fun showProgress() {
        loginButton.isEnabled = false
        loginProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        loginButton.isEnabled = true
        loginProgress.visibility = View.INVISIBLE
    }
}
