package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.AptitudeQuestion
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAptitudeGenerator {
    private const val TAG = "GeminiAptitude"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateIndiaBixQuestion(topic: String, category: String): AptitudeQuestion? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "No valid Gemini API key provided. Using local IndiaBIX curriculum bank.")
            return@withContext null
        }

        try {
            val prompt = """
                You are an expert examiner creating questions strictly adhering to the standard IndiaBIX $category syllabus.
                Topic: $topic.
                Generate exactly ONE multiple-choice aptitude question with 4 distinct options (A, B, C, D).
                Return strictly valid JSON with this exact schema (no markdown, no backticks, only raw JSON):
                {
                  "question": "Clear problem statement",
                  "options": ["Option A", "Option B", "Option C", "Option D"],
                  "correctIndex": 0,
                  "explanation": "Concise step-by-step mathematical or logical solution using standard IndiaBIX formulas."
                }
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.4)
                    put("responseMimeType", "application/json")
                })
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: return@withContext null

            val rootJson = JSONObject(responseString)
            val candidates = rootJson.optJSONArray("candidates") ?: return@withContext null
            if (candidates.length() == 0) return@withContext null

            val content = candidates.getJSONObject(0).getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val text = parts.getJSONObject(0).getString("text")

            val parsedJson = JSONObject(text.trim())
            val questionText = parsedJson.getString("question")
            val optionsJson = parsedJson.getJSONArray("options")
            val optionsList = mutableListOf<String>()
            for (i in 0 until optionsJson.length()) {
                optionsList.add(optionsJson.getString(i))
            }
            val correctIndex = parsedJson.getInt("correctIndex")
            val explanation = parsedJson.getString("explanation")

            AptitudeQuestion(
                id = "ai_${System.currentTimeMillis()}",
                topic = topic,
                question = questionText,
                options = optionsList,
                correctIndex = correctIndex.coerceIn(0, 3),
                explanation = explanation,
                difficulty = "Dynamic (AI IndiaBIX)"
            )
        } catch (e: Exception) {
            Log.w(TAG, "Gemini API generation error: ${e.message}. Gracefully falling back to curriculum repository.")
            null
        }
    }
}
