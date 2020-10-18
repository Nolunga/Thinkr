package edu.android.thinkr.models

/**
 * @author robin
 * Created on 10/17/20
 */

data class ChatMessage (
    private var message: String,
    private val user_id: String,
    private val timestamp: String,
    private val profile_image: String,
    private val name: String
)
