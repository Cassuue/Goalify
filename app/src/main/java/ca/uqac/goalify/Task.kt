package ca.uqac.goalify

class Task (
    var key: String,
    var name: String,
    var description: String,
    var color: String,
    var type: String,
    var days: MutableMap<String, Boolean>,
    var validate: Boolean
)