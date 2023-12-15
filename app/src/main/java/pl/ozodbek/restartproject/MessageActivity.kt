package pl.ozodbek.restartproject

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import pl.ozodbek.restartproject.databinding.ActivityMessageBinding
import pl.ozodbek.restartproject.models.Message
import pl.ozodbek.restartproject.models.Users
import java.text.SimpleDateFormat
import java.util.Date

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding
    private lateinit var currentUserId: String
    private lateinit var user: Users
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("User")

        currentUserId = intent.getStringExtra("id") ?: ""
        user = intent.getSerializableExtra("User") as Users

        messageList = ArrayList()
        messageAdapter = MessageAdapter(messageList, currentUserId)
        binding.recyclerview.adapter = messageAdapter

        binding.sendButton.setOnClickListener {
            val text = binding.messageEdittext.text.toString()
            val message = Message(text, user.uid, currentUserId, getData())
            val key = reference.push().key

            reference.child(user.uid ?: "").child("messages").child(currentUserId)
                .child(key ?: "").setValue(message)

            reference.child(currentUserId).child("messages").child(user.uid ?: "")
                .child(key ?: "").setValue(message)

            binding.messageEdittext.setText("")
        }

        reference.child(currentUserId).child("messages").child(user.uid ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    val children = snapshot.children
                    children.forEach {
                        val value = it.getValue(Message::class.java)
                        if (value != null) {
                            messageList.add(value)
                        }
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

    @SuppressLint("SimpleDateFormat")
    private fun getData(): String? {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
        return simpleDateFormat.format(date)
    }
}