package com.ari.submission.storyapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.ari.submission.storyapp.background.ApiConfig
import com.ari.submission.storyapp.background.LoginResponse
import com.ari.submission.storyapp.background.LoginResult
import com.ari.submission.storyapp.databinding.ActivityLoginBinding
import com.ari.submission.storyapp.ui.home.MainActivity
import com.ari.submission.storyapp.ui.home.MainViewModel
import com.ari.submission.storyapp.ui.register.SignUpActivity
import com.ari.submission.storyapp.utils.MyResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sph: com.ari.submission.storyapp.preferences.SharedPreferences

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.Factory(this)
    }
    companion object{
        private const val TAG = "LoginActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sph = com.ari.submission.storyapp.preferences.SharedPreferences(this)
        if (sph.getStatusLogin()){
            val main = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(main)
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val register = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(register)
        }

        binding.progressBar.visibility = View.GONE
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when{
                email.isEmpty()->{
                    binding.edLoginEmail.error = "Email is Required"
                }
                password.isEmpty()->{
                    binding.edLoginPassword.error = "Password is Required"
                }
                else->{

                  loginViewModel.getAllUserLogin(email,password).observe(this@LoginActivity){ result ->
                      if (result != null){
                          when(result){
                              is MyResult.Loading -> {
                                  binding.progressBar.visibility = View.VISIBLE
                              }
                              is MyResult.Success -> {
                                  binding.progressBar.visibility = View.GONE
                                  sph.saveUserToken(result.list.loginResult.token)
                                  showToast()
                              }
                              is MyResult.Error -> {
                                  binding.progressBar.visibility = View.GONE
                                  Toast.makeText(this@LoginActivity, result.Error, Toast.LENGTH_SHORT).show()
                              }
                          }
                      }
                  }
                }
            }
        }
    }

     private fun showToast(){
       Toast.makeText(this@LoginActivity, "Berhasil", Toast.LENGTH_SHORT).show()
         val builder = AlertDialog.Builder(this@LoginActivity)
         with(builder)
         {
             setTitle("Login Success")
             setMessage("Press it to join our story ")
             setPositiveButton("OK") { _, _ ->
                 getIntentMain()
             }
             val alertDialog: AlertDialog = builder.create()
             alertDialog.setCancelable(false)
             alertDialog.show()

         }
     }

    private fun getIntentMain(){
        sph.setStatusLogin(true)
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }





//    private fun login(email: String, password: String){
//        val client = ApiConfig().getApiService().login(email,password)
//        client.enqueue(object: Callback<LoginResponse>{
//            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
//                if (response.isSuccessful){
//                    val responseBody = response.body()
//                    if (responseBody!=null && !responseBody.error){
//                        sph.saveUserToken(responseBody.loginResult.token)
//                        sph.setStatusLogin(true)
//                        val main = Intent(this@LoginActivity, MainActivity::class.java)
//                        startActivity(main)
//                        finishAffinity()
//                    }else{
//                        Toast.makeText(applicationContext, "${responseBody?.message}", Toast.LENGTH_LONG).show()
//                    }
//                }else{
//                    val builder = AlertDialog.Builder(this@LoginActivity)
//                    with(builder)
//                    {
//                        setTitle("Login Failed")
//                        setMessage("Please check! ${response.message()}")
//                        setPositiveButton("OK") { dialogInterface, which ->
//                            Toast.makeText(this@LoginActivity, "Try Again", Toast.LENGTH_SHORT).show()
//
//                        }
//                        val alertDialog: AlertDialog = builder.create()
//                        alertDialog.setCancelable(false)
//                        alertDialog.show()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_LONG).show()
//
//                Log.e(TAG, "onFailure: ${t.message.toString()}")
//            }
//
//        })
//    }
}

