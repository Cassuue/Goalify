package ca.uqac.goalify

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class AddTask : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ajout des items dans le spinner
        val spinner = view.findViewById<Spinner>(R.id.typeTask)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.item_dropdown_type,
            R.layout.item_spinner_type
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            spinner.adapter = adapter
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
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                list_resDay[checkBox.tag.toString()] = isChecked
            }
        }

        // Ajout de la tâche dans la base de donnée
        btnAdd.setOnClickListener(){

            // On récupère les valeurs des champs
            val inputName = view.findViewById<EditText>(R.id.InputName).text.toString()
            val inputDesc = view.findViewById<EditText>(R.id.InputDesc).text.toString()
            val selectedType = spinner.selectedItem.toString()

            val positionSelectedColor = spinnerColor.selectedItemPosition
            val selectedColor = color_items[positionSelectedColor].text.toString()
            //Toast.makeText(requireContext(), selectedTag, Toast.LENGTH_SHORT).show()

            // TODO : Ajouter le code pour envoi dans la BDD + toast de confirmation

            Log.d("AddBDD", "Nom de la tache : ${inputName}")

            val newtask = database.child("users").child(userUid.toString()).child("tasks").push()

            val task = mapOf(
                "name" to inputName,
                "type" to selectedType,
                "color" to selectedColor,
                "description" to inputDesc,
                "users" to userUid,
                "days" to list_resDay
            )

            newtask.setValue(task)
                .addOnSuccessListener {
                    Log.d("AddBDD", "Ajout de la task avec succès !")
                }
                .addOnFailureListener { exception ->
                    Log.d("AddBDD","Erreur lors de l'enregistrement : ${exception.message}")
                }


            // Réinitialisation des champs du formulaire
            spinner.setSelection(0)
            spinnerColor.setSelection(0)
            view.findViewById<EditText>(R.id.InputName).setText("")
            view.findViewById<EditText>(R.id.InputDesc).setText("")

            arrayCheckBoxs.forEach { checkBox ->
                checkBox.isChecked = false
            }

            val fragmentHome = Home()

            // Utilise FragmentTransaction pour remplacer le fragment actuel par le framgment_add_task
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragmentHome)
                .addToBackStack(null)
                .commit()
        }
    }

    fun shouldWarnOnExit(): Boolean {
        return true
    }

    // Méthode pour gérer la logique de l’avertissement
    fun showWarning(onConfirmed: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Attention")
            .setMessage("Etes vous sûr de vouloir quitter la page ? Les modifications apportées ne seront pas enregistrées")
            .setPositiveButton("Oui") { _, _ -> onConfirmed() }
            .setNegativeButton("Non", null)
            .show()
    }
}