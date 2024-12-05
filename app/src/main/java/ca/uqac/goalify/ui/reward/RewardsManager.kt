package ca.uqac.goalify.ui.reward

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object RewardsManager {
    private val _rewardsLiveData = MutableLiveData<List<Reward>>()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var allRewards: List<Reward> = emptyList()

    fun initialize() {
        loadRewardsFromFirestore()
        fetchUserRewards()
    }

    private fun loadRewardsFromFirestore() {
        firestore.collection("rewards").get().addOnSuccessListener { documents ->
            val rewards = documents.map { document ->
                val id = document.id.toInt()
                val title = document.getString("title") ?: ""
                val subtitle = document.getString("subtitle") ?: ""
                val imageUrl = document.getString("imageUrl") ?: ""
                val isUnlocked = document.getBoolean("isUnlocked") ?: false
                val isHidden = document.getBoolean("isHidden") ?: false
                Reward(id, title, subtitle, imageUrl, isUnlocked, isHidden)
            }
            allRewards = rewards
            _rewardsLiveData.value = rewards
        }
    }

    private fun fetchUserRewards() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("rewards")
            .get().addOnSuccessListener { documents ->
                val userRewardsMap = documents.associate { document ->
                    val rewardId = document.id.toInt()
                    val unlocked = document.getBoolean("unlocked") ?: false
                    val isVisible = document.getBoolean("isVisible") ?: false
                    rewardId to Pair(unlocked, isVisible)
                }
                val updatedRewards = allRewards.map { reward ->
                    val userReward = userRewardsMap[reward.id]
                    if (userReward != null) {
                        reward.copy(isUnlocked = userReward.first, isHidden = !userReward.second)
                    } else {
                        reward
                    }
                }
                _rewardsLiveData.value = updatedRewards
            }
    }

    fun getRewards(): LiveData<List<Reward>> = _rewardsLiveData

    fun unlockReward(id: Int) {
        val userId = auth.currentUser?.uid ?: return
        val reward = allRewards.find { it.id == id }
        if (reward != null) {
            firestore.collection("users").document(userId).collection("rewards")
                .document(id.toString()).set(mapOf("unlocked" to true, "isVisible" to true))
            fetchUserRewards()
        }
    }

    fun isRewardUnlocked(id: Int): Boolean {
        return _rewardsLiveData.value?.find { it.id == id }?.isUnlocked == true
    }

    fun checkAndUnlockRewards(userId: String, rewardId: Int, context: Context) {
        val rewardsToUnlock = mutableListOf<Int>()

        // Exemple de condition pour débloquer une récompense
        if (rewardId > 0) {
            rewardsToUnlock.add(rewardId) // ID de la récompense à débloquer
        }

        if (rewardsToUnlock.isNotEmpty()) {
            unlockRewards(userId, rewardsToUnlock, context)
        }
    }

    private fun unlockRewards(userId: String, rewardIds: List<Int>, context: Context) {
        val userRef = firestore.collection("users").document(userId).collection("rewards")

        rewardIds.forEach { rewardId ->
            userRef.document(rewardId.toString()).set(mapOf("unlocked" to true, "isVisible" to true))
                .addOnSuccessListener {
                    showRewardUnlockedNotification(rewardId, context)
                }
        }
    }

    private fun showRewardUnlockedNotification(rewardId: Int, context: Context) {
        Toast.makeText(context, "Récompense débloquée : n°$rewardId", Toast.LENGTH_SHORT).show()
    }
}
