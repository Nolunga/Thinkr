package edu.android.thinkr

/**
 * @author robin
 * Created on 10/29/20
 */
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.android.thinkr.utils.AppConstants
import edu.android.thinkr.utils.showToast
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var firebaseAuthStateListener : FirebaseAuth.AuthStateListener
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
        firebaseAuthStateListener = FirebaseAuth.AuthStateListener {
            val user : FirebaseUser? = it.currentUser
            if (user == null){
                startActivity(Intent(this, LoginActivity::class.java))
                showToast("Please sign-In")
                finish()
            }
        }
        setupViews()

    }

    private fun setupViews() {
        auth.currentUser.apply {
            tv_email.text = this?.email
        }

        firestore.collection(AppConstants.COLLECTION_USERS).document(auth.currentUser?.uid!!).get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    tv_phone.text = it.result!!["phoneNo"] as String
                    tv_username.text = it.result!!["userName"] as String
                    tv_name.text = it.result!!["fullName"] as String

                    Glide.with(this)
                        .load(it.result?.get("imageUrl") as String)
                        .circleCrop()
                        .into(tv_imageView)
                }
                else{
                    showToast(it.exception?.message!!)
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
}