package com.example.apptranslate

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apptranslate.API.retrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var btnDetectLanguage:Button
    private lateinit var etDescription: EditText

    var allLanguages = emptyList<Language>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initListener()
        getLanguages()
    }

    private fun initListener() {
        btnDetectLanguage.setOnClickListener{
            val text = etDescription.text.toString()
            if (text.isNotEmpty()){
                getTextLanguage(text)
            }
        }
    }
    private fun getTextLanguage(text: String){
        CoroutineScope(Dispatchers.IO).launch {
            val result: Response<DetectionResponse> = retrofitService.getTextLanguage(text)
            if (result.isSuccessful){
                checkResult(result.body())
            }else{
                showError()
            }
        }
    }

    private fun checkResult(detectionResponse: DetectionResponse?) {
        if(detectionResponse != null && detectionResponse.data.detections.isNullOrEmpty()){
            val correctLanguages : List<Detection> = detectionResponse.data.detections.filter{it.isReliable}
            if (correctLanguages.isNotEmpty()){

                val languageName : Language? = allLanguages.find {it.code == correctLanguages.first().language}
                if (languageName != null){
                    runOnUiThread{
                        Toast.makeText(this, "The language is ${languageName.name}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getLanguages() {
        CoroutineScope(Dispatchers.IO).launch{

            val languages: Response<List<Language>> = retrofitService.getLanguages()
            if (languages.isSuccessful){
                allLanguages = languages.body() ?: emptyList()
                showSuccess()
            }else{
                showError()
            }
        }

    }

    private fun showSuccess() {
        runOnUiThread {
            Toast.makeText(this, "correct petition", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showError() {
        runOnUiThread{
            Toast.makeText(this, "error when calling", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initView() {
        btnDetectLanguage = findViewById(R.id.btnDetectLanguage)
        etDescription = findViewById(R.id.etDescription)
    }
}