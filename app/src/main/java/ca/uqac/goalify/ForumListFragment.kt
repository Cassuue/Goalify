package ca.uqac.goalify

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.uqac.goalify.databinding.FragmentForumListBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ForumListFragment : Fragment() {
    private lateinit var forumListView: ListView
    private lateinit var forumAdapter: ForumAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentForumListBinding.inflate(inflater, container, false)
        forumListView = binding.root.findViewById(R.id.forum_list_view)

        // Récupérer les forums depuis Firestore
        fetchVisibleThreads()

        // Configurer le bouton FloatingActionButton
        val fabAddThread = binding.root.findViewById<FloatingActionButton>(R.id.fab_add_thread)
        fabAddThread.setOnClickListener {
            showCreateThreadDialog()
        }

        return binding.root
    }

    private fun fetchVisibleThreads() {
        val db = FirebaseFirestore.getInstance()

        db.collection("forum")
            .whereEqualTo("visible", true)
            .get()
            .addOnSuccessListener { documents ->
                val forumList = mutableListOf<Forum>()
                for (document in documents) {
                    val title = document.getString("title") ?: ""
                    val author = document.getString("author") ?: "Unknown"
                    val createdAt = document.getTimestamp("created_at")?.toDate()?.let { formatDate(it) } ?: "Unknown"
                    val commentsCount = document.get("comments") as? List<*> ?: emptyList<Any>()
                    val commentsCountValue = commentsCount.size

                    forumList.add(Forum(
                        title, author, createdAt, commentsCountValue,
                        documentId = document.id
                    ))
                }

                forumAdapter = ForumAdapter(requireContext(), forumList)
                forumListView.adapter = forumAdapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Erreur: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    private fun showCreateThreadDialog() {
        // Création d'une boîte de dialogue pour saisir le titre et le contenu du thread

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_create_thread)

        dialog.findViewById<Button>(R.id.addForum).setOnClickListener(){

            val title = dialog.findViewById<EditText>(R.id.titleEditText).text.toString()
            val content = dialog.findViewById<EditText>(R.id.contentEditText).text.toString()
            if (title.isNotBlank() && content.isNotBlank()) {
                createThread(title, content)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.findViewById<ImageButton>(R.id.close).setOnClickListener(){
            dialog.dismiss()
        }

        dialog.show()


        /*val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Créer un thread")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val titleEditText = EditText(requireContext())
        titleEditText.hint = "Titre"
        layout.addView(titleEditText)

        val contentEditText = EditText(requireContext())
        contentEditText.hint = "Contenu"
        layout.addView(contentEditText)

        builder.setView(layout)

        builder.setPositiveButton("Créer") { _, _ ->
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            if (title.isNotBlank() && content.isNotBlank()) {
                createThread(title, content)
            } else {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Annuler", null)

        builder.show()*/
    }

    private fun createThread(title: String, content: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email ?: "Anonyme (voir les erreurs)"

        val db = FirebaseFirestore.getInstance()
        val newThread = hashMapOf(
            "title" to title,
            "content" to content,
            "author" to userEmail.substringBefore('@'),
            "created_at" to Timestamp.now(),
            "visible" to true
        )

        db.collection("forum")
            .add(newThread)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Fil créé avec succès", Toast.LENGTH_SHORT).show()
                fetchVisibleThreads()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Erreur: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }
}
