package ca.uqac.goalify.ca.uqac.goalify

data class Reward(
    val id: Int,
    var title: String,
    var subtitle: String,
    val imageUrl: String,
    var isUnlocked: Boolean = false
)
