package ca.uqac.goalify

import android.content.Context
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
        authorTextView.text = "By: ${forum?.author}"
        createdAtTextView.text = "Created at: ${forum?.createdAt}"
        commentsCountTextView.text = "Comments: ${forum?.commentsCount}"

        // Ajouter un clic sur l'élément de la liste
        view.setOnClickListener {
            // Afficher un toast avec le titre du forum lorsque l'élément est cliqué
            Toast.makeText(context, "Forum clicked: ${forum?.title}", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
