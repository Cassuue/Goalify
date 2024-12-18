package ca.uqac.goalify

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

private const val ARG_TITLE = "taskTitle"
private const val ARG_DESC = "taskDesc"


class AddTask : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var title: String? = null
    private var desc: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            desc = it.getString(ARG_DESC)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String) =
            AddTask().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_DESC, desc)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputName = view.findViewById<EditText>(R.id.InputName)
        val inputDesc = view.findViewById<EditText>(R.id.InputDesc)

        if(title != null){
            inputName.setText(title)
        }

        if(desc != null){
            inputDesc.setText(desc)
        }

        // Initialiser FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Récupérer l'UID de l'utilisateur actuel, s'il est connecté
        val currentUser = auth.currentUser
        val userUid = currentUser?.uid

        database = Firebase.database.reference

        val spinnerColor = view.findViewById<Spinner>(R.id.BtnColor)

        val color_items = listOf(
            SpinnerItemColor(R.drawable.color_item_red, "red"),
            SpinnerItemColor(R.drawable.color_item_orange, "orange"),
            SpinnerItemColor(R.drawable.color_item_yellow, "yellow"),
            SpinnerItemColor(R.drawable.color_item_green, "green"),
            SpinnerItemColor(R.drawable.color_item_blue, "blue"),
            SpinnerItemColor(R.drawable.color_item_purple, "purple")
        )

        val adapter = SpinnerAdapterColor(requireContext(), color_items)
        spinnerColor.adapter = adapter

        // on déclare les variables nécessaires pour intéragir avec le frameLayout
        val btnAdd = view.findViewById<Button>(R.id.Add)

        val checkBoxMon = view.findViewById<CheckBox>(R.id.CheckMon)
        val checkBoxTue = view.findViewById<CheckBox>(R.id.CheckTue)
        val checkBoxWed = view.findViewById<CheckBox>(R.id.CheckWed)
        val checkBoxThu = view.findViewById<CheckBox>(R.id.CheckThu)
        val checkBoxFri = view.findViewById<CheckBox>(R.id.CheckFri)
        val checkBoxSat = view.findViewById<CheckBox>(R.id.CheckSat)
        val checkBoxSun = view.findViewById<CheckBox>(R.id.CheckSun)

        // Dictionnaire pour récupérer les valeurs des checkboxs
        var list_resDay = mutableMapOf(
            "monday" to false,
            "tuesday" to false,
            "wednesday" to false,
            "thursday" to false,
            "friday" to false,
            "saturday" to false,
            "sunday" to false)

        // Tableau pour parcourir toutes les checkboxs
        val arrayCheckBoxs = listOf(checkBoxMon, checkBoxTue, checkBoxWed, checkBoxThu, checkBoxFri, checkBoxSat, checkBoxSun)

        // Pour chaque checkbox on récupère la valeur et on l'ajoute dans le dictionnaire
        arrayCheckBoxs.forEach { checkBox->
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                list_resDay[checkBox.tag.toString()] = isChecked
            }
        }

        // Ajout de la tâche dans la base de donnée
        btnAdd.setOnClickListener(){

            // On récupère les valeurs des champs
            val taskName = inputName.text.toString()

            val taskDesc = inputDesc.text.toString()

            val positionSelectedColor = spinnerColor.selectedItemPosition
            val selectedColor = color_items[positionSelectedColor].text.toString()

            val newtask = database.child("users").child(userUid.toString()).child("tasks").push()

            val task = mapOf(
                "name" to taskName,
                "color" to selectedColor,
                "description" to taskDesc,
                "validate" to false,
                "days" to list_resDay
            )

            newtask.setValue(task)
                .addOnSuccessListener {
                    Log.d("BDD", "Ajout de la task avec succès !")
                }
                .addOnFailureListener { exception ->
                    Log.d("BDD","Erreur lors de l'enregistrement : ${exception.message}")
                }


            // Réinitialisation des champs du formulaire
            spinnerColor.setSelection(0)
            inputName.setText("")
            inputDesc.setText("")

            arrayCheckBoxs.forEach { checkBox ->
                checkBox.isChecked = false
            }

            findNavController().navigate(R.id.navigation_home)

        }

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.setOnItemSelectedListener { item ->
            val currentDestination = findNavController().currentDestination

            if (currentDestination?.id == R.id.navigation_add_task) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Attention")
                    .setMessage("Etes-vous sûr de vouloir quitter la page ? Les modifications apportées ne seront pas enregistrées")
                    .setPositiveButton("Oui") { _, _ -> findNavController().navigate(item.itemId) }
                    .setNegativeButton("Non", null)
                    .show()
                true
            } else {

                findNavController().navigate(item.itemId)
                true
            }
        }
    }
}