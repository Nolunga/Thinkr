package edu.android.thinkr

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import edu.android.thinkr.utils.Resource
import edu.android.thinkr.utils.Validation
import edu.android.thinkr.utils.showToast
import edu.android.thinkr.utils.takeWords
import edu.android.thinkr.viewModel.AppViewModel
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_login.*

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var email : TextInputEditText
    private lateinit var sendButton : Button
    private lateinit var progress : ProgressBar

    private val viewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory(application).create(AppViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { super.onBackPressed() }
        setStatusBarWhite(this@ForgotPasswordActivity)

        email = findViewById(R.id.tgt_email)
        sendButton = findViewById(R.id.button_send)
        progress = findViewById(R.id.forgot_progress)
    }
    fun setStatusBarWhite(activity: AppCompatActivity){
        //Make status bar icons color dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.statusBarColor = Color.WHITE
        }
    }

    fun initiateResetPassword(view: View) {
        if (!validateFields()) return
        viewModel.resetPassword(email.takeWords()).observe(this, Observer {
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

        if (!Validation.isValidEmail(email.takeWords())){
            forgot_email_text_input_layout.error = "Enter valid Email"
            return false
        } else {
            forgot_email_text_input_layout.error = null
        }
        return true
    }

    private fun showProgress() {
        sendButton.isEnabled = false
        progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        sendButton.isEnabled = true
        progress.visibility = View.INVISIBLE
    }
}
