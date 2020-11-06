package edu.android.thinkr

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.android.thinkr.adapters.SubjectListAdapter
import edu.android.thinkr.models.Subject
import edu.android.thinkr.utils.AppConstants.CHAT_ROOM_KEY
import edu.android.thinkr.utils.AppConstants.COLLECTION_USERS
import edu.android.thinkr.utils.Resource
import edu.android.thinkr.utils.showToast
import edu.android.thinkr.viewModel.AppViewModel

class MainActivity : AppCompatActivity(), SubjectListAdapter.OnSubjectClickedListener {
    private lateinit var firebaseAuthStateListener : FirebaseAuth.AuthStateListener
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navigationView : NavigationView
    private lateinit var drawerIcon : ImageView
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
        setContentView(R.layout.activity_select_module)
        getChatData()

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        drawerIcon = findViewById(R.id.hamburger_fab)
        recyclerView = findViewById(R.id.recycler_subjects)
        progressBar = findViewById(R.id.subject_load_progress)
        adapter = SubjectListAdapter(chatData, this, this)
        recyclerView.adapter = adapter
        navigationView.setCheckedItem(R.id.drawer_home)


        drawerIcon.setOnClickListener {
            openDrawer()
        }

            firebaseAuthStateListener = FirebaseAuth.AuthStateListener {
            val user : FirebaseUser? = it.currentUser
            if (user == null){
                startActivity(Intent(this, LoginActivity::class.java))
                showToast("Please sign-In")
                finish()
            }
        }
        setupNavListeners()
        setUpNavHeaders()
    }

    private fun setUpNavHeaders() {
        val view = navigationView.getHeaderView(0)
        val headerImage: ImageView = view.findViewById(R.id.nav_header_image)
        val headerName : TextView = view.findViewById(R.id.nav_header_name)
        val headerEmail : TextView = view.findViewById(R.id.nav_header_email)

        auth.currentUser.apply {
            headerName.text = this?.displayName
            headerEmail.text = this?.email
        }


        firestore.collection(COLLECTION_USERS).document(auth.currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful){
                Glide.with(this)
                    .load(it.result?.get("imageUrl") as String)
                    .into(headerImage)
            }else{
                showToast(it.exception?.message!!)
            }
        }
    }

    private fun getChatData() {
        chatData = listOf(
            Subject("Math", R.drawable.ic_math, R.color.md_amber_100, R.color.md_amber_50, "fWJfvwbLkqZBJbA7IPfr"),
            Subject("English", R.drawable.ic_english, R.color.md_amber_200,  R.color.md_amber_100, "13kgvYmbYgvDpE9bO5v0"),
            Subject("Chemistry", R.drawable.ic_chemistry, R.color.md_amber_300, R.color.md_amber_100, "1RVdBRppF7QApbrOZ3mm"),
            Subject("Life Science", R.drawable.ic_life_science, R.color.md_amber_100, R.color.md_amber_50, "6efSteXeg68a50rfWK83"),
            Subject("Accounting", R.drawable.ic_accounting, R.color.md_amber_200, R.color.md_amber_100, "fJwa7sHnvRuI8TQJjBYF"),
            Subject("French", R.drawable.ic_french, R.color.md_amber_300, R.color.md_amber_100, "NyKD5yTvNnA9Tm2ZDrRE")
        )
    }

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuthStateListener)
        navigationView.setCheckedItem(R.id.drawer_home)
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

    private fun hideDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                hideDrawer()
            }
            else -> {
                showExitAlert()
            }
        }
    }
    private fun showExitAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.thinkrpen)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .create().show()
    }

    private fun setupNavListeners() {
        navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { menuItem ->
            val checkedItem = navigationView.checkedItem
            if (checkedItem != null && checkedItem.itemId == menuItem.itemId) return@OnNavigationItemSelectedListener false
            when (menuItem.itemId) {
                R.id.drawer_home -> navigationView.setCheckedItem(R.id.drawer_home)
                R.id.drawer_settings ->startActivity(Intent(this, SettingsActivity::class.java))
                R.id.drawer_timer -> startActivity(Intent(this, ThinkinActivity::class.java))
                R.id.drawer_logout -> logOut()
            }
            hideDrawer()
            true
        })
    }

    private fun logOut() {
        auth.signOut()
    }
}