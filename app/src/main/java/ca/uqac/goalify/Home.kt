package ca.uqac.goalify

import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date

// TODO: Rename parameter arguments, choose names that match

class Home : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialiser FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Récupérer l'UID de l'utilisateur actuel, s'il est connecté
        val currentUser = auth.currentUser
        val userUid = currentUser?.uid

        database = Firebase.database.reference

        // Pattern complet : d MMM yyyy, EEE, HH:mm:ss z
        val dateFormat = SimpleDateFormat("EEE")
        val day = dateFormat.format(Date())

        val today = when (day) {
            "Mon" -> "monday"
            "Tue" -> "tuesday"
            "Wed" -> "wednesday"
            "Thu" -> "thursday"
            "Fri" -> "friday"
            "Sat" -> "saturday"
            else -> "sunday"
        }

        database.child("users").child(userUid.toString()).child("tasks").orderByChild("days/$today").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (taskSnapshot in dataSnapshot.children) {
                        // Récupère les informations de chaque tâche où "Monday" est à true
                        val taskName = taskSnapshot.child("name").value.toString()
                        val taskType = taskSnapshot.child("type").value.toString()
                        val taskColor = taskSnapshot.child("color").value.toString()
                        val taskDesc = taskSnapshot.child("description"). value.toString()
                        // Récupérer le LinearLayout défini en XML
                        val linearLayoutTasks = view.findViewById<LinearLayout>(R.id.listTask)

                        val color = when (taskColor) {
                            "blue" -> R.drawable.color_item_blue
                            "green" -> R.drawable.color_item_green
                            "orange" -> R.drawable.color_item_orange
                            "purple" -> R.drawable.color_item_purple
                            "yellow" -> R.drawable.color_item_yellow
                            else -> R.drawable.color_item_red
                        }

                        // Ajouter une tâche
                        addTaskView(linearLayoutTasks,taskName,taskDesc,color)

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Erreur lors de la récupération des tâches : ${databaseError.message}")
                }
            })



        /*// Récupérer le LinearLayout défini en XML
        val linearLayoutTasks = view.findViewById<LinearLayout>(R.id.listTask)

        // On crée le linear layout pour afficher la tache
        val mainLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                bottomMargin = 20.dpToPx()
            }
            orientation = LinearLayout.HORIZONTAL
        }

        // Création du CheckBox
        val checkBox = CheckBox(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(40.dpToPx(), 40.dpToPx())
        }

        // Création du LinearLayout vertical pour les TextViews
        val textContainer = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                marginStart = 20.dpToPx()
                marginEnd = 20.dpToPx()
            }
            orientation = LinearLayout.VERTICAL
        }

        // Création du premier TextView (nom de la tâche)
        val textNameTask = TextView(requireContext()).apply {
            id = View.generateViewId()
            text = "Tâche2"
            textSize = 17f
        }

        // Création du deuxième TextView (description de la tâche)
        val textDesc = TextView(requireContext()).apply {
            id = View.generateViewId()
            text = "Description2"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Ajouter les TextViews au LinearLayout vertical
        textContainer.addView(textNameTask)
        textContainer.addView(textDesc)

        // Création de l'ImageView
        val colorItem = ImageView(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(30.dpToPx(), 30.dpToPx()).apply {
                gravity = Gravity.CENTER
            }
            setImageResource(R.drawable.color_item_green) // Définissez votre image ici
        }
        mainLayout.addView(checkBox)
        mainLayout.addView(textContainer)
        mainLayout.addView(colorItem)

        // Création de la vue pour le diviseur
        val divider = View(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.dpToPx() // Hauteur de 1dp, utilisez l'extension dpToPx pour la conversion
            ).apply {
                marginStart = 25.dpToPx()
                marginEnd = 25.dpToPx()
                weight = 1f // Appliquer le poids si nécessaire
            }
            setBackgroundResource(R.color.grey)
        }

        // Ajouter le TextView au LinearLayout
        linearLayoutTasks.addView(mainLayout)
        linearLayoutTasks.addView(divider)*/

        val btn = view.findViewById<Button>(R.id.AddTask)

        btn.setOnClickListener(){
            //Toast.makeText(requireContext(), "Bouton cliqué !", Toast.LENGTH_SHORT).show()
            // Crée une instance du FragmentAddTask
            val fragmentAddTask = AddTask()

            // Utilise FragmentTransaction pour remplacer le fragment actuel par le framgment_add_task
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragmentAddTask)
                .addToBackStack(null)
                .commit()
        }

    }

    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun addTaskView(parentLayout: LinearLayout, taskName: String, taskDescription: String, colorResId: Int) {

        // Création du LinearLayout principal pour afficher la tâche
        val mainLayout = LinearLayout(parentLayout.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                bottomMargin = 20.dpToPx()
            }
            orientation = LinearLayout.HORIZONTAL
        }

        // Création du CheckBox
        val checkBox = CheckBox(parentLayout.context).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(40.dpToPx(), 40.dpToPx())
        }

        // Création du LinearLayout vertical pour les TextViews
        val textContainer = LinearLayout(parentLayout.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                marginStart = 20.dpToPx()
                marginEnd = 20.dpToPx()
            }
            orientation = LinearLayout.VERTICAL
        }

        // Création du TextView pour le nom de la tâche
        val textNameTask = TextView(parentLayout.context).apply {
            id = View.generateViewId()
            text = taskName
            textSize = 17f
        }

        // Création du TextView pour la description de la tâche
        val textDesc = TextView(parentLayout.context).apply {
            id = View.generateViewId()
            text = taskDescription
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Ajout des TextViews dans le LinearLayout vertical
        textContainer.addView(textNameTask)
        textContainer.addView(textDesc)

        // Création de l'ImageView pour la couleur de la tâche
        val colorItem = ImageView(parentLayout.context).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(30.dpToPx(), 30.dpToPx()).apply {
                gravity = Gravity.CENTER
            }
            setImageResource(colorResId) // Définissez la couleur de l'image
        }

        // Ajout des éléments dans le LinearLayout principal
        mainLayout.addView(checkBox)
        mainLayout.addView(textContainer)
        mainLayout.addView(colorItem)

        // Création de la vue de diviseur
        val divider = View(parentLayout.context).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.dpToPx() // Hauteur de 1dp, utilisez l'extension dpToPx pour la conversion
            ).apply {
                marginStart = 25.dpToPx()
                marginEnd = 25.dpToPx()
            }
            setBackgroundResource(R.color.grey)
        }

        // Ajouter le LinearLayout principal et le diviseur au parent
        parentLayout.addView(mainLayout)
        parentLayout.addView(divider)
    }
}