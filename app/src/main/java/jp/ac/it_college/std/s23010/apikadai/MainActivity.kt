package jp.ac.it_college.std.s23010.apikadai

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import jp.ac.it_college.std.s23010.apikadai.databinding.ActivityMainBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val DEBUG_TAG = "kadaiApplication"
        private const val YESNO_URL = "https://yesno.wtf/api"
        private const val INSULT_URL = "https://evilinsult.com/generate_insult.php?lang=ja&type=text"
        private const val ADVICE_URL = "https://api.adviceslip.com/advice"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            val manager = LinearLayoutManager(this@MainActivity)
            DividerItemDecoration(this@MainActivity, manager.orientation)
        }

        binding.yes.setOnClickListener {
            fetchAnswer("yes")
        }

        binding.no.setOnClickListener {
            fetchAnswer("no")
        }
    }

    private fun fetchAnswer(expectedAnswer: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(YESNO_URL)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(DEBUG_TAG, "API request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val json = JSONObject(responseBody.string())
                    val answer = json.getString("answer")

                    val result = (expectedAnswer.equals(answer, ignoreCase = true))

                    runOnUiThread {
                        binding.answer.text = answer.capitalize()

                        //Log.d(DEBUG_TAG, "結果: $result")
                        binding.res.text = "APIの答え: ${answer.capitalize()},\n 結果: $result"
                    }
                }
            }
        })
    }
}
