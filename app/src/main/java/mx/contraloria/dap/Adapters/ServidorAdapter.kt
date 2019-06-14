package mx.contraloria.dap.Adapters
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import mx.contraloria.dap.R
import mx.contraloria.dap.models.Servidores
import android.widget.*
import com.daimajia.swipe.SwipeLayout
import java.io.IOException
import java.net.URL
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_swipe.view.*
import mx.contraloria.dap.MapsActivity
import mx.contraloria.dap.models.Favorites
import mx.contraloria.dap.models.FuncionesGenerales
import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.lang.Exception
import java.util.*


class ServidorAdapter(context: Context, val items: List<Servidores>) : BaseAdapter() {
    private val layoutInflater = LayoutInflater.from(context)
    private val favorites = Favorites(context)

    var mBusy: Boolean = false
    var FuncionesGenerales = FuncionesGenerales(context)
    lateinit var context: Context

    init {

        this.context = context
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View? {
        val viewHolder: ViewHolder
        val rowView: View?

        if (view == null) {
            rowView = layoutInflater.inflate(R.layout.row_swipe, viewGroup, false)

            viewHolder = ViewHolder(rowView)
            rowView.tag = viewHolder

        } else {
            rowView = view
            viewHolder = rowView.tag as ViewHolder
        }

        viewHolder.LayoutSwipe.addDrag(SwipeLayout.DragEdge.Left, viewHolder.LayoutSwipe.findViewById(R.id.bottom_wrapper))
        viewHolder.LayoutSwipe.addDrag(SwipeLayout.DragEdge.Right, viewHolder.LayoutSwipe.findViewById(R.id.bottom_wrapper_2))

        var item = items.get(position)
        var snombre_completo = FuncionesGenerales.NormalizerTextNames( item.nombre_completo)
        var sTitulo = FuncionesGenerales.NormalizerTextNames( item.titulo)
        var sDependecia = FuncionesGenerales.NormalizerTextNames( item.dependencia)



        /////////// Emails ****
        viewHolder.btnEmails.setOnClickListener(View.OnClickListener {
            FuncionesGenerales.GenerarviewCallEmail(snombre_completo,item.foto,item.correo_electronico)
        })
        ///////////telefonos ***
        viewHolder.btnPhones.setOnClickListener(View.OnClickListener {
           FuncionesGenerales.GenerarViewCallTelefonos(snombre_completo,item.foto,item.lada,item.telefono)

        })


        //Cargamos la imagen si el scroll se detuvo
        if(!mBusy){
            //ponemos la imagen
            if(items[position].foto != ""){
                try{
                    /*val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                    StrictMode.setThreadPolicy(policy)*/
                    var url = items[position].foto
                    Picasso.with(context).load(url)
                        .fit()
                        .transform(CircleTransform())
                        .centerCrop()
                        .placeholder(R.drawable.spinner_progress_animation)
                        .error(R.mipmap.ic_icon_perfil_round)
                        .into(viewHolder.imgPerfil)
                }catch (e: IOException){
                    print(e)
                }
            }

        }else{
            viewHolder.imgPerfil.setImageResource(R.drawable.spinner_progress_animation)
        }
        viewHolder.nombre.text = FuncionesGenerales.NormalizerTextNames( sTitulo +" "+snombre_completo)
        viewHolder.dependencia.text = FuncionesGenerales.NormalizerTextNames(sDependecia)

        //Creamos el evento de Favoritos
        //Favoritos
        favorites.InitPreferentImageText(items.get(position).id.toString(),viewHolder.imgFavoritos)
        viewHolder.imgFavoritos.setOnClickListener(View.OnClickListener {
            favorites.AddDeleteFavoritosYext(viewHolder.imgFavoritos,item.id.toString(),snombre_completo,viewHolder.imgFavoritos)
        })

        //Creamos el evento compartir **************************************** pendiente de revision para poner los snombre_completo
        viewHolder.imgCompartir.setOnClickListener(View.OnClickListener {
            try{
                /*Get the bitmap that we stored in a File*/
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
                var url = URL(items.get(position).foto)
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
                FuncionesGenerales.GenerarVcard(item,data)

            }catch (e: Exception){
                print(e.message)
            }

        })
        //Creamos el evento de Geolocalizacion
        //Geolocalizacion


        viewHolder.btnGeolocalizacion.setOnClickListener(View.OnClickListener {
            var vMapaActivity = Intent(context, MapsActivity::class.java)
            vMapaActivity.putExtra("servidor", item as Serializable)
            startActivity(context, vMapaActivity, null)
        })



        return rowView
    }

    override fun getItem(position: Int): Any {
        return 0
    }

    override fun getItemId(position: Int): Long {
        return 0
    }





    /*Datos que necesito de mi vista*/
    private class ViewHolder(view: View?) {
        val imgPerfil = view?.findViewById(R.id.iImageR) as ImageView
        val nombre = view?.findViewById(R.id.txtNombreR) as TextView
        val dependencia = view?.findViewById(R.id.txtPuestoR) as TextView
        val puesto = view?.findViewById(R.id.txtUnidadR) as TextView
        val imgFavoritos = view?.findViewById(R.id.btnFavorito) as TextView
        val imgCompartir = view?.findViewById(R.id.btnCompartir) as TextView
        val LayoutSwipe = view?.findViewById(R.id.row_swipe_1) as SwipeLayout
        val btnPhones = view?.findViewById(R.id.Phones) as ImageView
        val btnEmails = view?.findViewById(R.id.Emails) as ImageView
        val btnGeolocalizacion = view?.findViewById(R.id.Geolocalizacion) as ImageView

    }


}


