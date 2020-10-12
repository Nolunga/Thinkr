package edu.android.thinkr.repository

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.android.thinkr.user.User
import edu.android.thinkr.utils.AppConstants.COLLECTION_USERS
import edu.android.thinkr.utils.Resource
import kotlinx.coroutines.tasks.await

class FirebaseRepo private constructor() {
    private val auth = Firebase.auth
    private val fireStore = Firebase.firestore
    private val allUsersCollectionRef = fireStore.collection(COLLECTION_USERS)


    suspend fun registerUser(email: String, password: String): Resource<String> {
        var result : Resource<String> = Resource.Loading()
        try {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                result = if (it.isComplete) Resource.Success("Account successfully created") else Resource.Failure(it.exception?.message!!)
            }.addOnFailureListener {
                result = Resource.Failure(it.message!!)
            }.await()
            Resource.Success("Account successfully created")
        } catch (e: Exception) {
            Log.e(TAG, e.message!! )
            result = Resource.Failure(e.message!!)
        }
        return result
    }

    suspend fun putUser(user: User): Resource<String>? {
        var result : Resource<String> = Resource.Loading()
        try {
            allUsersCollectionRef.document(user.uid).set(user, SetOptions.merge())
                .addOnCompleteListener {
                    Log.e(TAG, "Account successfully logged in" )
                    result = if (it.isSuccessful) Resource.Success("Account successfully created") else Resource.Failure(it.exception?.message!!)
                }
                .addOnFailureListener{
                    Log.e(TAG,  it.message!!)
                    result = Resource.Failure(it.message!!)
                }.await()
        }catch (e :Exception){
            Log.e(TAG, e.message!! )
            result = Resource.Failure(e.message!!)
        }
        return result
    }

    suspend fun signInUsers(email: String, password: String): Resource<String> {
        var result : Resource<String> = Resource.Loading()
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    result = if (it.isSuccessful) Resource.Success("Account successfully logged in") else Resource.Failure(it.exception?.message!!)
                }
                .addOnFailureListener {
                    Log.e(TAG,  it.message!!)
                    result = Resource.Failure(it.message!!)
                }.await()
        }catch (e : Exception){
            Log.e(TAG, e.message!! )
            result = Resource.Failure(e.message!!)
        }
        return result
    }

    suspend fun resetPassword(email: String): Resource<String> {
        var result : Resource<String> = Resource.Loading()
        try {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    result = if (it.isSuccessful) Resource.Success("Password reset email sent") else Resource.Failure(it.exception?.message!!)
                }
                .addOnFailureListener {
                    Log.e(TAG,  it.message!!)
                    result = Resource.Failure(it.message!!)
                }.await()
        }catch (e : Exception){
            Log.e(TAG, e.message!! )
            result = Resource.Failure(e.message!!)
        }
        return result
    }


    companion object {
        private const val TAG = "FirebaseRepo"
        @Volatile
        private var INSTANCE: FirebaseRepo? = null
        @Synchronized
        fun getInstance(): FirebaseRepo {
            return INSTANCE ?: synchronized(this) {
                FirebaseRepo()
            }.also { INSTANCE = it }
        }
    }
}
