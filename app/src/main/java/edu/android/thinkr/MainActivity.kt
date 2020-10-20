package edu.android.thinkr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.android.thinkr.adapters.SubjectListAdapter
import edu.android.thinkr.models.Subject
import edu.android.thinkr.utils.AppConstants.CHAT_ROOM_KEY
import edu.android.thinkr.utils.Resource
import edu.android.thinkr.utils.showToast
import edu.android.thinkr.viewModel.AppViewModel

class MainActivity : AppCompatActivity(), SubjectListAdapter.OnSubjectClickedListener {
    private lateinit var firebaseAuthStateListener : FirebaseAuth.AuthStateListener
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : SubjectListAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var chatData : List<Subject>

    private val viewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory(application).create(AppViewModel::class.java)
    }


    private fun getChatRoomID(chatRoomName: String): String {
        var chatRoomID = ""
        viewModel.getChatRoomID(chatRoomName).observe(this, Observer {
            when(it){
                is Resource.Loading -> showProgress()
                is Resource.Success ->{
                    hideProgress()
                    chatRoomID = it.data
                }
                is Resource.Failure ->{
                    showToast(it.message)
                    hideProgress()
                }
            }
        })
        return chatRoomID
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getChatData()

        recyclerView = findViewById(R.id.recycler_subjects)
        progressBar = findViewById(R.id.subject_load_progress)
        adapter = SubjectListAdapter(chatData, this, this)
        recyclerView.adapter = adapter

            firebaseAuthStateListener = FirebaseAuth.AuthStateListener {
            val user : FirebaseUser? = it.currentUser
            if (user == null){
                startActivity(Intent(this, LoginActivity::class.java))
                showToast("Please sign-In")
                finish()
            }
        }
    }

    private fun getChatData() {
        chatData = listOf(
            Subject("Math", R.drawable.ic_math, R.color.md_blue_200, R.color.md_blue_50, "fWJfvwbLkqZBJbA7IPfr"),
            Subject("English", R.drawable.ic_english, R.color.md_red_200,  R.color.md_red_50, "13kgvYmbYgvDpE9bO5v0"),
            Subject("Life Science", R.drawable.ic_life_science, R.color.md_green_200, R.color.md_green_50, "6efSteXeg68a50rfWK83"),
            Subject("Chemistry", R.drawable.ic_chemistry, R.color.md_yellow_200, R.color.md_yellow_50, "1RVdBRppF7QApbrOZ3mm"),
            Subject("Accounting", R.drawable.ic_accounting, R.color.md_deep_purple_200, R.color.md_deep_purple_50, "fJwa7sHnvRuI8TQJjBYF"),
            Subject("French", R.drawable.ic_french, R.color.md_pink_200, R.color.md_pink_50, "NyKD5yTvNnA9Tm2ZDrRE")
        )
    }

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(firebaseAuthStateListener)
    }


    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

    private fun hideProgress() {
        progressBar.visibility = View.INVISIBLE
        recyclerView.visibility = View.VISIBLE
    }

    override fun onClick(subject: Subject) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(CHAT_ROOM_KEY, subject)
        startActivity(intent)
    }
}