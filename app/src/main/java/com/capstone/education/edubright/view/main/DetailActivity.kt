package com.capstone.education.edubright.view.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.capstone.education.edubright.R
import com.capstone.education.edubright.data.UserRepository
import com.capstone.education.edubright.data.pref.UserPreference
import com.capstone.education.edubright.data.response.PredictResponse
import com.capstone.education.edubright.data.retrofit.ApiConfig
import com.capstone.education.edubright.data.room.SentimentDatabase
import com.capstone.education.edubright.databinding.ActivityDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var pieChart: AnyChartView

    // Sentiment data dummy
    private val sentiments = mapOf(
        "Awesome" to 40,
        "Good" to 35,
        "Others" to 25
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titleRes = intent.getIntExtra("TITLE_RES", 0)
        val authorRes = intent.getIntExtra("AUTHOR_RES", 0)
        val descriptionRes = intent.getIntExtra("DESCRIPTION_RES", 0)
        val imageRes = intent.getIntExtra("IMAGE_RES", 0)

        binding.tvCourseTitle.text = getString(titleRes)
        binding.tvInstructor.text = getString(authorRes)
        binding.tvDescription.text = getString(descriptionRes)
        binding.ivCourseImage.setImageResource(imageRes)

        pieChart = binding.ivSentimentGraph

        // Menampilkan sentiment chart awal
        displaySentimentData()

        binding.btnFeedback.setOnClickListener {
            val feedback = binding.etFeedback.text.toString()
            if (feedback.isNotBlank()) {
                // Simulasikan sentiment berdasarkan feedback, misalnya menggunakan data dummy
                Toast.makeText(this, "Feedback: $feedback", Toast.LENGTH_SHORT).show()

                // Update Sentiment Chart (Simulasi dengan data dummy)
                displaySentimentData()
            } else {
                Toast.makeText(this, "Please enter feedback", Toast.LENGTH_SHORT).show()
            }
        }

        supportActionBar?.apply {
            title = getString(R.string.detail)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun displaySentimentData() {
        val total = sentiments.values.sum().toFloat()

        // Membuat entry data untuk pie chart
        val dataEntries = sentiments.map { (label, count) ->
            ValueDataEntry(label, (count / total) * 100)
        }

        // Membuat dan menampilkan pie chart
        val pie = AnyChart.pie()
        pie.data(dataEntries)
        pie.title("Sentiment Analysis")
        pie.labels().position("outside")
        pieChart.setChart(pie)

        // Update persentase sentimen di TextView
        binding.tvAwesomePercentage.text = "Awesome: ${sentiments["Awesome"]?.toFloat()?.div(total)?.times(100)?.toInt() ?: 0}%"
        binding.tvGoodPercentage.text = "Good: ${sentiments["Good"]?.toFloat()?.div(total)?.times(100)?.toInt() ?: 0}%"
        binding.tvOtherPercentage.text = "Others: ${sentiments["Others"]?.toFloat()?.div(total)?.times(100)?.toInt() ?: 0}%"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
