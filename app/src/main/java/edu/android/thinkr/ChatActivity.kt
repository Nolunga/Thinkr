package edu.android.thinkr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.android.thinkr.models.Subject
import edu.android.thinkr.utils.AppConstants

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val subject : Subject = intent.extras?.getParcelable(AppConstants.CHAT_ROOM_KEY)!!
        setUpActionBar(subject.chat_room_name)
    }

    private fun setUpActionBar(chatRoomName: String) {
        supportActionBar?.title = "$chatRoomName Chat"
    }
}