package com.example.bolovanje.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.example.bolovanje.R
import com.example.bolovanje.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        makeFullScreen()

        setContentView(R.layout.activity_splash)

        //using the handler to delay loading the MainActivity
        Handler().postDelayed({
            //start activity
            startActivity(Intent(this, MainActivity::class.java))

            //animate the loading of new activity
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            //close this activity
            finish()
        }, 2000)
    }

    private fun makeFullScreen(){
        //remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        //make full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //hide the toolbar
        supportActionBar?.hide()
    }
}
