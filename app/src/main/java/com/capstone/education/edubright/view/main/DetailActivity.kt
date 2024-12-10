package com.capstone.education.edubright.view.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.capstone.education.edubright.R
import com.capstone.education.edubright.databinding.ActivityDetailBinding
import kotlin.random.Random

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var pieChart: AnyChartView

    // Dummy sentiment data (initial values)
    private val sentiments = mutableMapOf(
        "Awesome" to Random.nextInt(0, 50),
        "Good" to Random.nextInt(0, 50),
        "Others" to Random.nextInt(0, 50)
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

        displaySentimentData()

        binding.btnFeedback.setOnClickListener {
            val feedback = binding.etFeedback.text.toString()
            if (feedback.isNotBlank()) {
                updateSentimentDataRandomly()
            } else {
                Toast.makeText(this, "Feedback cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        supportActionBar?.apply {
            title = getString(R.string.detail)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun updateSentimentDataRandomly() {
        val total = Random.nextInt(1, 100)
        sentiments["Awesome"] = Random.nextInt(0, total)
        sentiments["Good"] = Random.nextInt(0, total - sentiments["Awesome"]!!)
        sentiments["Others"] = total - sentiments["Awesome"]!! - sentiments["Good"]!!

        Log.d("SentimentData", sentiments.toString()) // Menampilkan data sentiment baru
        displaySentimentData()
        Toast.makeText(this, "Feedback recorded and sentiments updated", Toast.LENGTH_SHORT).show()
    }


    private fun displaySentimentData() {
        val total = sentiments.values.sum().toFloat()
        val dataEntries = sentiments.map { (label, count) ->
            ValueDataEntry(label, (count / total) * 100)
        }
        val pie = AnyChart.pie()
        pie.data(dataEntries)
        pie.title("Sentiment Analysis")
        pie.labels().position("outside")
        pieChart.setChart(pie)

        // Update percentage labels
        binding.tvAwesomePercentage.text = "Awesome: ${(sentiments["Awesome"]?.toFloat()?.div(total)?.times(100)?.toInt() ?: 0)}%"
        binding.tvGoodPercentage.text = "Good: ${(sentiments["Good"]?.toFloat()?.div(total)?.times(100)?.toInt() ?: 0)}%"
        binding.tvOtherPercentage.text = "Others: ${(sentiments["Others"]?.toFloat()?.div(total)?.times(100)?.toInt() ?: 0)}%"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
