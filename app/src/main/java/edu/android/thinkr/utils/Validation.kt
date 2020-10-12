package edu.android.thinkr.utils

import android.text.TextUtils
import java.util.regex.Pattern

object Validation {

    fun isValidEmail(email: String): Boolean {
        val matcher = android.util.Patterns.EMAIL_ADDRESS.matcher(email)
        return matcher.matches()
    }

    fun isValidPhoneNo(phoneNo: String?): Boolean {
        return phoneNo != null && phoneNo.length >= 10
    }

    fun isValidUsername(username : String) :Boolean{
        return !TextUtils.isEmpty(username)
    }

    fun isValidPassword(pass: String?): Boolean {
        return pass != null && pass.length >= 6
    }

    fun isValidConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

}