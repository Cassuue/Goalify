package ca.uqac.goalify

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast

class ForumAdapter(context: Context, private val forums: List<Forum>) :
    ArrayAdapter<Forum>(context, 0, forums) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_forum, parent, false)

        val forum = getItem(position)

        val titleTextView: TextView = view.findViewById(R.id.forum_title)
        val authorTextView: TextView = view.findViewById(R.id.forum_author)
        val createdAtTextView: TextView = view.findViewById(R.id.forum_created_at)
        val commentsCountTextView: TextView = view.findViewById(R.id.forum_comments_count)

        titleTextView.text = forum?.title
        authorTextView.text = "Par: ${forum?.author}"
        createdAtTextView.text = "Créé le : ${forum?.createdAt}"
        commentsCountTextView.text = "Commentaires: ${forum?.commentsCount}"

        // Ajouter un clic sur l'élément de la liste
        view.setOnClickListener {
            forum?.let {
                val intent = Intent(context, ThreadDetailActivity::class.java)
                intent.putExtra("THREAD_DOCUMENT_ID", forum.documentId)
                context.startActivity(intent)
            } ?: Toast.makeText(context, "Forum introuvable", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
