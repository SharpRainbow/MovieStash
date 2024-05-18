package ru.mirea.moviestash.user_management

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.ActivityRegisterBinding
import ru.mirea.moviestash.entites.Credentials

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var nameIn: EditText
    private lateinit var emailIn: EditText
    private lateinit var loginIn: EditText
    private lateinit var passIn: EditText
    private val dao by lazy {
        CredentialsDatabase.getDatabase(this@RegisterActivity).credsDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindViews()
        bindListeners()
        DatabaseController.user?.let {
            binding.person = it
        }
    }

    private fun bindViews() {
        nameIn = binding.nameEd
        emailIn = binding.emailEd
        loginIn = binding.loginEd
        passIn = binding.passEd
    }

    private fun bindListeners() {
        binding.registerUser.setOnClickListener {
            val name = nameIn.text.toString()
            val email = emailIn.text.toString()
            val login = loginIn.text.toString()
            val pass = passIn.text.toString()
            if (((name.isEmpty() || email.isEmpty() || login.isEmpty() || pass.isEmpty()) && DatabaseController.user == null) || ((name.isEmpty() || email.isEmpty()) && DatabaseController.user != null)) {
                Toast.makeText(this, "Заполните необходимые поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                    is Result.Success<Boolean> -> {
                        if (!result.data) {
                            Toast.makeText(
                                this@RegisterActivity, "Ошибка сетевого запроса", Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@RegisterActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                var credentials: Credentials? = null
                val msg = if (DatabaseController.user != null) {
                    withContext(Dispatchers.IO) {
                        dao.getByLogin(DatabaseController.user!!.login).let { credList ->
                            if (credList.isNotEmpty()) credentials = credList.first()
                        }
                    }
                    DatabaseController.modUserData(name, email)
                } else DatabaseController.registerNewUser(login, pass, name, email)
                when (msg) {
                    is Result.Success<Boolean> -> {
                        credentials?.let { cred ->
                            cred.email = email
                            withContext(Dispatchers.IO) {
                                dao.update(cred).toString()
                            }
                        }
                        Toast.makeText(this@RegisterActivity, "Успех!", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    is Result.Error -> {
                        val exc = msg.exception.message.toString()
                        if (exc.contains("valid_email")) Toast.makeText(
                            this@RegisterActivity,
                            "Email содержит недопустимые символы",
                            Toast.LENGTH_SHORT
                        ).show()
                        else if (exc.contains("valid_nickname")) Toast.makeText(
                            this@RegisterActivity,
                            "Никнейм содержит недопустимые символы",
                            Toast.LENGTH_SHORT
                        ).show()
                        else if (exc.contains("User Already Exists")) Toast.makeText(
                            this@RegisterActivity,
                            "Такой никнейм уже существует",
                            Toast.LENGTH_SHORT
                        ).show()
                        else {
                            Toast.makeText(
                                this@RegisterActivity, exc, Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                }
            }
        }
    }
}