package mx.contraloria.dap

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import kotlinx.android.synthetic.main.activity_home_tab.*
import mx.contraloria.dap.Adapters.TabHomeAdapter
import mx.contraloria.dap.fragments.BuscarFragment
import mx.contraloria.dap.fragments.FavoritosFragment
import mx.contraloria.dap.models.Dependencias
import mx.contraloria.dap.models.Poderes
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.ArrayList

class HomeTabActivity : MyToolBarActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_tab)


        val adapter = TabHomeAdapter(supportFragmentManager)
        adapter.addFragment(BuscarFragment(), getString(R.string.str_buscar))
        adapter.addFragment(FavoritosFragment(), getString(R.string.str_favoritos))
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        val request = Request.Builder().url(getString(R.string.api_poderes_gobierno)).build()

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



        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listItems)
        spDependenciasGobierno.adapter = arrayAdapter

        spDependenciasGobierno.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                var iPosition: Int = if (position == 0)  position else position - 1
                if(position > 0)
                {
                    Toast.makeText(this@HomeTabActivity, "Seleccionado: " + response_json.get(iPosition).nombre, Toast.LENGTH_SHORT).show()
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
        var intent = Intent(this@HomeTabActivity, ListaServidoresActivity::class.java)
        intent.putExtra("filtro_dependencia_id",42)
        intent.putExtra("filtro_nombre_servidor","Pablo de Je")
        intent.putExtra("filtro_poder_id",0)
        startActivity(intent)
    }

}
