package mx.contraloria.dap

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.toolbar_image.*


abstract class MyToolBarActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.toolbar_image)


    }

    override fun onResume() {
        super.onResume()
        var imagen = findViewById<ImageView>(R.id.bottom_white_background)

        if(imagen != null && this !is DetalleServidorActivity)
        {
            val rnds = (2000..2800).random()
            //imagen.animate().translationY(rnds.toFloat()).setDuration(1000).start()
            /*var animacion : Animation = TranslateAnimation(0f,0f,0f,rnds.toFloat())
            animacion.setDuration(1000)
            imagen.startAnimation(animacion)*/
            SpringAnimation(imagen, DynamicAnimation.TRANSLATION_Y, rnds.toFloat()).apply {
                spring.stiffness = 70f
                spring.dampingRatio = 0.5f
            }.start()


        }else if(imagen != null){
            SpringAnimation(imagen, DynamicAnimation.TRANSLATION_Y,1550f).apply {
                spring.stiffness = 70f
                spring.dampingRatio = 0.5f
            }.start()
        }
    }

    fun BackButtonCustom(view: View)
    {
        onBackPressed()
    }

    fun OpenMenu(view: View)
    {

    }

    fun btnFavoritosPress(view: View){
        var intent = Intent(this, FavoritosActivity::class.java)
        startActivity(intent)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
