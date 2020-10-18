package edu.android.thinkr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var  auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        auth = FirebaseAuth.getInstance()

        val handler = Handler()
        handler.postDelayed( Runnable {
            if (isUserAuthenticated()){
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 3000)
    }

    private fun isUserAuthenticated() = auth.currentUser!=null
}