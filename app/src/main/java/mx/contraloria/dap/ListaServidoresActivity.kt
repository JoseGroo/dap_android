package mx.contraloria.dap

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import kotlinx.android.synthetic.main.row.view.*
import kotlinx.android.synthetic.main.row_swipe.view.*
import mx.contraloria.dap.Adapters.MyListAdapter2
import mx.contraloria.dap.Adapters.ServidorAdapter
import mx.contraloria.dap.models.FuncionesGenerales
import mx.contraloria.dap.models.Servidores
import java.io.Serializable
import java.util.jar.Manifest
import android.util.Pair as UtilPair



class ListaServidoresActivity : MyToolBarActivity(){


    lateinit var listView: ListView
    val REQUEST_PHONE_CALL = 1
    lateinit var btnAnimation : Animation
    var filtro_dependencia_id=""
    var filtro_nombre_servidor =""
    var filtro_poder_id=""
    var pages= 1
    var flag_loading=false
    lateinit var adaptador: ServidorAdapter
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



        buscarFloating.setOnClickListener {
            oFuncionesGenerales.goIndex(buscarFloating)
        }

        fetchJason()
    }

    fun fetchJason(){
        var progress = progreessBar()
        progress.show()


        val url = getString(R.string.api_lista_servidores) +
                "filterrific[con_dependencia_id]="+ filtro_dependencia_id+
                "&filterrific[buscar_por_nombre]="+ filtro_nombre_servidor+
                "&filterrific[con_detalle]=1&pagina="+pages

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
                val listType = object : TypeToken<ArrayList<Servidores>>() { }.type
                var newList = gson.fromJson<ArrayList<Servidores>>(body, listType)

                runOnUiThread {
                    var adapter = ServidorAdapter(this@ListaServidoresActivity,newList)
                    listView.adapter = adapter
                    progress.dismiss()
                    listView.setOnScrollListener(object : AbsListView.OnScrollListener {

                        override fun onScroll(
                            view: AbsListView?,
                            firstVisibleItem: Int,
                            visibleItemCount: Int,
                            totalItemCount: Int
                        ) {
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
                                newList.size - 1 && !isLoading){
                                isLoading=true
                                Snackbar.make(listView, "Cargando mas registros.", Snackbar.LENGTH_LONG).show()
                                doAsync {
                                    pages = pages + 1
                                    var new_list = addMoreItems(newList)

                                    this@ListaServidoresActivity.runOnUiThread(java.lang.Runnable {
                                        newList.addAll(new_list)
                                        adapter.notifyDataSetChanged()
                                        isLoading = false
                                    })

                                }.execute()

                            }


                        }
                    })

                    listView.setOnItemClickListener { parent, view, position, id ->

                        try{

                            var intent = Intent(this@ListaServidoresActivity, DetalleServidorActivity::class.java)
                            intent.putExtra("servidor", newList.get(position) as Serializable)
                                // Check if we're running on Android 5.0 or higher
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                                    val options = ActivityOptions.makeSceneTransitionAnimation(this@ListaServidoresActivity,
                                        UtilPair.create<View,String>(view.iImageR, "imageTransition"),
                                        UtilPair.create<View,String>(view.txtNombreR, "nombreTransition"))

                                    startActivity(intent,options.toBundle())
                                } else {
                                    // Swap without transition
                                    startActivity(intent)
                                }


                        }catch (e:Exception){
                            Toast.makeText(
                                this@ListaServidoresActivity,
                                "Error al momento de mostrar el detalle del servidor.",
                                Toast.LENGTH_LONG
                            ).show()
                        }


                    }
                }



            }

        })
    }
    //loadMore
    fun addMoreItems(servidores: List<Servidores>): List<Servidores> {

        //var progress = progreessBar()
        var newList_prob : List<Servidores> = servidores
       // progress.show()
        val url = getString(R.string.api_lista_servidores) +
                "filterrific[con_dependencia_id]="+ filtro_dependencia_id+
                "&filterrific[buscar_por_nombre]="+ filtro_nombre_servidor+
                "&filterrific[con_detalle]=1&pagina="+pages


        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        try {
            //mode strict
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            var response = client.newCall(request).execute()
            val body = response?.body?.string()
            val gson = Gson()
            val listType = object : TypeToken<List<Servidores>>() { }.type
            var newList_prob = gson.fromJson<List<Servidores>>(body, listType)
            return newList_prob
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



