package edu.android.thinkr.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/**
 * @author robin
 * Created on 10/17/20
 */
@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
class ChatMessage constructor(){
    var message: String = ""
    var user_id: String = ""
    var message_id: String = ""
    @ServerTimestamp var timestamp: Date? = null

    constructor(message: String, user_id: String, message_id : String, timestamp: Date?) : this(){
        this.message = message
        this.user_id = user_id
        this.message_id = message_id
        this.timestamp = timestamp
    }
}
