package ca.uqac.goalify.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import ca.uqac.goalify.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import ca.uqac.goalify.R

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Initialisation de Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Récupération des éléments de la vue
        val emailField = view.findViewById<EditText>(R.id.etRegisterEmail)
        val passwordField = view.findViewById<EditText>(R.id.etRegisterPassword)
        val confirmPasswordField = view.findViewById<EditText>(R.id.etConfirmPassword)
        val createAccountButton = view.findViewById<Button>(R.id.btnCreateAccount)
        val backToLoginText = view.findViewById<TextView>(R.id.tvBackToLogin)

        // Créer un compte
        createAccountButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(requireContext(), "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)

                val database = FirebaseDatabase.getInstance()
                val uid = auth.currentUser?.uid
                println("UID de l'utilisateur connecté : $uid")

                val userRef = database.reference.child("users").child(auth.currentUser?.uid ?: "")
                userRef.setValue("${auth.currentUser?.displayName}")
                    .addOnSuccessListener {
                        println("Données de l'utilisateur enregistrées !")
                    }
                    .addOnFailureListener { exception ->
                        println("Erreur lors de l'enregistrement : ${exception.message}")
                    }
            }
        }

        // Retour à la page de connexion
        backToLoginText.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    updateUI(auth.currentUser)
                } else {
                    Toast.makeText(requireContext(), "Échec de la création de compte : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(requireContext(), "Compte créé avec succès!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
    }
}