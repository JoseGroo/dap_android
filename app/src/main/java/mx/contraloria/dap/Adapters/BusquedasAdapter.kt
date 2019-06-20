package mx.contraloria.dap.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.contraloria.dap.R
import android.widget.*
import mx.contraloria.dap.models.Busquedas
import mx.contraloria.dap.models.FuncionesGenerales
import java.lang.Exception
import kotlin.collections.ArrayList



class BusquedasAdapter(context: Context, var items: List<Busquedas>) : BaseAdapter() {
    private val layoutInflater = LayoutInflater.from(context)
    var context: Context = context

    override fun getCount(): Int {
        return items.size
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View? {


        val viewHolder: ViewHolder
        val rowView: View?

        if (view == null) {
            rowView = layoutInflater.inflate(R.layout.row_busqueda, viewGroup, false)

            viewHolder = ViewHolder(rowView)
            rowView.tag = viewHolder

        } else {
            rowView = view
            viewHolder = rowView.tag as ViewHolder
        }

        var item = items.get(position)
        var Busqueda = item.Busqueda
        var FechaBusqueda = item.Fecha




        viewHolder.Busqueda.text = Busqueda
        viewHolder.FechaBusqueda.text = FechaBusqueda


        viewHolder.EliminarBusqueda.setOnClickListener(View.OnClickListener {
            var oFuncionesGenerales = FuncionesGenerales(context)

            var vBusquedas = ArrayList<Busquedas>(items)

            vBusquedas.remove(item)

            oFuncionesGenerales.saveSearchJson(vBusquedas)
            items = vBusquedas as List<Busquedas>
            this.notifyDataSetChanged()



            Toast.makeText(
                context,
                "Se elimino: " + item.Busqueda,
                Toast.LENGTH_LONG
            ).show()
        })

        /*Click detalles*/
        viewHolder.layoutContainer.setOnClickListener(View.OnClickListener {
            try{

                Toast.makeText(
                    context,
                    "de aqui mandarlo a buscar. con: " + item.Busqueda,
                    Toast.LENGTH_LONG
                ).show()
            }catch (e:Exception){
                Toast.makeText(
                    context,
                    "Error al momento de mostrar el detalle del servidor.",
                    Toast.LENGTH_LONG
                ).show()
            }
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
        val FechaBusqueda = view?.findViewById(R.id.tvFechaBusqueda) as TextView
        val Busqueda = view?.findViewById(R.id.tvBusqueda) as TextView
        val layoutContainer = view?.findViewById(R.id.containerRowBusqueda) as RelativeLayout
        val EliminarBusqueda = view?.findViewById(R.id.ivEliminarBusqueda) as ImageView
    }


}


