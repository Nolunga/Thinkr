package edu.android.thinkr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.android.thinkr.adapters.ChatAdapter
import edu.android.thinkr.models.ChatMessage
import edu.android.thinkr.models.Subject
import edu.android.thinkr.utils.AppConstants
import edu.android.thinkr.utils.AppConstants.CHAT_ROOM_KEY
import edu.android.thinkr.utils.AppConstants.COLLECTION_CHATS
import edu.android.thinkr.utils.showToast
import edu.android.thinkr.viewModel.AppViewModel
import kotlinx.coroutines.coroutineScope

@Suppress("unused")
class ChatActivity : AppCompatActivity() {
    private lateinit var firebaseAuthStateListener : FirebaseAuth.AuthStateListener
    private lateinit var toolbar: Toolbar
    private lateinit var sendImage: ImageView
    private lateinit var subject : Subject
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private val fireStore = Firebase.firestore
    private var allChats : List<ChatMessage> = mutableListOf()
    private lateinit var messageRefSnapshotListener : ListenerRegistration
    private lateinit var messageEditText: EditText
    private lateinit var userID : String
    private val allUsersCollectionRef = fireStore.collection(AppConstants.COLLECTION_USERS)
    private val allChatRoomsCollectionRef = fireStore.collection(AppConstants.COLLECTION_CHAT_ROOMS)
    private lateinit var chatsCollection : CollectionReference

    private val viewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory(application).create(AppViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        firebaseAuthStateListener = FirebaseAuth.AuthStateListener {
            val user : FirebaseUser? = it.currentUser
            if (user == null){
                startActivity(Intent(this, LoginActivity::class.java))
                showToast("Please sign-In")
                finish()
            }
            else{
                it.currentUser.apply {
                    userID = this!!.uid
                }
            }
        }

        adapter = ChatAdapter(allChats, this)
        toolbar = findViewById(R.id.chat_toolbar)
        sendImage = findViewById(R.id.send_message_icon)
        recyclerView = findViewById(R.id.chat_recycler)
        recyclerView.adapter = adapter
        messageEditText = findViewById(R.id.input_message)

        sendImage.setOnClickListener {
            this.sendImage.isClickable = false

            if (messageEditText.text.toString().trim() != ""){
                try {
                    val message = messageEditText.text.toString()
                    messageEditText.setText("")
                    val chatDocument = chatsCollection.document()
                    val chatMessage = ChatMessage(message, userID, chatDocument.id, null, "")
                    chatDocument.set(chatMessage).addOnCompleteListener {
                        if (it.isSuccessful){
                            showToast("Message Sent")
                            messageEditText.setText("")
                        }else{
                            showToast(it.exception?.message!!)
                        }
                        this.sendImage.isClickable = true
                    }
                }catch (e:Exception){
                    showToast(e.message!!)
                    this.sendImage.isClickable = true
                }
            }
        }

        messageEditText.setOnClickListener {
            recyclerView.scrollToPosition(adapter.itemCount - 1)
        }
        if(intent.hasExtra(CHAT_ROOM_KEY)){
            subject = intent.extras?.getParcelable(CHAT_ROOM_KEY)!!
            chatsCollection = allChatRoomsCollectionRef.document(subject.chat_room_id).collection(COLLECTION_CHATS)
        }
        setUpActionBar(subject)
    }

    private fun setUpChatListener() {
        messageRefSnapshotListener = chatsCollection.orderBy("timestamp").addSnapshotListener{value, error ->
            if (error != null){
                error.message?.let { showToast(it) }
                return@addSnapshotListener
            }
            if (value != null) {
                Log.e("TAG", "setUpChatListener: got all messages! its ${value.documents}" )
                val chatList = value.toObjects(ChatMessage::class.java)
                allChats = chatList
                adapter.addChats(chatList)
            }
        }
    }

    private fun setUpActionBar(subject: Subject) {
        toolbar.title = "${subject.chat_room_name} Chat"
        recyclerView.setBackgroundColor(ContextCompat.getColor(this, subject.chatBackgroundColor))
    }

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuthStateListener)
        setUpChatListener()
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(firebaseAuthStateListener)
        messageRefSnapshotListener.remove()
    }
}