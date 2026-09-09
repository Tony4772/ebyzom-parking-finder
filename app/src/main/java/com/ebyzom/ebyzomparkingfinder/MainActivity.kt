package com.ebyzom.ebyzomparkingfinder

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.net.URISyntaxException

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupWebView()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            databaseEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            javaScriptCanOpenWindowsAutomatically = true
            mediaPlaybackRequiresUserGesture = false
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false

                // Si es Google Maps, intentamos forzar que se quede dentro del WebView
                if (url.contains("google.com/maps") || url.contains("maps.app.goo.gl")) {
                    if (url.startsWith("intent://")) {
                        try {
                            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                            val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                            if (fallbackUrl != null) {
                                view?.loadUrl(fallbackUrl)
                                return true
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    return false // Permite que el WebView cargue la URL de maps directamente
                }

                // Para otros esquemas (tel, mailto, etc.) o intents genéricos
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    try {
                        val intent = if (url.startsWith("intent://")) {
                            Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        } else {
                            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        }

                        if (intent != null) {
                            if (intent.resolveActivity(packageManager) != null) {
                                startActivity(intent)
                            } else {
                                val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                if (fallbackUrl != null) {
                                    webView.loadUrl(fallbackUrl)
                                }
                            }
                            return true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return true
                }
                
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                injectCSS()
            }
            
            @Suppress("DEPRECATION")
            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                if (errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT || errorCode == ERROR_HOST_LOOKUP) {
                    // Aquí podrías mostrar una vista de error personalizada
                }
            }
        }

        webView.loadUrl("https://ebyzom-parking-finder.lovable.app/")
    }

    private fun injectCSS() {
        val css = "#lovable-badge { display: none !important; }"
        val js = "(function() {" +
                "var style = document.createElement('style');" +
                "style.innerHTML = '$css';" +
                "document.head.appendChild(style);" +
                "})();"
        webView.evaluateJavascript(js, null)
    }
}