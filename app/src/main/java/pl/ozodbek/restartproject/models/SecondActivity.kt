package pl.ozodbek.restartproject.models

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import pl.ozodbek.restartproject.databinding.ActivitySecondBinding
import java.util.concurrent.TimeUnit

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding
    private lateinit var auth: FirebaseAuth
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var verificationId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.sendBtn.setOnClickListener{
            val phoneNumber = binding.phoneNumberEt.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                // Request verification code
                sendVerificationCode(phoneNumber)
            } else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }

        binding.verifyBtn.setOnClickListener {
            val receivedCode = binding.codeEt.text.toString().trim()
            if (receivedCode.isNotEmpty()){
                verifyPhoneNumberWithCode(receivedCode)
            }else{
                Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show()
            }
        }

        binding.resendBtn.setOnClickListener {
            val phoneNumber = binding.phoneNumberEt.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                // Resend verification code
                resendVerificationCode(phoneNumber, resendToken)
            } else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Automatically verify the code on some devices
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // Verification failed, handle the error
                    Toast.makeText(this@SecondActivity, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    // Save the verification ID to be used later
                    this@SecondActivity.verificationId = verificationId
                    // Show the verification code entry UI
                    // You can navigate to a new activity to enter the code or display an EditText
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken?) {
        val options = token?.let {
            PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        // This callback is not relevant for resending
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        // Verification failed, handle the error
                        Toast.makeText(this@SecondActivity, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        resendToken: PhoneAuthProvider.ForceResendingToken
                    ) {
                        // Save the new verification ID and token to be used later
                        this@SecondActivity.verificationId = verificationId
                        this@SecondActivity.resendToken = resendToken
                        // Show the verification code entry UI again
                        // You can navigate to a new activity to enter the code or display an EditText
                        Toast.makeText(this@SecondActivity, "Verification code sent again", Toast.LENGTH_SHORT).show()
                    }
                })
                .setForceResendingToken(it) // Set the token for resending
                .build()
        }

        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun verifyPhoneNumberWithCode(code: String) {
        // Verify the code entered by the user
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Phone number is verified and user is signed in
                    val user = task.result?.user
                    Toast.makeText(this@SecondActivity, "Phone number verified", Toast.LENGTH_SHORT).show()
                } else {
                    // Sign in failed, handle the error
                    Toast.makeText(this@SecondActivity, "Verification failed", Toast.LENGTH_SHORT).show()
                }
            }
    }


}