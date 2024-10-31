package com.example.goalify

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import com.example.goalifyperso.SpinnerAdapter
import com.example.goalifyperso.SpinnerItem

class AddTask : Fragment() {

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

        val spinnerColor = view.findViewById<Spinner>(R.id.BtnColor)

        val color_items = listOf(
            SpinnerItem(R.drawable.color_item_red, "red"),
            SpinnerItem(R.drawable.color_item_orange, "orange"),
            SpinnerItem(R.drawable.color_item_yellow, "yellow"),
            SpinnerItem(R.drawable.color_item_green, "green"),
            SpinnerItem(R.drawable.color_item_blue, "blue"),
            SpinnerItem(R.drawable.color_item_purple, "purple")
        )


        val adapter = SpinnerAdapter(requireContext(), color_items)
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
            "Monday" to false,
            "Tuesday" to false,
            "Wednesday" to false,
            "Thursday" to false,
            "Friday" to false,
            "Saturday" to false,
            "Sunday" to false)

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
            val inputName = view.findViewById<EditText>(R.id.InputName).text
            val inputDesc = view.findViewById<EditText>(R.id.InputDesc).text
            val selectedSpinner = spinner.selectedItem.toString()

            val positionSelectedColor = spinnerColor.selectedItemPosition
            val selectedTag = color_items[positionSelectedColor].text
            //Toast.makeText(requireContext(), selectedTag, Toast.LENGTH_SHORT).show()

            // TODO : Ajouter le code pour envoi dans la BDD + toast de confirmation

            // Réinitialisation des champs du formulaire
            spinner.setSelection(0)
            view.findViewById<EditText>(R.id.InputName).setText("")
            view.findViewById<EditText>(R.id.InputDesc).setText("")

            arrayCheckBoxs.forEach { checkBox ->
                checkBox.isChecked = false
            }
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