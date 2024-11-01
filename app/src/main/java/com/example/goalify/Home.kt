package com.example.goalify

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
import java.text.SimpleDateFormat
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"*/

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    /*private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    /*companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

// Pattern complet : d MMM yyyy, EEE, HH:mm:ss z
        val dateFormat = SimpleDateFormat("EEE")
        val day = dateFormat.format(Date())

        // Idée : comparer la valeur de "day" aux 3 premières lettres de la chaine de caractères
        // contenue dans la base de données pour trouver les taches apartenant au jour actuel
        // penser a tout passer en minuscule (true) : assertTrue { first.equals(firstCapitalized, true) }

        val first = "kotlin"
        val firstCapitalized = "KOTLIN"
        val test = first.equals(firstCapitalized, true)
        println("Test : $test")

        // Récupérer le LinearLayout défini en XML
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
        linearLayoutTasks.addView(divider)

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
}