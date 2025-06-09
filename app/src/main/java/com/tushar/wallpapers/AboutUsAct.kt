package com.tushar.wallpapers

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.tushar.wallpapers.databinding.ActivityAboutUsBinding
import com.tushar.wallpapers.model.Photo
import java.lang.reflect.Field

class AboutUsAct : AppCompatActivity() {
    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.link.text = HtmlCompat.fromHtml(
            "Images Source: <a href =\"https://www.pexels.com\">Pexels</a>",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.link.movementMethod = LinkMovementMethod.getInstance()
        setupListeners()

        onBackPressedDispatcher.addCallback(this) {
            val intent = Intent(this@AboutUsAct, MainActivity::class.java)
            intent.putExtra("selected_id", R.id.nav_home)
            startActivity(intent)
            finish()
        }}



    private fun setupListeners() {
        binding.menu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("selected_id", R.id.nav_home)
            startActivity(intent)
            finish()
        }
    }

}