package mx.contraloria.dap

import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.util.AttributeSet
import android.util.Pair
import android.view.View
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import kotlinx.android.synthetic.main.activity_home_tab.*
import kotlinx.android.synthetic.main.row_swipe.view.*
import mx.contraloria.dap.Adapters.ServidorAdapter
import mx.contraloria.dap.Adapters.TabHomeAdapter
import mx.contraloria.dap.fragments.BuscarFragment
import mx.contraloria.dap.fragments.FavoritosFragment
import mx.contraloria.dap.models.*
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class HomeTabActivity : MyToolBarActivity() {

    var poder_id = 0
    var dependencia_id = 0
    var nombre_servidor = ""
    lateinit var vDependencias: ArrayList<Dependencias>
    lateinit var vPoderes: ArrayList<Poderes>

    lateinit var oFuncionesGenerales: FuncionesGenerales

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_tab)

        oFuncionesGenerales = FuncionesGenerales(this@HomeTabActivity)




        val adapter = TabHomeAdapter(supportFragmentManager)
        adapter.addFragment(BuscarFragment(), getString(R.string.str_buscar))
        adapter.addFragment(FavoritosFragment(), getString(R.string.str_favoritos))

        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        val request = Request.Builder().url(getString(R.string.api_poderes_gobierno)).build()

        tabs.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                 when(tab.position){

                     1 -> {
                         val listView = findViewById<ListView>(R.id.listFavoritos)
                         fetchJason(listView)
                     }
                 }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })
        var client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback{

            override fun onResponse(call: okhttp3.Call, response: Response) {

                val body = response?.body?.string()
                ///////cuando es solo un dato//////
                //val model =  Gson().fromJson(json, mList::class.java)

                //////cuando es mas de un solo los pone en arrays///////
                ////val gson = Gson()
                //val founderArray = gson.fromJson(body, Array<mList>::class.java)

                //////cuando es mas de un solo los pone en lista////////
                val gson = Gson()
                val listType = object : TypeToken<List<Poderes>>() { }.type
                val poderesGobiernoJson = gson.fromJson<List<Poderes>>(body, listType)
                runOnUiThread {
                    LlenarPoderesGobierno(poderesGobiernoJson)
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("fallo peticion ")
                println(e)
                //Toast.makeText(this@HomeTabActivity, "Fallo al recolectar la información" , Toast.LENGTH_SHORT).show()
            }
        })


    }



    fun LlenarPoderesGobierno(response_json: List<Poderes>)
    {
        var listItems = ArrayList<String>()
        listItems.add("TODOS")
        for (i in 0 until response_json.size) {
            listItems.add(response_json.get(i).name)
        }

        vPoderes = response_json as ArrayList<Poderes>

        val spPoderesGobierno = findViewById<Spinner>(R.id.spPoderesGobierno)

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listItems)
        spPoderesGobierno.adapter = arrayAdapter

        var progressDialog = ProgressDialog(this@HomeTabActivity)
        progressDialog.setMessage(getString(R.string.str_cargando))
        progressDialog.setCancelable(false)

        spPoderesGobierno.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {


                progressDialog.show()

                var iPosition: Int = if (position == 0)  position else position - 1

                var iIdPoder = if (position == 0) "" else response_json.get(iPosition).id.toString()

                val request = Request.Builder().url(getString(R.string.api_dependencias_gobierno) + iIdPoder).build()

                poder_id = if (iIdPoder != "") iIdPoder.toInt() else 0

                var client = OkHttpClient()
                client.newCall(request).enqueue(object: Callback {

                    override fun onResponse(call: okhttp3.Call, response: Response) {

                        val body = response?.body?.string()

                        val gson = Gson()
                        val listType = object : TypeToken<List<Dependencias>>() { }.type
                        val vDependenciasJson = gson.fromJson<List<Dependencias>>(body, listType)
                        runOnUiThread {
                            LlenarDependenciasGobierno(vDependenciasJson)
                            progressDialog.dismiss()
                        }
                    }

                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        println("fallo peticion")
                        progressDialog.dismiss()
                        Toast.makeText(this@HomeTabActivity, "Fallo al recolectar la información" , Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
                Toast.makeText(this@HomeTabActivity, "No entro compa: " , Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun LlenarDependenciasGobierno(response_json: List<Dependencias>)
    {
        var listItems = ArrayList<String>()
        listItems.add("TODOS")
        val spDependenciasGobierno = findViewById<Spinner>(R.id.spDependenciasGobierno)

        if(response_json != null && response_json.size > 0)
        {
            for (i in 0 until response_json.size) {
                listItems.add(response_json.get(i).nombre)
            }
        }




        vDependencias = response_json as ArrayList<Dependencias>

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listItems)
        spDependenciasGobierno.adapter = arrayAdapter

        /*var actv = findViewById<AutoCompleteTextView>(R.id.autocompleteDependencia);
        actv.setThreshold(1)
        actv.setAdapter(arrayAdapter)*/

        spDependenciasGobierno.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                var iPosition: Int = if (position == 0)  position else position - 1
                dependencia_id = 0
                if(position > 0)
                {
                    dependencia_id = response_json.get(iPosition).id.toInt()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
                Toast.makeText(this@HomeTabActivity, "No entro compa: " , Toast.LENGTH_SHORT).show()
            }
        }

        //progressDialog.dismiss()
    }

    fun BuscarServidores(view:View)
    {
        var Filtro = if(findViewById<EditText>(R.id.etFiltroNombrePuesto).text != null) findViewById<EditText>(R.id.etFiltroNombrePuesto).text.toString() else ""
        var intent = Intent(this@HomeTabActivity, ListaServidoresActivity::class.java)
        intent.putExtra("filtro_dependencia_id",dependencia_id)
        intent.putExtra("filtro_nombre_servidor", Filtro)
        intent.putExtra("filtro_poder_id",poder_id)

        var vBusquedas = oFuncionesGenerales.getSearchFromSharedPreferences() as ArrayList<Busquedas>

        var vNewBusqueda = Busquedas()
        vNewBusqueda.Busqueda = Filtro
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


        vNewBusqueda.Fecha = "$vFormattedDate | $vHourFormatted"
        var vDependenciaSeleccionada = vDependencias.filter { r -> r.id.toInt() == dependencia_id }.firstOrNull()
        var vPoderSeleccionado = vPoderes.filter { r -> r.id == poder_id.toString() }.firstOrNull()
        vNewBusqueda.Dependencia = if(vDependenciaSeleccionada != null) vDependenciaSeleccionada!!.iniciales else ""
        vNewBusqueda.Poder = if(vPoderSeleccionado != null) vPoderSeleccionado!!.name else ""

        vBusquedas.add(vNewBusqueda)

        oFuncionesGenerales.saveSearchJson(vBusquedas)

        startActivity(intent)
    }

    fun LimpiarFiltros(view: View)
    {
        var etFiltroNombrePuesto = findViewById<EditText>(R.id.etFiltroNombrePuesto)
        etFiltroNombrePuesto.setText("")

        var sPoderesGobierno = findViewById<Spinner>(R.id.spPoderesGobierno)
        sPoderesGobierno.setSelection(0, true)

        var spDependenciasGobierno = findViewById<Spinner>(R.id.spDependenciasGobierno)
        spDependenciasGobierno.setSelection(0, true)
    }

    //Pablo Fragment
    fun fetchJason(listView: ListView){


            var favoritos = Favorites(this)
            var lista = favoritos.getFavoritosFromSharedJsonToListServidores()
            var adapter = ServidorAdapter(this,lista,true,null)
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


            listView.setOnItemClickListener { parent, view, position, id ->

                try{
                    //Creamos el evento de Favoritos
                    //Favoritos



                    var intent = Intent(this, DetalleServidorActivity::class.java)
                    intent.putExtra("servidor", lista.get(position) as Serializable)
                    // Check if we're running on Android 5.0 or higher
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        val options = ActivityOptions.makeSceneTransitionAnimation(this,
                            Pair.create<View,String>(view.iImageR, "imageTransition"),
                            Pair.create<View,String>(view.txtNombreR, "nombreTransition"))

                        startActivity(intent,options.toBundle())
                    } else {
                        // Swap without transition
                        startActivity(intent)
                    }


                }catch (e: Exception){
                    Toast.makeText(
                        this,
                        "Error al momento de mostrar el detalle del servidor.",
                        Toast.LENGTH_LONG
                    ).show()
                }


            }


    }
    fun progreessBar(): ProgressDialog {
        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.str_cargando))
        progressDialog.setCancelable(false)
        return progressDialog

    }

    class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            handler()
            return null
        }
    }



}
