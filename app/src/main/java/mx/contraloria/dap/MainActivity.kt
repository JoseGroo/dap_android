package mx.contraloria.dap

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)



        Handler().postDelayed({
            //Verificamos si el usuario ya vio la intro
            if(!restorePrefData()){
                val intro_activity : Intent = Intent(applicationContext,IntroActivity::class.java)
                startActivity(intro_activity)
                finish()
            }else{
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finish()
            }

        }, 1000)


    }

    private fun restorePrefData(): Boolean {

        val pref: SharedPreferences = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val isIntroActivityOpenedBefore : Boolean = pref.getBoolean("isIntroOpened",false)
        return isIntroActivityOpenedBefore
    }
}
