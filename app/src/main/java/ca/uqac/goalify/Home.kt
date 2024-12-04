package ca.uqac.goalify

import android.app.Dialog
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.Calendar


class Home : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var listAdapter: ListAdapter
    private lateinit var listData: Task
    var dataArrayList = ArrayList<Task?>()
    private lateinit var calendarView : CalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialiser FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Récupérer l'UID de l'utilisateur actuel, s'il est connecté
        val currentUser = auth.currentUser
        val userUid = currentUser?.uid

        database = Firebase.database.reference

        // Pattern complet : d MMM yyyy, EEE, HH:mm:ss z
        val locale = Locale.ENGLISH
        var dateFormat = SimpleDateFormat("EEE", locale)
        val day_today = dateFormat.format(Date())

        // Correspondance date avec le jour
        val today = when (day_today) {
            "Mon" -> "monday"
            "Tue" -> "tuesday"
            "Wed" -> "wednesday"
            "Thu" -> "thursday"
            "Fri" -> "friday"
            "Sat" -> "saturday"
            else -> "sunday"
        }
        val listView = view.findViewById<ListView>(R.id.listview)

//------------------------- Affichage des tâches du jour dans le listView
        affichageTaskDay(today, userUid, listView)

//------------------------- Action du floatting button
        val btn: FloatingActionButton = view.findViewById(R.id.AddTask)

        btn.setOnClickListener() {
            findNavController().navigate(R.id.navigation_propositions_tasks)
        }

    }

    fun updateTask(task : Task?, userID : String?){
        if (task != null) {

            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_modif_task)

            dialog.findViewById<TextView>(R.id.titre).text = "Modifier la tâche"

            // We add values in the spinner
            val spinnerColor = dialog.findViewById<Spinner>(R.id.BtnColor)

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

            // We change the selected item to the color of the task
            color_items.forEachIndexed() { index, color ->
                if (color.text == task.color) {
                    spinnerColor.setSelection(index)
                }

            }

            val inputName = dialog.findViewById<TextView>(R.id.InputName)
            val inputDesc = dialog.findViewById<TextView>(R.id.InputDesc)

            inputName.text = task.name
            inputDesc.text = task.description

            val checkBoxMon = dialog.findViewById<CheckBox>(R.id.CheckMon)
            val checkBoxTue = dialog.findViewById<CheckBox>(R.id.CheckTue)
            val checkBoxWed = dialog.findViewById<CheckBox>(R.id.CheckWed)
            val checkBoxThu = dialog.findViewById<CheckBox>(R.id.CheckThu)
            val checkBoxFri = dialog.findViewById<CheckBox>(R.id.CheckFri)
            val checkBoxSat = dialog.findViewById<CheckBox>(R.id.CheckSat)
            val checkBoxSun = dialog.findViewById<CheckBox>(R.id.CheckSun)

            task.days.forEach(){ day, value->
                if (value == true){
                    if(day == "monday") checkBoxMon.isChecked = true
                    if(day == "tuesday") checkBoxTue.isChecked = true
                    if(day == "wednesday") checkBoxWed.isChecked = true
                    if(day == "thursday") checkBoxThu.isChecked = true
                    if(day == "friday") checkBoxFri.isChecked = true
                    if(day == "saturday") checkBoxSat.isChecked = true
                    if(day == "sunday") checkBoxSun.isChecked = true
                }
            }

            val arrayCheckBoxs = listOf(checkBoxMon, checkBoxTue, checkBoxWed, checkBoxThu, checkBoxFri, checkBoxSat, checkBoxSun)

            // Pour chaque checkbox on récupère la valeur et on l'ajoute dans le dictionnaire
            arrayCheckBoxs.forEach { checkBox->
                checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    task.days[checkBox.tag.toString()] = isChecked
                }
            }

            dialog.findViewById<Button>(R.id.Add).setOnClickListener(){

                // Récupérer les infos changées
                // Tableau pour parcourir toutes les checkboxs
                println(task.days)

                task.name = inputName.text.toString()
                task.description = inputDesc.text.toString()
                task.color = color_items[spinnerColor.selectedItemPosition].text

                // Mettre à jour les infos dans la bdd
                val infoTask = mapOf(
                    "name" to task.name,
                    "description" to task.description,
                    "color" to task.color,
                    //"validate" to task.validate,
                    "days" to task.days
                )

                val childUpdate = hashMapOf<String, Any>(
                    "/users/$userID/tasks/${task.key}" to infoTask
                )

                database.updateChildren(childUpdate)
                    .addOnSuccessListener {
                        Log.d("BDD", "Update de la task avec succès !")
                    }
                    .addOnFailureListener { exception ->
                        Log.d("BDD","Erreur lors de l'update : ${exception.message}")
                    }
                dialog.dismiss()
            }

            dialog.findViewById<ImageButton>(R.id.Supp).setOnClickListener(){
                // Supprimer la tâche dans la base de donnée
                database.child("users").child("$userID").child("tasks").child(task.key).removeValue()
                    .addOnSuccessListener {
                        Log.d("BDD", "Delete de la task avec succès !")
                    }
                    .addOnFailureListener { exception ->
                        Log.d("BDD","Erreur lors de la suppression : ${exception.message}")
                    }

                dialog.dismiss()
            }


            dialog.show()
        }
    }

    fun affichageTaskDay(day: String, userUid: String?, listView: ListView){
        database.child("users").child(userUid.toString()).child("tasks").orderByChild("days/$day").equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataArrayList.clear()

                    for (taskSnapshot in dataSnapshot.children) {
                        // Récupère les informations de chaque tâche du jour

                        val taskKey = taskSnapshot.key.toString()
                        val taskName = taskSnapshot.child("name").value.toString()
                        val taskType = taskSnapshot.child("type").value.toString()
                        val taskDesc = taskSnapshot.child("description"). value.toString()
                        val taskColor = taskSnapshot.child("color").value.toString()

                        val taskMon = taskSnapshot.child("days").child("monday").value.toString()
                        val taskTue = taskSnapshot.child("days").child("tuesday").value.toString()
                        val taskWed = taskSnapshot.child("days").child("wednesday").value.toString()
                        val taskThu = taskSnapshot.child("days").child("thursday").value.toString()
                        val taskFri = taskSnapshot.child("days").child("friday").value.toString()
                        val taskSat = taskSnapshot.child("days").child("saturday").value.toString()
                        val taskSun = taskSnapshot.child("days").child("sunday").value.toString()
                        val taskValid = taskSnapshot.child("validate").value.toString() == "true"

                        var list_Days = mutableMapOf(
                            "monday" to (taskMon == "true"),
                            "tuesday" to (taskTue == "true"),
                            "wednesday" to (taskWed == "true"),
                            "thursday" to (taskThu == "true"),
                            "friday" to (taskFri == "true"),
                            "saturday" to (taskSat == "true"),
                            "sunday" to (taskSun == "true")
                        )

                        // Création d'une tache à l'aide de la classe Task
                        listData = Task(taskKey,taskName, taskDesc,  taskColor, list_Days, taskValid)
                        dataArrayList.add(listData)
                    }

                    val calendarCheck = false
                    // Add of all the today task
                    listAdapter = ListAdapter(requireContext(), dataArrayList, userUid, calendarCheck)
                    listView.adapter = listAdapter
                    listView.isClickable = true

                    listView.setOnItemClickListener { parent, view, position, id ->
                        val selectedItem = dataArrayList[position]
                        if(selectedItem != null){
                            selectedItem.validate = if(selectedItem.validate) false else true
                            view.findViewById<CheckBox>(R.id.validTask).isChecked = selectedItem.validate
                            val nameTask = view.findViewById<TextView>(R.id.textNameTask)
                            if(selectedItem.validate){
                                nameTask.paintFlags = nameTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            } else{
                                nameTask.paintFlags = nameTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                            }
                        }
                        true
                    }

                    // Event listener for update tasks
                    listView.setOnItemLongClickListener { parent, view, position, id ->
                        val selectedItem = dataArrayList[position]
                        updateTask(selectedItem, userUid)
                        true
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Erreur lors de la récupération des tâches : ${databaseError.message}")
                }
            })
    }
}