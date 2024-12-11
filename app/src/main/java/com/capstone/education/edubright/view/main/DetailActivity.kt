package com.capstone.education.edubright.view.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.capstone.education.edubright.R
import com.capstone.education.edubright.data.UserRepository
import com.capstone.education.edubright.data.pref.UserPreference
import com.capstone.education.edubright.data.response.FeedbackStatistics
import com.capstone.education.edubright.data.retrofit.ApiConfig
import com.capstone.education.edubright.data.room.SentimentDatabase
import com.capstone.education.edubright.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch
import com.capstone.education.edubright.data.pref.Result

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var userRepository: UserRepository
    private lateinit var pieChart: AnyChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titleRes = intent.getIntExtra("TITLE_RES", -1)
        val authorRes = intent.getIntExtra("AUTHOR_RES", -1)
        val descriptionRes = intent.getIntExtra("DESCRIPTION_RES", -1)
        val imageRes = intent.getIntExtra("IMAGE_RES", -1)

        if (titleRes != -1 && authorRes != -1 && descriptionRes != -1 && imageRes != -1) {
            displayData(titleRes, authorRes, descriptionRes, imageRes)
        } else {
            Toast.makeText(this, "Data not available", Toast.LENGTH_SHORT).show()
        }
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(this)
        val sentimentDao = SentimentDatabase.getInstance(this).sentimentDao()
        userRepository = UserRepository.getInstance(userPreference, apiService, sentimentDao)

        setupUI()
        loadFeedbackStatistics()
    }

    private fun displayData(titleRes: Int, authorRes: Int, descriptionRes: Int, imageRes: Int) {
        val context = this
        binding.tvCourseTitle.text = context.getString(titleRes)
        binding.tvInstructor.text = context.getString(authorRes)
        binding.tvDescription.text = context.getString(descriptionRes)
        binding.ivCourseImage.setImageResource(imageRes)
    }

    private fun setupUI() {
        pieChart = binding.ivSentimentGraph

        binding.btnFeedback.setOnClickListener {
            val userFeedback = binding.etFeedback.text.toString()
            if (userFeedback.isNotBlank()) {
                lifecycleScope.launch {
                    userRepository.getUserId().collect { userId ->
                        val feedbackValue = determineSentiment(userFeedback)
                        postComment(userId, "$userFeedback (Sentiment: $feedbackValue)") {
                            loadFeedbackStatistics()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Feedback cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }


        supportActionBar?.apply {
            title = getString(R.string.detail)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun loadFeedbackStatistics() {
        lifecycleScope.launch {
            userRepository.getFeedbackStatistics().collect { result ->
                when (result) {
                    is Result.Loading -> {
                    }
                    is Result.Success -> {
                        displaySentimentData(result.data.statistics)
                    }
                    is Result.Error -> {
                        Toast.makeText(this@DetailActivity, result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun postComment(userId: String, commentText: String, onSuccess: (() -> Unit)? = null) {
        Log.d("PostCommentData", "userId: $userId, commentText: $commentText")

        lifecycleScope.launch {
            userRepository.postComment(userId, commentText).collect { result ->
                when (result) {
                    is Result.Loading -> {
                    }
                    is Result.Success -> {
                        Toast.makeText(
                            this@DetailActivity,
                            "Feedback submitted successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        onSuccess?.invoke()
                    }
                    is Result.Error -> {
                        Log.e("PostCommentError", "Error: ${result.error}")
                        Toast.makeText(this@DetailActivity, result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun displaySentimentData(statistics: List<FeedbackStatistics>) {
        val dataEntries = statistics.map {
            ValueDataEntry(it.feedback, it.percentage.toFloat())
        }

        val pie = AnyChart.pie()
        pie.data(dataEntries)
        pie.title("Sentiment Analysis")
        pie.labels().position("outside")
        pieChart.setChart(pie)

        statistics.forEach {
            when (it.feedback) {
                "Awesome" -> binding.tvAwesomePercentage.text = "Awesome: ${it.percentage}%"
                "Good" -> binding.tvGoodPercentage.text = "Good: ${it.percentage}%"
                "Neutral" -> binding.tvNeutralPercentage.text = "Neutral: ${it.percentage}%"
                "Poor" -> binding.tvPoorPercentage.text = "Poor: ${it.percentage}%"
                "Awful" -> binding.tvAwfulPercentage.text = "Awful: ${it.percentage}%"
            }
        }
    }

    private fun determineSentiment(feedback: String): String {
        return when {
            feedback.contains("awesome", ignoreCase = true) -> "Awesome"
            feedback.contains("good", ignoreCase = true) -> "Good"
            feedback.contains("neutral", ignoreCase = true) -> "Neutral"
            feedback.contains("poor", ignoreCase = true) -> "Poor"
            else -> "Awful"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
