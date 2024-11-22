package ca.uqac.goalify

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class ListAdapter(context: Context, dataArrayList: ArrayList<Task?>?,
                  private val userID: String?, private val calendar: Boolean) :

    ArrayAdapter<Task?>(context, R.layout.list_view_tasks, dataArrayList!!) {

    private lateinit var database: DatabaseReference

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {

        database = Firebase.database.reference

        var view = view
        val listData = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_view_tasks, parent, false)
        }
        val nameTask = view!!.findViewById<TextView>(R.id.textNameTask)
        val descTask = view.findViewById<TextView>(R.id.textDesc)
        val colorItem = view.findViewById<ImageView>(R.id.colorItem)

        if (listData != null) {
            nameTask.text = listData.name
            descTask.text = listData.description
            val color = when (listData.color) {
                "blue" -> R.drawable.color_item_blue
                "green" -> R.drawable.color_item_green
                "orange" -> R.drawable.color_item_orange
                "purple" -> R.drawable.color_item_purple
                "yellow" -> R.drawable.color_item_yellow
                else -> R.drawable.color_item_red
            }
            colorItem.setImageResource(color)
        }

        val checkBox = view.findViewById<CheckBox>(R.id.validTask)

        if(calendar){
            checkBox.visibility = View.GONE
        } else{
            checkBox.visibility = View.VISIBLE
        }

        return view
    }
}
