package pl.ozodbek.restartproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import pl.ozodbek.restartproject.databinding.ActivityUsersBinding
import pl.ozodbek.restartproject.models.Users

class UsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private val TAG = "UsersActivity"
    private lateinit var userAdapter: UserAdapter
    private lateinit var list: ArrayList<Users>
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid = intent.getStringExtra("id") ?:""
        binding.recyclerview.adapter = userAdapter
        list = ArrayList()
        userAdapter = UserAdapter(list){
            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("User", it)
            intent.putExtra("id", uid)
            startActivity(intent)
        }

        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("User")
        auth = FirebaseAuth.getInstance()

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                val children = snapshot.children
                children.forEach {
                    val value = it.getValue(Users::class.java)
                    if (value != null && uid != value.uid) {
                        list.add(value)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }


            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}