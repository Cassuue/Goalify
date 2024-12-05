package ca.uqac.goalify.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.uqac.goalify.MainActivity
import ca.uqac.goalify.R
import ca.uqac.goalify.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        val data = result.data
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            activity?.finish()
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                        } else {
                            Toast.makeText(requireContext(), "Erreur de connexion Google", Toast.LENGTH_SHORT).show()
                            Log.d("Google", "Erreur de connexion Google: ${task.exception?.message}")
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Erreur de connexion Google", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Toast.makeText(requireContext(), "Erreur de connexion Google", Toast.LENGTH_SHORT).show()
            Log.d("Google", "Erreur de connexion Google: ${e.message}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        oneTapClient = Identity.getSignInClient(requireContext())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            activity?.finish()
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                        } else {
                            Toast.makeText(requireContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else{
                Toast.makeText(requireContext(), "Merci de remplir tous les champs !", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        binding.tvSignInWithGoogle.setOnClickListener {
            signInWithGoogle()
        }

        return binding.root
    }

    private fun signInWithGoogle() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent).build()
                googleSignInLauncher.launch(intentSenderRequest)
            }
            .addOnFailureListener { e: Exception ->
                Toast.makeText(requireContext(), "Erreur de connexion Google", Toast.LENGTH_SHORT).show()
                Log.d("Google", "Erreur de connexion Google: ${e.message}")
            }
    }
}