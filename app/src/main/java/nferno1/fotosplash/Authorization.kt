package nferno1.fotosplash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import nferno1.fotosplash.api.Scope
import nferno1.fotosplash.data.Token
import nferno1.fotosplash.repository.UnsplashRepository
import nferno1.fotosplash.utils.Constants
import nferno1.fotosplash.utils.Constants.KEY_AUTORIZATION
import nferno1.fotosplash.utils.Constants.TOKEN
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class Authorization : AppCompatActivity() {

    @Inject
    lateinit var repository: UnsplashRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autorization)
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val button = findViewById<Button>(R.id.buttonAuthorization)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarAuthorization)
        button.setOnClickListener {
            authorize(sharedPref, this)
        }
        val code = handleAuthCallback()
        Log.d("auto", "code=$code")
        if (code != null) {
            progressBar.isVisible = true
            button.isVisible = false
            repository.getToken(code).enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    val tokenResponse = response.body() ?: return
                    val token = tokenResponse.accessToken.toString()
                    Log.d("auto", "token=$token")
                    navigate(token)
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    progressBar.isVisible = false
                    button.isVisible = true
                    Toast.makeText(
                        this@Authorization,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    fun navigate(token: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(TOKEN, token)
        startActivity(intent)
        finish()
    }

    private fun authorize(sharedPref: SharedPreferences, context: Context) {
        val firstAuto = sharedPref.getBoolean(KEY_AUTORIZATION, true)
        if (firstAuto) {
            val scopeList =
                listOf(Scope.PUBLIC, Scope.READ_USER, Scope.WRITE_USER, Scope.WRITE_LIKES)
            var scopes = StringBuilder()
            for (scope in scopeList) {
                scopes.append(scope.scope).append("+")
            }
            scopes = scopes.deleteCharAt(scopes.length - 1)
            val url =
                "https://unsplash.com/oauth/authorize?client_id=${Constants.ACCESS_KEY}&redirect_uri=${Constants.redirectURI}&response_type=code&scope=$scopes"
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(url))
        }
    }

    private fun handleAuthCallback(): String? {
        val data = this.intent?.data
        return data?.query?.replace("code=", "")
    }
}