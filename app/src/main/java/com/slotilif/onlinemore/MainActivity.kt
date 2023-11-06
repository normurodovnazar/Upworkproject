package com.slotilif.onlinemore

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.PopupMenu
import androidx.activity.ComponentActivity
import com.slotilif.onlinemore.databinding.MainBinding

class MainActivity : ComponentActivity() {
    private lateinit var binding: MainBinding
    lateinit var menu: PopupMenu
    var errorHappened = false
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        menu = PopupMenu(this,binding.menu)
        menu.menu.add(0, 1, 1, "Rate us")
        //menu.menu.add(0, 2, 2, "Share app")

        binding.web.settings.javaScriptEnabled = true
        binding.web.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        CookieManager.getInstance().setAcceptCookie(true)
        binding.web.webViewClient = MClient()

        if (savedInstanceState==null) {
            binding.web.loadUrl("https://footbllfun.online/")
        }

        binding.reload.setOnClickListener {
            errorHappened = false
            binding.web.reload()
            showLoading()
        }

        binding.menu.setOnClickListener {
            menu.show()
        }
        menu.setOnMenuItemClickListener {
            when(it.itemId){
                1 ->{
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                }
//                2 ->{
//                    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
//                    val clipData = ClipData.newPlainText("Link","https://play.google.com/store/apps/details?id=$packageName")
//                    clipboardManager.setPrimaryClip(clipData)
//                    Toast.makeText(this,"App link copied to clipboard",Toast.LENGTH_LONG).show()
//                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.web.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.web.restoreState(savedInstanceState)
    }

    inner class MClient: WebViewClient() {

        init {
            showLoading()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (errorHappened){
                showError()
            } else {
                showPage()
            }
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            if (error != null) {
                if (error.description.toString()=="net::ERR_NAME_NOT_RESOLVED") {
                    errorHappened = true
                }
            }
            showError()
        }

    }

    private fun showError(){
        Log.e("e","showError")
        binding.loading.visibility = View.GONE
        binding.web.visibility = View.GONE
        binding.error.visibility = View.VISIBLE
    }
    private fun showLoading(){
        Log.e("e","showLoading")
        binding.error.visibility = View.GONE
        binding.web.visibility = View.VISIBLE
        binding.loading.visibility = View.VISIBLE
    }
    private fun showPage(){
        Log.e("e","showPage")
        binding.loading.visibility = View.GONE
        binding.error.visibility = View.GONE
        binding.web.visibility = View.VISIBLE
    }
}