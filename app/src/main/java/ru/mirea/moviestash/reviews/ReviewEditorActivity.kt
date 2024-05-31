package ru.mirea.moviestash.reviews

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.ActivityReviewEditorBinding
import ru.mirea.moviestash.entites.Review

class ReviewEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewEditorBinding
    private lateinit var title: EditText
    private lateinit var description: EditText
    private var filmID = -1
    private lateinit var review: Review

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindViews()
        bindListeners()
        filmID = intent.getIntExtra("CID", -1)
        intent.getParcelableExtra<Review>("RV")?.let {
            review = it
            binding.headerEd.setText(it.title)
            description.setText(it.description)
            binding.opSpinner.setSelection(it.opinion - 1)
        }
    }

    private fun bindViews() {
        title = binding.headerEd
        description = binding.descriptionEd
        description.movementMethod = ScrollingMovementMethod()
    }

    private fun bindListeners() {
        binding.saveReviewBtn.setOnClickListener {
            val titleText = title.text.toString().trim()
            val descText = description.text.toString().trim()
            val opinion = binding.opSpinner.selectedItemPosition + 1
            if (titleText.isEmpty() || descText.isEmpty()) {
                Toast.makeText(this, "Не заполнены необходимые поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (filmID == -1 && !::review.isInitialized) {
                Toast.makeText(this, "Непредвиденная ошибка!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                    is Result.Success<Boolean> -> {
                        if (!result.data) {
                            Toast.makeText(
                                this@ReviewEditorActivity,
                                "Ошибка сетевого запроса",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@ReviewEditorActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                val result: Result<Boolean> =
                    if (::review.isInitialized) DatabaseController.addModReview(
                        titleText, descText, review.id, opinion, true
                    )
                    else DatabaseController.addModReview(
                        titleText, descText, filmID, opinion, false
                    )
                when (result) {
                    is Result.Success<Boolean> -> {
                        setResult(RESULT_OK)
                        finish()
                    }

                    is Result.Error -> Toast.makeText(
                        this@ReviewEditorActivity, result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}