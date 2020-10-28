package edu.android.thinkr.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import edu.android.thinkr.ChatActivity
import edu.android.thinkr.R
import edu.android.thinkr.models.ChatMessage
import edu.android.thinkr.utils.AppConstants
import edu.android.thinkr.viewModel.AppViewModel

/**
 * @author robin
 * Created on 10/17/20
 */

class ChatAdapter(val context: Context) : ListAdapter<ChatMessage, ChatAdapter.ViewHolder>(ChatDiffCallback()) {
    private val auth = Firebase.auth
    companion object {
        const val receivedMessage = 1
        const val mySentMessage = 2
    }

    private val fireStore = Firebase.firestore
    private val allUsersCollectionRef = fireStore.collection(AppConstants.COLLECTION_USERS)


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var profileImage : CircleImageView = itemView.findViewById(R.id.chat_profile_image)
        private var chatMessage : TextView = itemView.findViewById(R.id.chat_message)
        private var chatUserName : TextView = itemView.findViewById(R.id.chat_user_name)

        fun bind(message: ChatMessage) {
            chatMessage.text = message.message
            Log.e("Glide Gun", "message.profile_image_url.toString()")
            allUsersCollectionRef.document(message.user_id).get().addOnCompleteListener {
                if (it.isSuccessful){
                    Log.e("YActivity Image gotten", "its at ${it.result?.get("imageUrl")}" )
                    try {
                        chatUserName.text = it.result?.get("userName") as String
                        Glide.with(context)
                            .load(it.result?.get("imageUrl") as String)
                            .error(R.drawable.ic_account)
                            .into(profileImage)
                    }catch (e : Exception){
                        Log.e("Failed loading" , e.printStackTrace().toString() )
                    }
                }else{
                    Log.e("Loader Error", "Load Image Failure ${it.exception}" )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == receivedMessage){
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_list_item, parent, false))
        }else{
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_sent_item, parent, false))
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).user_id == auth.currentUser?.uid ){
            mySentMessage
        }else{
            receivedMessage
        }
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>(){
    override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem.message_id == newItem.message_id
    }

    override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem.message == newItem.message
    }
}