package ca.uqac.goalify.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ca.uqac.goalify.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import ca.uqac.goalify.R

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Initialisation de Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configuration de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Récupération des éléments de la vue
        val emailField = view.findViewById<EditText>(R.id.etEmail)
        val passwordField = view.findViewById<EditText>(R.id.etPassword)
        val signInButton = view.findViewById<Button>(R.id.btnSignIn)
        val forgotPasswordText = view.findViewById<TextView>(R.id.tvForgotPassword)
        val createAccountText = view.findViewById<TextView>(R.id.tvCreateAccount)
        val signInWithGoogleText = view.findViewById<TextView>(R.id.tvSignInWithGoogle)

        // Connexion avec email/mot de passe
        signInButton.setOnClickListener {
            Log.d("LoginFragment", "Sign-in button clicked")
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            signInWithEmail(email, password)
        }

        // Mot de passe oublié
        forgotPasswordText.setOnClickListener {
            val email = emailField.text.toString()
            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Email de réinitialisation envoyé.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Erreur : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Veuillez entrer votre email", Toast.LENGTH_SHORT).show()
            }
        }

        // Créer un compte
        createAccountText.setOnClickListener {
            // Naviguer vers le fragment de création de compte
            findNavController().navigate(R.id.navigation_register)
        }

        // Connexion avec Google
        signInWithGoogleText.setOnClickListener {
            signInWithGoogle()
        }

        return view
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    updateUI(auth.currentUser)
                } else {
                    Toast.makeText(requireContext(), "Échec de la connexion : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account?.idToken!!)
            } catch (e: ApiException) {
                Log.w("LoginFragment", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    updateUI(auth.currentUser)
                } else {
                    Toast.makeText(requireContext(), "Échec de la connexion avec Google : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Rediriger vers l'activité principale après connexion
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
    }
}