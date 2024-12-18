package ca.uqac.goalify.ui.reward

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.uqac.goalify.R

class RewardsFragment : Fragment() {

    private lateinit var unlockedAdapter: RewardAdapter
    private lateinit var lockedAdapter: RewardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rewards, container, false)

        val unlockedRecyclerView: RecyclerView = view.findViewById(R.id.unlocked_achievements_recycler)
        val lockedRecyclerView: RecyclerView = view.findViewById(R.id.locked_achievements_recycler)

        unlockedAdapter = RewardAdapter(requireContext(), listOf())
        lockedAdapter = RewardAdapter(requireContext(), listOf())

        unlockedRecyclerView.adapter = unlockedAdapter
        unlockedRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        lockedRecyclerView.adapter = lockedAdapter
        lockedRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialiser le RewardsManager avec le contexte
        RewardsManager.initialize()

        // Observer les changements dans les récompenses
        RewardsManager.getRewards().observe(viewLifecycleOwner, Observer { rewards ->
            val unlockedRewards = rewards.filter { it.isUnlocked }
            val lockedRewards = rewards.filter { !it.isUnlocked && !it.isHidden }

            unlockedAdapter.updateData(unlockedRewards)
            lockedAdapter.updateData(lockedRewards)
        })

        return view
    }
}
