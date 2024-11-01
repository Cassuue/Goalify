package ca.uqac.goalify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialisation de Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Récupération des éléments de la vue
        val emailField = findViewById<EditText>(R.id.etRegisterEmail)
        val passwordField = findViewById<EditText>(R.id.etRegisterPassword)
        val confirmPasswordField = findViewById<EditText>(R.id.etConfirmPassword)
        val createAccountButton = findViewById<Button>(R.id.btnCreateAccount)
        val backToLoginText = findViewById<TextView>(R.id.tvBackToLogin)

        // Créer un compte
        createAccountButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }
        }

        // Retour à la page de connexion
        backToLoginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateUI(auth.currentUser)
                } else {
                    Toast.makeText(this, "Échec de la création de compte : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, "Compte créé avec succès!", Toast.LENGTH_SHORT).show()
            // Rediriger vers l'activité principale après création du compte
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
