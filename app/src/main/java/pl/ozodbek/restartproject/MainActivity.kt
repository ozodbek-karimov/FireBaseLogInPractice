package pl.ozodbek.restartproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import pl.ozodbek.restartproject.databinding.ActivityMainBinding
import pl.ozodbek.restartproject.models.Users

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var startGoogleSignIn: ActivityResultLauncher<Intent>
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var reference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("User")



        startGoogleSignIn = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                handleSignInResult(data)
            }
        }

        binding.signIn.setOnClickListener {
            // Configure Google Sign-In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build()

            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = mGoogleSignInClient.signInIntent
            startGoogleSignIn.launch(signInIntent)

        }

        binding.signOut.setOnClickListener {
            googleSignInClient?.signOut()
            auth.signOut()
        }

        binding.buttonintent.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = android.net.Uri.fromParts("package", packageName, null)
            startActivity(intent)
        }


    }

    private fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val user = Users(
                account?.displayName,
                account?.id,
                account?.email,
                account?.photoUrl.toString()
            )
            reference
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var itHas = false
                        val children = snapshot.children
                        children.forEach {
                            val value = it.getValue(Users::class.java)
                            if (value != null && value.uid == user.uid) {
                                itHas = true
                            }
                        }
                        if (itHas) {
                            val intent = Intent(this@MainActivity, UsersActivity::class.java)
                                .putExtra("id", user.uid)
                            startActivity(intent)
                        } else {
                            setNewUser(user)
                        }
                    }


                    override fun onCancelled(error: DatabaseError) {
                    }

                })
            // Sign in with Firebase
            firebaseAuthWithGoogle(account?.idToken)
        } catch (e: ApiException) {
            // Handle sign-in failure (e.g., invalid credentials, network error)
            Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setNewUser(user: Users) {
        reference.child(user.uid ?: "").setValue(user)
            .addOnSuccessListener {
                val intent = Intent(this@MainActivity, UsersActivity::class.java)
                    .putExtra("id", user.uid)
                startActivity(intent)
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = auth.currentUser
                    Toast.makeText(this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show()
                } else {
                    // Sign in failure
                    Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
