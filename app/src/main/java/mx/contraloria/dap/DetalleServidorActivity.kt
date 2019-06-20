package mx.contraloria.dap

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
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
import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.lang.Exception
import java.net.URL
import java.util.*


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
                /*Picasso.with(this@DetalleServidorActivity)
                    .load(vServidor.foto)
                    .fit()
                    .transform(CircleTransform())
                    .into(imagePerfil)*/
                Picasso.get()
                    .load(vServidor.foto)
                    .transform(CircleTransform())
                    .placeholder(R.drawable.spinner_progress_animation)
                    .error(R.mipmap.ic_icon_perfil_round)
                    .into(imagePerfil)

            }
            catch (e: IOException)
            {
                print(e)
            }
        }

        var Favorito = Favorites(this@DetalleServidorActivity)

        Favorito.InitPreferentImage(vServidor, menu_favoritos)
    }

    fun PutFav(view: View)
    {
        var Favorito = Favorites(this@DetalleServidorActivity)
        Favorito.AddDeleteFavoritosJson(view,vServidor, menu_favoritos)
        menuOptions.close(true)
    }

    fun Compartir(view: View)
    {
        try{
            /*Get the bitmap that we stored in a File*/
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            var url = URL(vServidor.foto)
            var bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())

            /*Convert bitmap to Base64*/
            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            //val image_encoded = Base64.encodeToString(b, Base64.DEFAULT)
            val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Base64.getEncoder().encodeToString(b)
            } else {
                android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT)
            }
            oFuncionesGenerales.GenerarVcard(vServidor,data)

        }catch (e: Exception){
            print(e.message)
        }
        menuOptions.close(true)
    }

    fun Mapa(view: View)
    {
        var vMapaActivity = Intent(this@DetalleServidorActivity, MapsActivity::class.java)
        vMapaActivity.putExtra("servidor", vServidor as Serializable)
        startActivity(vMapaActivity)
        menuOptions.close(true)
    }

    fun Telefonos(view: View)
    {
        oFuncionesGenerales.GenerarViewCallTelefonos(tvNombreServidor.text.toString(), vServidor.foto, vServidor.lada, vServidor.telefono)
        menuOptions.close(true)
    }

    fun Correos(view: View)
    {
        oFuncionesGenerales.GenerarviewCallEmail(tvNombreServidor.text.toString(), vServidor.foto, vServidor.correo_electronico)
        menuOptions.close(true)
    }
}
