package ca.uqac.goalify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ca.uqac.goalify.ca.uqac.goalify.Reward

object RewardsManager {
    private val _rewards = mutableListOf<Reward>()
    private val _rewardsLiveData = MutableLiveData<List<Reward>>()

    init {
        // Initialisation des récompenses avec des données de démonstration
        _rewards.add(Reward(1, "Marathon Finisher", "Gold Medal\nCity Marathon • Winner", "https://cdn.pixabay.com/photo/2024/06/28/07/14/sprint-8858775_640.jpg", isUnlocked = true))
        _rewards.add(Reward(2, "Top Scorer", "Silver Medal\nBasketball League • Winner", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSQ8NnghqBK3m_oL06lzCimFTVsMTtkB-NZjA&s"))
        _rewards.add(Reward(3, "Chess Champion", "Bronze Medal\nChess Club • Nominee", "https://images.pexels.com/photos/260024/pexels-photo-260024.jpeg"))
        _rewards.add(Reward(4, "Secret Reward 1", "Details hidden", "https://png.pngtree.com/png-vector/20230423/ourmid/pngtree-secret-stamp-design-vector-png-image_6719082.png", isUnlocked = false))
        _rewards.add(Reward(5, "Secret Reward 2", "Details hidden", "https://png.pngtree.com/png-vector/20230423/ourmid/pngtree-secret-stamp-design-vector-png-image_6719082.png", isUnlocked = false))

        _rewardsLiveData.value = _rewards
    }

    fun getRewards(): LiveData<List<Reward>> = _rewardsLiveData

    fun unlockReward(id: Int) {
        _rewards.find { it.id == id }?.let {
            it.isUnlocked = true
            // Mise à jour de la description si c'était un secret
            if (it.title.startsWith("Secret")) {
                it.title = it.title.replace("Secret ", "")
                it.subtitle = "Unlocked: Details now visible"
            }
            _rewardsLiveData.value = _rewards // Notifie les observateurs des changements
        }
    }

    fun isRewardUnlocked(id: Int): Boolean {
        return _rewards.find { it.id == id }?.isUnlocked == true
    }
}
