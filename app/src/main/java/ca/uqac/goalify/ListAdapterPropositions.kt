package ca.uqac.goalify

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ListAdapterPropositions (context: Context, dataArrayList: ArrayList<String>) :
    ArrayAdapter<String>(context, R.layout.list_view_proposition_tasks, dataArrayList){

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {

        var view = view

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_view_proposition_tasks, parent, false)

        }

        val nameTask = view!!.findViewById<TextView>(R.id.propositionName)

        nameTask.text = getItem(position)

        return view
    }
}