package mx.contraloria.dap


import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import mx.contraloria.dap.Adapters.ServidorAdapter
import mx.contraloria.dap.models.Favorites
import mx.contraloria.dap.models.Servidores
import java.lang.Exception
import android.util.Pair as UtilPair

class FavoritosActivity : MyToolBarActivity(){

    lateinit var txtTotal : TextView
    lateinit var txtSearch : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        var listView = findViewById<ListView>(R.id.listFavoritos)
        txtTotal = findViewById(R.id.txt_numero_busqueda_favoritos)
        txtSearch = findViewById(R.id.btnSearch_favoritos)
        fetchJason(listView)
    }
    //Pablo Fragment
    fun fetchJason(listView: ListView){



        //evento onScroll de la lista

        runOnUiThread {
            var favoritos = Favorites(this)
            var lista = favoritos.getFavoritosFromSharedJsonToListServidores()
            var adapter = ServidorAdapter(this,lista,true,txtTotal)
            listView.adapter = adapter
            //agregamos un respaldo de lista
            var ListaxFiltros = ArrayList<Servidores>()
            ListaxFiltros.addAll(lista)

            //hacemos los conteos necesarios
            var countRows = listView.count.toString()
            var tFavoritos = if(countRows > "1" || countRows == "0") " Favoritos" else " Favorito"
            txtTotal.text = countRows +tFavoritos
            txtSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    ListaServidoresActivity.doAsync {

                        this@FavoritosActivity.runOnUiThread(Runnable {
                            try {

                                var valor = p0.toString().toLowerCase()
                                var filtros = ListaxFiltros.filter { s ->
                                    s.nombre_completo.toLowerCase().contains(valor) ||
                                            s.dependencia.toLowerCase().contains(valor) ||
                                            s.puesto_funcional.toLowerCase().contains(valor) ||
                                            s.puesto_oficial.toLowerCase().contains(valor) ||
                                            s.titulo.toLowerCase().toLowerCase().contains(valor)
                                }
                                var xlista = lista as ArrayList<Servidores>
                                xlista.removeAll(lista)
                                xlista.addAll(filtros)
                                adapter.notifyDataSetChanged()
                            } catch (e: Exception) {
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
                }
            })
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
                }
            })

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
