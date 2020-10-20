package edu.android.thinkr.utils

import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * @author robin
 * Created on 10/11/20
 */
fun AppCompatActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
fun EditText.takeWords() : String {
    return this.text.toString().trim()
}

fun AppCompatActivity.hideSoftKeyBoard(){
    this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
}