package mx.contraloria.dap

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.media.Image
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.daimajia.swipe.SwipeLayout
import mx.contraloria.dap.models.mList
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_lista_servidores.*
import kotlinx.android.synthetic.main.pruebas_relatives.*
import kotlinx.android.synthetic.main.row.view.*
import kotlinx.android.synthetic.main.row_swipe.view.*
import mx.contraloria.dap.Adapters.MyListAdapter2
import mx.contraloria.dap.Adapters.ServidorAdapter
import mx.contraloria.dap.models.FuncionesGenerales
import mx.contraloria.dap.models.ResultadosServidores
import mx.contraloria.dap.models.Servidores
import java.io.Serializable
import java.text.DecimalFormat
import java.util.jar.Manifest
import android.util.Pair as UtilPair



class ListaServidoresActivity : MyToolBarActivity(){


    lateinit var listView: ListView
    lateinit var btnSearch: EditText
    lateinit var txtFiltrosBusqueda: TextView
    val REQUEST_PHONE_CALL = 1
    var filtro_dependencia_id=""
    var filtro_nombre_servidor =""
    var filtro_poder_id=""
    var pages= 1
    var isLoading: Boolean = false


    var oFuncionesGenerales = FuncionesGenerales(this)


    private fun setupPermissions() {
        val permission = checkSelfPermission(this,
            android.Manifest.permission.CALL_PHONE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE),REQUEST_PHONE_CALL)
        }

        val permission_extorage = checkSelfPermission(this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission_extorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        val permission_read_extorage = checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission_read_extorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_servidores)

        filtro_dependencia_id = if(intent.getIntExtra("filtro_dependencia_id",0) == 0) "" else intent.getIntExtra("filtro_dependencia_id",0).toString()
        filtro_nombre_servidor = intent.getStringExtra("filtro_nombre_servidor")
        filtro_poder_id = if(intent.getIntExtra("filtro_poder_id",0) == 0) "" else intent.getIntExtra("filtro_poder_id",0).toString()
        setupPermissions()

        listView = findViewById(R.id.list)
        btnSearch =findViewById(R.id.btnSearch)
        txtFiltrosBusqueda = findViewById(R.id.txt_filtro_busqueda)
        /*val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val screenHeight = size.y
        bottom_white_background.translationY = 4000f*/


        fetchJason()
    }

    fun fetchJason(){
        var progress = progreessBar()
        progress.show()


        /*val url = getString(R.string.api_lista_servidores) +
                "filterrific[con_dependencia_id]="+ filtro_dependencia_id+
                "&filterrific[buscar_por_nombre]="+ filtro_nombre_servidor+
                "&filterrific[con_detalle]=1&pagina="+pages*/
        val url = getString(R.string.api_lista_servidores) +
                "&filterrific[buscar_por_todo]="+ filtro_nombre_servidor+
                "&filterrific[con_detalle]=1"+
                "&con_totales=1"+
                "&pagina="+pages

        //lo ponemos en el titulo aabajo de resultados
        txtFiltrosBusqueda.text = if ( filtro_nombre_servidor != "" ) filtro_nombre_servidor else "TODOS"

        //damos formato a los totatles
        val formatoDecimales = DecimalFormat("#,###,###")
        var mostrando=""


        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                progress.dismiss()
                Toast.makeText(
                    this@ListaServidoresActivity,
                    "Error al conectarse con la API",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()
                val gson = Gson()
                val listType = object : TypeToken<ResultadosServidores>() { }.type
                var newList = gson.fromJson<ResultadosServidores>(body, listType)
                var ListaxFiltros = ArrayList<Servidores>()
                ListaxFiltros.addAll(newList.resultados)

                runOnUiThread {
                    var adapter= ServidorAdapter(this@ListaServidoresActivity,newList.resultados,false,null)
                    listView.adapter = adapter


                    txt_numero_busqueda.setText(formatoDecimales.format(newList.total))
                    mostrando = " "+formatoDecimales.format(newList.resultados.count()) + " - " + formatoDecimales.format(newList.total) +" resultados."
                    txt_mostrando_resultados.setText(mostrando)
                    progress.dismiss()
                    //animacion de fondo

                    btnSearch.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(p0: Editable?) {
                            doAsync {

                                this@ListaServidoresActivity.runOnUiThread(java.lang.Runnable {
                                    try{

                                        var valor = p0.toString().toLowerCase()
                                        var filtros = ListaxFiltros.filter { s ->
                                            s.nombre_completo.toLowerCase().contains(valor) ||
                                                    s.dependencia.toLowerCase().contains(valor) ||
                                            s.puesto_funcional.toLowerCase().contains(valor) ||
                                            s.puesto_oficial.toLowerCase().contains(valor) ||
                                            s.titulo.toLowerCase().toLowerCase().contains(valor)}
                                        newList.resultados.removeAll(newList.resultados)
                                        newList.resultados.addAll(filtros)
                                        adapter.notifyDataSetChanged()
                                    }catch (e: Exception){
                                        print(e)
                                    }
                                })
                            }.execute()
                        }

                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {






                        }
                    })
                    listView.setOnScrollListener(object : AbsListView.OnScrollListener {

                        override fun onScroll(
                            view: AbsListView?,
                            firstVisibleItem: Int,
                            visibleItemCount: Int,
                            totalItemCount: Int
                        ) {
                           /* vAntiguo++
                            if(vAntiguo >= 6){
                                nFloating -=5f
                                if(nFloating >= 1400f){
                                    LnLBackground.animate().translationY(nFloating).start()
                                }

                                Toast.makeText(this@ListaServidoresActivity,vAntiguo.toString(),Toast.LENGTH_SHORT).show()

                            }*/




                        }


                        override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                            when (scrollState) {

                                AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> {
                                    adapter.mBusy=false
                                    adapter.notifyDataSetChanged()
                                }
                                AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> adapter.mBusy = true
                                AbsListView.OnScrollListener.SCROLL_STATE_FLING -> adapter.mBusy = true


                            }




                            //scroooll
                            if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && listView.getLastVisiblePosition() ==
                                newList.resultados.size - 1 && !isLoading){
                                isLoading=true
                                var vSnack = Snackbar.make(listView, "Cargando mas registros.", Snackbar.LENGTH_INDEFINITE)
                                vSnack.show()
                                doAsync {
                                    pages = pages + 1
                                    var new_list = addMoreItems(newList.resultados)

                                    this@ListaServidoresActivity.runOnUiThread(java.lang.Runnable {
                                        newList.resultados.addAll(new_list)
                                        ListaxFiltros.addAll(new_list)
                                        adapter.notifyDataSetChanged()
                                        mostrando = " "+newList.resultados.count().toString() + " - " + newList.total.toString()+" resultados."
                                        txt_mostrando_resultados.setText(mostrando)
                                        isLoading = false
                                        if(vSnack.isShown){
                                            vSnack.dismiss()
                                        }
                                    })

                                }.execute()

                            }


                        }
                    })


                }



            }

        })
    }
    //loadMore
    fun addMoreItems(servidores: List<Servidores>): List<Servidores> {

        //var progress = progreessBar()
        var newList_prob : List<Servidores> = servidores
       // progress.show()
        /*val url = getString(R.string.api_lista_servidores) +
                "filterrific[con_dependencia_id]="+ filtro_dependencia_id+
                "&filterrific[buscar_por_nombre]="+ filtro_nombre_servidor+
                "&filterrific[con_detalle]=1&pagina="+pages*/
        val url = getString(R.string.api_lista_servidores) +
                "&filterrific[buscar_por_todo]="+ filtro_nombre_servidor+
                "&filterrific[con_detalle]=1"+
                "&con_totales=1"+
                "&pagina="+pages


        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        try {
            //mode strict
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            var response = client.newCall(request).execute()
            val body = response?.body?.string()
            val gson = Gson()
            val listType = object : TypeToken<ResultadosServidores>() { }.type
            var newList_prob = gson.fromJson<ResultadosServidores>(body, listType)
            return newList_prob.resultados
        }catch (e: Exception){
            //progress.dismiss()
            Toast.makeText(
                this@ListaServidoresActivity,
                "Error al conectarse con la API",
                Toast.LENGTH_LONG
            ).show()
        }
        //progress.dismiss()
        return newList_prob

    }


    fun progreessBar(): ProgressDialog{
        var progressDialog = ProgressDialog(this@ListaServidoresActivity)
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



