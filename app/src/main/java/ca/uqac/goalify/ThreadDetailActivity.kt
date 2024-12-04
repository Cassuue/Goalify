package ca.uqac.goalify

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ThreadDetailActivity : AppCompatActivity() {
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentInput: EditText
    private lateinit var addCommentButton: Button
    private val db = FirebaseFirestore.getInstance()
    private lateinit var navController: NavController

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Ferme l'activité pour revenir en arrière
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread_detail)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Activer le bouton "Retour"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Détails"

        // Récupérer l'ID du document transmis par l'intent
        val threadDocumentId = intent.getStringExtra("THREAD_DOCUMENT_ID") ?: run {
            Toast.makeText(this, "Erreur: ID du thread manquant.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val threadTitle: TextView = findViewById(R.id.thread_title)
        val threadAuthor: TextView = findViewById(R.id.thread_author)
        val threadContent: TextView = findViewById(R.id.thread_content)
        commentsRecyclerView = findViewById(R.id.comments_recycler_view)
        commentInput = findViewById(R.id.comment_input)
        addCommentButton = findViewById(R.id.add_comment_button)

        // Configurer RecyclerView pour les commentaires
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Charger les détails du thread
        db.collection("forum").document(threadDocumentId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    threadTitle.text = document.getString("title")
                    threadAuthor.text = "Par : ${document.getString("author")}"
                    threadContent.text = document.getString("content")
                    loadComments(threadDocumentId)
                } else {
                    Toast.makeText(this, "Thread introuvable.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erreur: Impossible de charger les détails.", Toast.LENGTH_SHORT).show()
            }

        // Ajouter un commentaire
        addCommentButton.setOnClickListener {
            val commentText = commentInput.text.toString()
            if (commentText.isNotEmpty()) {
                addComment(threadDocumentId, commentText)
            } else {
                Toast.makeText(this, "Le commentaire est vide.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadComments(threadDocumentId: String) {
        db.collection("forum").document(threadDocumentId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val commentsList = document.get("comments") as? List<Map<String, Any>> ?: emptyList()
                    val comments = commentsList.map { commentData ->
                        val author = commentData["author"] as? String ?: "Inconnu"
                        val content = commentData["comment"] as? String ?: "Pas de contenu"
                        "$author: $content"
                    }
                    commentsRecyclerView.adapter = CommentsAdapter(comments)
                } else {
                    Toast.makeText(this, "Thread introuvable.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erreur: Impossible de charger les commentaires.", Toast.LENGTH_SHORT).show()
            }
    }


    private fun addComment(threadDocumentId: String, comment: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email ?: "Anonyme (voir les erreurs)"

        val commentData = mapOf(
            "comment" to comment,
            "author" to userEmail.substringBefore('@'),
            "created_at" to com.google.firebase.Timestamp.now()
        )

        db.collection("forum").document(threadDocumentId)
            .update("comments", com.google.firebase.firestore.FieldValue.arrayUnion(commentData))
            .addOnSuccessListener {
                Toast.makeText(this, "Commentaire ajouté avec succès.", Toast.LENGTH_SHORT).show()
                commentInput.text.clear()
                loadComments(threadDocumentId)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur: Impossible d'ajouter le commentaire.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
    }


}
