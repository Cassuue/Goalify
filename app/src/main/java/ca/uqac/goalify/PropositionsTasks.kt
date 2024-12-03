package ca.uqac.goalify

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.navigation.fragment.findNavController


class PropositionsTasks : Fragment() {

    var dataArrayList = ArrayList<String>()
    private lateinit var listAdapter: ListAdapterPropositions


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_propositions_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataArrayList.add("Aller faire les courses")
        dataArrayList.add("Prendre une pause")
        dataArrayList.add("Apprendre une langue étrangère")
        dataArrayList.add("Apprendre à jouer d'un instrument")
        dataArrayList.add("Aller faire du sport")
        dataArrayList.add("Lire un livre")
        dataArrayList.add("Sortir prendre l'air")
        dataArrayList.add("Contacter la famille")
        dataArrayList.add("Limiter le temps sur les réseaux sociaux")
        dataArrayList.add("Faire le ménage")

        listAdapter = ListAdapterPropositions(requireContext(), dataArrayList)

        dataArrayList.forEach() { objet ->
            Log.d("Proposition", objet)
        }

        val listView = view.findViewById<ListView>(R.id.listviewPropositions)

        listView.adapter = listAdapter
        listView.isClickable = true

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = dataArrayList[position]
            val bundle = Bundle().apply {
                putString("taskTitle", selectedItem)
            }
            findNavController().navigate(R.id.navigation_add_task, bundle)
        }

        val button_next = view.findViewById<Button>(R.id.skip)

        button_next.setOnClickListener(){
            // Appel au fragment add Task sans champs remplis
            findNavController().navigate(R.id.navigation_add_task)
        }

    }

}