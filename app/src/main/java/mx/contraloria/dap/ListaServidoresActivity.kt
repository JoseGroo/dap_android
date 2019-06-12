package mx.contraloria.dap

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
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
import kotlinx.android.synthetic.main.row.view.*
import kotlinx.android.synthetic.main.row_swipe.view.*
import mx.contraloria.dap.Adapters.MyListAdapter2
import mx.contraloria.dap.models.Servidores
import java.io.Serializable
import java.util.jar.Manifest
import android.util.Pair as UtilPair


class ListaServidoresActivity : MyToolBarActivity() {


    lateinit var listView: ListView
    val REQUEST_PHONE_CALL = 1
    lateinit var btnAnimation : Animation
    var filtro_dependencia_id=0
    var filtro_nombre_servidor=""
    var filtro_poder_id=0
    var clicks= 1

    private fun setupPermissions() {
        val permission = checkSelfPermission(this,
            android.Manifest.permission.CALL_PHONE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE),REQUEST_PHONE_CALL)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_servidores)

        filtro_dependencia_id = intent.getIntExtra("filtro_dependencia_id",0)
        filtro_nombre_servidor = intent.getStringExtra("filtro_nombre_servidor")
        filtro_poder_id = intent.getIntExtra("filtro_poder_id",0)
        setupPermissions()

        listView = findViewById(R.id.list)



        fetchJason()
    }

    fun fetchJason(){
        //val url = "http://dummy.restapiexample.com/api/v1/employees"
        //http://dap.sonora.gob.mx/api/v1/servidores?filterrific[c%20on_dependencia_id]=0&filterrific[buscar_por_nombre]=jesus%20soto&page=1
        val url = getString(R.string.api_lista_servidores) +
                "filterrific[con_dependencia_id]="+ filtro_dependencia_id+
                "&filterrific[buscar_por_nombre]="+ filtro_nombre_servidor+"&page=1"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(
                    this@ListaServidoresActivity,
                    "Error al conectarse con la API",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()
                ///////cuando es solo un dato//////
                //val model =  Gson().fromJson(json, mList::class.java)

                //////cuando es mas de un solo los pone en arrays///////
                ////val gson = Gson()
                //val founderArray = gson.fromJson(body, Array<mList>::class.java)

                //////cuando es mas de un solo los pone en lista////////
                val gson = Gson()

                //example




                val listType = object : TypeToken<List<Servidores>>() { }.type
                val newList = gson.fromJson<List<Servidores>>(body, listType)


                runOnUiThread {
                    listView.adapter = MyListAdapter2(this@ListaServidoresActivity, R.layout.row_swipe, newList)

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

    fun goIndex(view: View){
        startActivity(Intent(this@ListaServidoresActivity, HomeActivity::class.java))
    }



    /*var sample1: SwipeLayout = findViewById(R.id.row_swipe_1)
       sample1.showMode = SwipeLayout.ShowMode.PullOut
       val starBottView: View = sample1.findViewById(R.id.starbott)
       sample1.addDrag(SwipeLayout.DragEdge.Left, sample1.findViewById(R.id.bottom_wrapper))
       sample1.addDrag(SwipeLayout.DragEdge.Right, sample1.findViewById(R.id.bottom_wrapper_2))
       sample1.addDrag(SwipeLayout.DragEdge.Top, starBottView)
       sample1.addDrag(SwipeLayout.DragEdge.Bottom, starBottView)
       sample1.addRevealListener(
           R.id.delete
       ) { child, edge, fraction, distance -> }

       sample1.surfaceView.setOnClickListener {
           Toast.makeText(this@ListaServidoresActivity, "Click on surface", Toast.LENGTH_SHORT).show()
           Log.d(ListaServidoresActivity::class.java!!.getName(), "click on surface")
       }
       sample1.surfaceView.setOnLongClickListener {
           Toast.makeText(this@ListaServidoresActivity, "longClick on surface", Toast.LENGTH_SHORT).show()
           Log.d(ListaServidoresActivity::class.java!!.getName(), "longClick on surface")
           true
       }*/
    /*sample1.findViewById(R.id.star2).setOnClickListener(View.OnClickListener {
        Toast.makeText(this@ListaServidoresActivity, "Star", Toast.LENGTH_SHORT).show()
    })

    sample1.findViewById(R.id.trash2).setOnClickListener(View.OnClickListener {
        Toast.makeText(this@ListaServidoresActivity, "Trash Bin", Toast.LENGTH_SHORT).show()
    })

    sample1.findViewById(R.id.magnifier2).setOnClickListener(View.OnClickListener {
        Toast.makeText(this@ListaServidoresActivity, "Magnifier", Toast.LENGTH_SHORT).show()
    })*/

    /* sample1.addRevealListener(
         R.id.starbott
     ) { child, edge, fraction, distance ->
         val star: View = child.findViewById(R.id.star)
         val d = (child.height / 2 - star.getHeight() / 2).toFloat()
         ViewHelper.setTranslationY(star, d * fraction)
         ViewHelper.setScaleX(star, fraction + 0.6f)
         ViewHelper.setScaleY(star, fraction + 0.6f)
     }*/

    /*val list = mutableListOf<mList>()

    list.add(
        mList(
            "DR. JOSE FRANCISCO LAM FELIX",
            "SERVICIOS DE SALUD DE SONORA / DIRECCION GENERAL DE ENSEÑANZA Y CALIDAD",
            "",
            R.mipmap.ic_launcher_round
        )
    )
    list.add(
        mList(
            "DR. JOSE FRANCISCO LAM FELIX",
            "SERVICIOS DE SALUD DE SONORA / DIRECCION GENERAL DE ENSEÑANZA Y CALIDAD",
            "",
            R.mipmap.ic_launcher_round
        )
    )
    list.add(
        mList(
            "DR. JOSE FRANCISCO LAM FELIX",
            "SERVICIOS DE SALUD DE SONORA / DIRECCION GENERAL DE ENSEÑANZA Y CALIDAD",
            "",
            R.mipmap.ic_launcher_round
        )
    )
    list.add(
        mList(
            "DR. JOSE FRANCISCO LAM FELIX",
            "SERVICIOS DE SALUD DE SONORA / DIRECCION GENERAL DE ENSEÑANZA Y CALIDAD",
            "",
            R.mipmap.ic_launcher_round
        )
    )
    list.add(
        mList(
            "DR. JOSE FRANCISCO LAM FELIX",
            "SERVICIOS DE SALUD DE SONORA / DIRECCION GENERAL DE ENSEÑANZA Y CALIDAD",
            "",
            R.mipmap.ic_launcher_round
        )
    )
    list.add(
        mList(
            "DR. JOSE FRANCISCO LAM FELIX",
            "SERVICIOS DE SALUD DE SONORA / DIRECCION GENERAL DE ENSEÑANZA Y CALIDAD",
            "",
            R.mipmap.ic_launcher_round
        )
    )

    listView.adapter = MyListAdapter(this, R.layout.row_contrain, list)

    listView.setOnItemClickListener { parent, view, position, id ->

        Toast.makeText(
            this@ListaServidoresActivity,
            list[position].nombre + ", posision: " + position,
            Toast.LENGTH_SHORT
        ).show()

    }*/

}


