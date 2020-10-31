package edu.android.thinkr.utils

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import android.view.WindowManager
/**
 * @author robin
 * Created on 10/11/20
 */
fun AppCompatActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun TextInputEditText.takeWords() : String{
    return this.text.toString().trim()
}
fun EditText.takeWords() : String{
    return this.text.toString().trim()
}

fun playAnimation(context:Context, int: Int, view:View){

    val animation = AnimationUtils.loadAnimation(context, int)
    view.startAnimation(animation)
}

fun AppCompatActivity.hideSoftKeyBoard(){
    this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
}