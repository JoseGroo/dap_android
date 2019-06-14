package mx.contraloria.dap

import android.app.ProgressDialog
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
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
import com.squareup.picasso.Picasso
import mx.contraloria.dap.Adapters.CircleTransform
import mx.contraloria.dap.models.FuncionesGenerales


class DetalleServidorActivity : MyToolBarActivity() {

    lateinit var vServidor: Servidores

    var oFuncionesGenerales = FuncionesGenerales(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_servidor)


        vServidor = intent.extras.get("servidor") as Servidores

        menu_buscar.setOnClickListener {
            oFuncionesGenerales.goIndex(menu_buscar)
        }

        menuOptions.setClosedOnTouchOutside(true)


        tvNombreServidor.setText(oFuncionesGenerales.NormalizerTextNames("${vServidor.titulo} ${vServidor.nombre_completo}"))
        tvPuestoOficial.setText(oFuncionesGenerales.NormalizerTextNames(vServidor.puesto_oficial))
        val sFechaSplit = vServidor.fecha_de_alta.split("-")
        var iMonth = sFechaSplit[1].toInt()
        val sMonth = oFuncionesGenerales.months[iMonth - 1]
        tvFechaAlta.setText("${sMonth} ${sFechaSplit[2]}, ${sFechaSplit[0]}")
        tvNivel.setText(vServidor.nivel)
        tvResena.setText(vServidor.reseña.trim())
        tvDependencia.setText(oFuncionesGenerales.NormalizerTextNames(vServidor.dependencia))
        tvUnidadAdministrativa.setText(oFuncionesGenerales.NormalizerTextNames(vServidor.unidad_administrativa))
        tvPuestoFuncional.setText(oFuncionesGenerales.NormalizerTextNames(vServidor.puesto_funcional))
        tvDomicilio.setText(vServidor.domicilio)
        tvTelefonos.setText(vServidor.telefono)
        tvCorreoElectronico.setText(vServidor.correo_electronico)

        if(vServidor.foto != ""){
            try
            {
                Picasso.with(this@DetalleServidorActivity)
                    .load(vServidor.foto)
                    .fit()
                    .transform(CircleTransform())
                    .into(imagePerfil)

            }
            catch (e: IOException)
            {
                print(e)
            }
        }

        var Favorito = Favorites(this@DetalleServidorActivity)

        Favorito.InitPreferentImage(vServidor.id.toString(), menu_favoritos)
    }

    fun PutFav(view: View)
    {
        var Favorito = Favorites(this@DetalleServidorActivity)
        Favorito.AddDeleteFavoritos(view,vServidor.id.toString(), vServidor.titulo + " " + vServidor.nombre_completo, menu_favoritos)
        menuOptions.close(true)
    }

    fun Compartir(view: View)
    {

    }

    fun Mapa(view: View)
    {

    }

    fun Telefonos(view: View)
    {

    }

    fun Correos(view: View)
    {

    }
}
