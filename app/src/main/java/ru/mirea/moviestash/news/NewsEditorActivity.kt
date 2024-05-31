package ru.mirea.moviestash.news

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.ActivityNewsEditorBinding
import ru.mirea.moviestash.entites.News
import ru.mirea.moviestash.network.ImgurResponse
import ru.mirea.moviestash.network.WebClient
import java.io.File
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException

class NewsEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsEditorBinding
    private var imageLink: String? = null
    private lateinit var news: News
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let {
                    val imageUri = it
                    binding.imageNews.setImageURI(imageUri)
                    getFileFromUri(imageUri)?.let { file ->
                        val filePart = MultipartBody.Part.createFormData(
                            "image", file.name, RequestBody.create(MediaType.parse("image/*"), file)
                        )
                        val resp = WebClient.imgurAPI.postImage(filePart)
                        resp.enqueue(object : Callback<ImgurResponse> {
                            override fun onResponse(
                                call: Call<ImgurResponse>, response: Response<ImgurResponse>
                            ) {
                                response.body()?.let { r ->
                                    imageLink = r.getImageLink().toString()
                                    binding.saveNewsBtn.isEnabled = true
                                }
                                if (!response.isSuccessful) {
                                    Toast.makeText(
                                        this@NewsEditorActivity,
                                        "Не удалось загрузить изображение!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.saveNewsBtn.isEnabled = true
                                }
                            }

                            override fun onFailure(call: Call<ImgurResponse>, t: Throwable) {
                                Toast.makeText(
                                    this@NewsEditorActivity,
                                    "Не удалось загрузить изображение!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.saveNewsBtn.isEnabled = true
                            }

                        })

                    }
                }
            } else binding.saveNewsBtn.isEnabled = true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindListeners()
        intent.getParcelableExtra<News>("NEW")?.let {
            news = it
            binding.news = news
            news.image?.let { link ->
                imageLink = link
                lifecycleScope.launch {
                    var bmp: Bitmap? = null
                    try {
                        withContext(Dispatchers.IO) {
                            bmp = BitmapFactory.decodeStream(
                                URL(link).openConnection().getInputStream()
                            )
                        }
                    } catch (e: UnknownHostException) {
                        Log.d("DEBUG", e.stackTraceToString())
                    } catch (e: ConnectException) {
                        Toast.makeText(
                            this@NewsEditorActivity,
                            "Не удалось получить изображения!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    bmp?.let { b ->
                        binding.imageNews.setImageBitmap(b)
                    }
                }
            }
        }
    }

    private fun bindListeners() {
        binding.imageNews.setOnClickListener {
            binding.saveNewsBtn.isEnabled = false
            pickImage.launch(
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            )
        }
        binding.saveNewsBtn.setOnClickListener {
            val title = binding.headerNews.text.toString().trim()
            val desc = binding.descriptionNews.text.toString().trim()
            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Заполните необходимые поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                    is Result.Success<Boolean> -> {
                        if (!result.data) {
                            Toast.makeText(
                                this@NewsEditorActivity,
                                "Ошибка сетевого запроса",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@NewsEditorActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                val result: Result<Boolean> =
                    if (::news.isInitialized) DatabaseController.addModNews(
                        title, desc, news.id, imageLink
                    )
                    else DatabaseController.addModNews(title, desc, 0, imageLink)
                when (result) {
                    is Result.Success<Boolean> -> {
                        finish()
                    }

                    is Result.Error -> Toast.makeText(
                        this@NewsEditorActivity, result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return this.contentResolver.query(
            uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null
        )?.use { cursor ->
            cursor.moveToFirst()
            cursor.getString(0)?.let { file -> File(file) }
        }
    }
}