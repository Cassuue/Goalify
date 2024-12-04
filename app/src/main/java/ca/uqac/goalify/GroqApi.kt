package ca.uqac.goalify

import android.app.Activity
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

data class TaskSuggestion(val titre: String, val description: String)
data class SuggestionsResponse(val suggestions: List<TaskSuggestion>)

object GroqApi {
    private const val API_KEY = "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"
    private const val BASE_URL = "https://api.groq.com/openai/v1/chat/completions"

    private val client = OkHttpClient()
    private val gson = Gson()

    fun generateTaskSuggestions(context: Activity, userObjective: String, callback: (List<TaskSuggestion>) -> Unit) {
        val requestBody = gson.toJson(mapOf(
            "model" to "llama-3.1-70b-versatile",
            "messages" to listOf(
                mapOf(
                    "role" to "system",
                    "content" to "Vous êtes un assistant IA. Votre réponse doit être un objet JSON avec une clé 'suggestions' contenant un tableau de suggestions de tâches. Les tâches doivent avoir uniquement un titre et une description."
                ),
                mapOf(
                    "role" to "user",
                    "content" to "Mon objectif est : $userObjective. Veuillez suggérer des tâches qui peuvent m'aider à atteindre cet objectif."
                )
            ),
            "response_format" to mapOf("type" to "json_object")
        ))

        val request = Request.Builder()
            .url(BASE_URL)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody))
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GroqApi", "Failed to generate task suggestions", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val type = object : TypeToken<Map<String, Any>>() {}.type
                    val responseMap = gson.fromJson<Map<String, Any>>(responseBody, type)
                    val choices = responseMap["choices"] as? List<Map<String, Any>>
                    val messageContent = choices?.firstOrNull()?.get("message") as? Map<*, *>?
                    val content = messageContent?.get("content") as? String

                    val suggestionsResponse = gson.fromJson(content, SuggestionsResponse::class.java)
                    val suggestions = suggestionsResponse.suggestions

                    context.runOnUiThread {
                        callback(suggestions)
                    }
                } else {
                    Log.e("GroqApi", "Failed to generate task suggestions: ${response}")
                }
            }
        })
    }
}