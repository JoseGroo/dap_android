package mx.contraloria.dap

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_detalle_servidor.*
import kotlinx.android.synthetic.main.row.view.*
import mx.contraloria.dap.models.Dependencias
import mx.contraloria.dap.models.Favorites
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class DetalleServidorActivity : MyToolBarActivity() {

    lateinit var vFavButton: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_servidor)



        tvNombreServidor.setText("José Manuel Guerrero Sánchez")
        tvPuestoOficial.setText("Jefe de departamento")
        tvFechaAlta.setText("16-10-2016")
        tvNivel.setText("9C")
        tvResena.setText("INGENIERO EN SISTEMAS COMPUTACIONALES POR EL ITH DEL AÑO 2015, EN EL AÑO 2015 SE DESEMPEÑÓ EN LA EMPRESA CREDIUNO DONDE OCUPÓ EL CARGO DE: DESARROLLADOR DE SOFTWARE, DEL AÑO 2015 AL 2016 SE DESEMPEÑÓ EN LA EMPRESA GRUPO LAN EDUCATION, OCUPO EL CARGO DE: DESARROLLADOR JUNIOR. ACTUALMENTE SE DESEMPEÑA EN LA SECRETARIA DE LA CONTRALORÍA GENERAL CON EL CARGO DE: JEFE DE DEPARTAMENTO.")
        tvDependencia.setText("Secretaria de la contraloria general")
        tvUnidadAdministrativa.setText("Subsecretaria de desarrollo administrativo y tecnologico")
        tvPuestoFuncional.setText("Jefe de departamento de planeación de tecnologia")

        vFavButton = findViewById(R.id.btnFavorito)

        var Favorito = Favorites(this@DetalleServidorActivity)

        Favorito.InitPreferentImage("1", vFavButton)
    }

    fun PutFav(view: View)
    {
        var Favorito = Favorites(this@DetalleServidorActivity)
        Favorito.AddDeleteFavoritos(view,"1", "Jose Manuel", vFavButton)
    }
}
