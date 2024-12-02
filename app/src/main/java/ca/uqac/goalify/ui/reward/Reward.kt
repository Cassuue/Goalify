package ca.uqac.goalify.ui.reward

data class Reward(
    val id: Int,
    var title: String,
    var subtitle: String,
    val imageUrl: String,
    var isUnlocked: Boolean = false,
    var isHidden: Boolean = false
)
