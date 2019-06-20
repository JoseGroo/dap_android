package mx.contraloria.dap

import android.app.ActivityOptions
import android.app.AppComponentFactory
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Pair
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.row_swipe.view.*
import mx.contraloria.dap.Adapters.BusquedasAdapter
import mx.contraloria.dap.Adapters.ServidorAdapter
import mx.contraloria.dap.models.Busquedas
import mx.contraloria.dap.models.Favorites
import mx.contraloria.dap.models.FuncionesGenerales
import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var oFuncionesGenerales: FuncionesGenerales



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        // ==================================================
        // Evento cuando dan enter al edit text de buscar
        // ==================================================
        etFiltroNombrePuesto.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                BuscarServidores(etFiltroNombrePuesto)
                return@OnKeyListener true
            }
            false
        })
        // ==================================================
        // Fin evento cuando dan enter al edit text de buscar
        // ==================================================

        oFuncionesGenerales = FuncionesGenerales(this@HomeActivity)

        FillSearch(listUltimasBusquedas)
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.eliminar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        FillSearch(listUltimasBusquedas)
        etFiltroNombrePuesto.setText("")
    }

    fun FillSearch(listView: ListView){

        oFuncionesGenerales = FuncionesGenerales(this@HomeActivity)
        var lista = oFuncionesGenerales.getSearchFromSharedPreferences()
        if(lista != null && lista.size > 0)
        {
            var vUltimoElemento = if (lista.size > 5)  5 else lista.size

            lista = lista.sortedByDescending { r -> r.Fecha }.subList(0, vUltimoElemento)
            var adapter = BusquedasAdapter(this, lista)
            listView.adapter = adapter
            listView.setOnScrollListener(object : AbsListView.OnScrollListener {

                override fun onScroll(
                    view: AbsListView?,
                    firstVisibleItem: Int,
                    visibleItemCount: Int,
                    totalItemCount: Int
                ) {
                }

                override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                }
            })
        }
    }

    fun GetIdBusqueda() :Int{
        var vBusquedas = oFuncionesGenerales.getSearchFromSharedPreferences() as ArrayList<Busquedas>

        var IdBusqueda: Int = 0
        var exist = true

        while (exist)
        {
            IdBusqueda = (0..900000).random()

            if(vBusquedas != null && vBusquedas.size > 0)
            {
                exist = vBusquedas.filter { r -> r.Id == IdBusqueda }.size > 0
            }else{
                exist = false
            }
        }

        return IdBusqueda
    }

    fun BuscarServidores(view:View)
    {

        var Filtro = if(findViewById<EditText>(R.id.etFiltroNombrePuesto).text != null) findViewById<EditText>(R.id.etFiltroNombrePuesto).text.toString() else ""
        var intent = Intent(this@HomeActivity, ListaServidoresActivity::class.java)

        intent.putExtra("filtro_nombre_servidor", Filtro)

       if(Filtro != "")
       {
           var vBusquedas = oFuncionesGenerales.getSearchFromSharedPreferences() as ArrayList<Busquedas>

           var vCurrentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

           var vCompleteDateSplitted = vCurrentDate.format(Date()).split(" ")
           var vDate = vCompleteDateSplitted.get(0)
           var vHour = vCompleteDateSplitted.get(1)
           var vDateSplitted = vDate.split("-")
           var iMonth = vDateSplitted.get(1).toInt()
           val sMonth = oFuncionesGenerales.months[iMonth - 1]
           var vFormattedDate = "${sMonth} ${vDateSplitted.get(2)}, ${vDateSplitted.get(0)}"
           var vHourSplitted = vHour.split(":")
           var vHourFormatted = "${vHourSplitted.get(0)}:${vHourSplitted.get(1)} Hrs."


           var vNewBusqueda = Busquedas(Filtro, "$vFormattedDate | $vHourFormatted", GetIdBusqueda())
           vBusquedas.add(vNewBusqueda)

           oFuncionesGenerales.saveSearchJson(vBusquedas)
       }

        startActivity(intent)
    }

    fun EliminarBusqueda(view: View)
    {

    }

    fun BusquedaAvanzada(view: View){
        var intent = Intent(this@HomeActivity, BusquedaAvanzadaActivity::class.java)
        startActivity(intent)
    }

}