package mx.contraloria.dap

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
import android.support.v4.view.ViewCompat.canScrollVertically
import android.support.v7.widget.RecyclerView
import android.widget.*
import mx.contraloria.dap.models.Servidores


class DetalleServidorActivity : MyToolBarActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_servidor)


        val vServidor = intent.extras.get("servidor") as Servidores


        tvNombreServidor.setText(vServidor.titulo + " " + vServidor.nombre_completo)
        tvPuestoOficial.setText(vServidor.puesto_oficial)
        tvFechaAlta.setText(vServidor.fecha_de_alta)
        tvNivel.setText(vServidor.nivel)
        tvResena.setText(vServidor.reseña)
        tvDependencia.setText(vServidor.dependencia)
        tvUnidadAdministrativa.setText(vServidor.unidad_administrativa)
        tvPuestoFuncional.setText(vServidor.puesto_funcional)

        var Favorito = Favorites(this@DetalleServidorActivity)

        Favorito.InitPreferentImage(vServidor.id.toString(), btnFavorito)
    }

    fun PutFav(view: View)
    {
        var Favorito = Favorites(this@DetalleServidorActivity)
        Favorito.AddDeleteFavoritos(view,"1", "Jose Manuel", btnFavorito)
    }
}
