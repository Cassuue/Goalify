package ca.uqac.goalify

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PropositionsTasks : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private var dataArrayList = ArrayList<String>()
    private lateinit var listAdapter: ListAdapterPropositions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_propositions_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        val userUid = currentUser?.uid

        if (userUid != null) {
            fetchUserObjective(userUid)
        }

        val listView = view.findViewById<ListView>(R.id.listviewPropositions)
        listAdapter = ListAdapterPropositions(requireContext(), dataArrayList)
        listView.adapter = listAdapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = dataArrayList[position]
            val bundle = Bundle().apply {
                putString("taskTitle", selectedItem)
            }
            findNavController().navigate(R.id.navigation_add_task, bundle)
        }

        val buttonNext = view.findViewById<Button>(R.id.skip)
        buttonNext.setOnClickListener {
            findNavController().navigate(R.id.navigation_add_task)
        }
    }

    private fun fetchUserObjective(userUid: String) {
        database.collection("users").document(userUid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userObjective = document["objective"].toString()
                    val aiEnabled = document["aiEnabled"] as? Boolean == true
                    val context = requireActivity()

                    dataArrayList.clear()
                    if (aiEnabled) {
                        GroqApi.generateTaskSuggestions(context, userObjective) { suggestions ->
                            suggestions.forEach { suggestion ->
                                Log.d("PropositionsTasks", "Suggestion: ${suggestion.description}")
                                dataArrayList.add(suggestion.titre)
                                //TODO: pour ajouter les descriptions des tâches, l'ia les fournit dans suggestions.description
                            }
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
                            listAdapter.notifyDataSetChanged()
                        }
                    } else {
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
                        listAdapter.notifyDataSetChanged()
                    }
                }
        }.addOnFailureListener {
            Log.e("PropositionsTasks", "Failed to fetch user objective: ${it.message}")
        }
    }
}
