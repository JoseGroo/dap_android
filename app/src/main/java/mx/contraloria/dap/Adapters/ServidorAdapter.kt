package mx.contraloria.dap.Adapters
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.contraloria.dap.R
import mx.contraloria.dap.models.Servidores
import android.widget.*
import com.daimajia.swipe.SwipeLayout
import java.io.IOException
import java.net.URL
import com.squareup.picasso.Picasso




class ServidorAdapter(context: Context, val items: List<Servidores>) : BaseAdapter() {
    private val layoutInflater = LayoutInflater.from(context)
    var mBusy: Boolean = false
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


        if(!mBusy){
            //ponemos la imagen
            if(items[position].foto != ""){
                try{
                    /*val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                    StrictMode.setThreadPolicy(policy)*/
                    var url = items[position].foto
                    Picasso.with(context).load(url).resize(100, 100)
                        .centerCrop()
                        .placeholder(R.drawable.spinner_progress_animation)
                        .error(R.mipmap.ic_icon_perfil_round)
                        .into(viewHolder.imgPerfil);
                }catch (e: IOException){
                    print(e)
                }
            }

        }else{
            viewHolder.imgPerfil.setImageResource(R.drawable.spinner_progress_animation)
        }
        viewHolder.nombre.text = items[position].titulo + items[position].nombre_completo
        viewHolder.dependencia.text = items[position].dependencia.substring(0,1).toUpperCase() + items[position].dependencia.substring(1).toLowerCase()

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
        val LayoutSwipe = view?.findViewById(R.id.row_swipe_1) as SwipeLayout

    }
}


