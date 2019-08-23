package mx.contraloria.dap

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_intro.*
import mx.contraloria.dap.Adapters.IntroViewPagerAdapter
import mx.contraloria.dap.models.ScreenItem
import mx.contraloria.dap.models.mList





class IntroActivity : AppCompatActivity() {

    lateinit var screnPager : ViewPager
    lateinit var introViewPagerAdapter : IntroViewPagerAdapter
    lateinit var btnSig : Button
    lateinit var btnGetStarted : Button
    lateinit var btnAnimation : Animation
    var position = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Make activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


        //Verificamos si el usuario ya vio la intro
        if(restorePrefData()){
            val v_main_activity : Intent = Intent(applicationContext,ListaServidoresActivity::class.java)
            startActivity(v_main_activity)
            finish()
        }
        //Hide the action bar
        getSupportActionBar()?.hide()

        setContentView(R.layout.activity_intro)
        val tabIndicator: TabLayout

        //init View
        btnSig = findViewById(R.id.btnSiguente)
        btnGetStarted = findViewById(R.id.btnComenzar)
        tabIndicator = findViewById(R.id.tabIntro)
        btnAnimation = AnimationUtils.loadAnimation(applicationContext,R.anim.button_animation_intro_comenzar)

        //llenamos los datos

        val mList = ArrayList<ScreenItem>()
        mList.add(
            ScreenItem(
                "Búsqueda",
                "La aplicación DAP, cuenta con un solo buscador, optimizando la búsqueda y experiencia del usuario," +
                        " cada palabra que se escriba en el buscador el sistema buscará las coincidencias en cada información de cada servidor",
                R.drawable.dap_buscador
            )
        )
        mList.add(
            ScreenItem(
                "Opciones Swipe",
                "La aplicación DAP nos permite llamar, mandar correo, ver dirección en mapa, guardar en favoritos " +
                        "y compartir usuario y su información a otras personas, solo haciendo swipe izquierdo o derecho sobre cada registro.",
                R.drawable.dap_swipe
            )
        )
        mList.add(
            ScreenItem(
                "Detalles del contacto",
                "Cuando accedemos al detalle, aparte de tener todas las opciones antes mencionadas, también contamos con " +
                        "información adicional del contacto así como su reseña.",
                R.drawable.dap_detalles
            )
        )




        screnPager = findViewById(R.id.screen_viewpager)

        introViewPagerAdapter = IntroViewPagerAdapter(this, mList)
        screnPager.adapter = introViewPagerAdapter

        //Setup number of tab
        tabIndicator.setupWithViewPager(screnPager)

        btnSig.setOnClickListener(View.OnClickListener {

            position = screnPager.currentItem

            if (position < mList.size) {
                position++
                screnPager.currentItem = position


            }
            if (position == mList.size) {
                loadingScreen()
            }

        })
        tabIndicator.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == (mList.size -1)) {
                    loadingScreen()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })

        //Botton comenzar click
        btnGetStarted.setOnClickListener(View.OnClickListener {

           val main_activity: Intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(main_activity)

            savePrefData()
            finish()

        })

    }

    private fun restorePrefData(): Boolean {

        val pref: SharedPreferences = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val isIntroActivityOpenedBefore : Boolean = pref.getBoolean("isIntroOpened",false)
        return isIntroActivityOpenedBefore
    }

    private fun savePrefData() {
        val pref: SharedPreferences = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putBoolean("isIntroOpened",true)
        editor.commit()

    }

    private fun loadingScreen() {

        btnSig.visibility = View.INVISIBLE
        btnGetStarted.visibility = View.VISIBLE
        tabIntro.visibility = View.INVISIBLE

        //Setup button comenzar
        btnGetStarted.animation = btnAnimation
    }
}
