package mx.contraloria.dap

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_detalle_servidor.*
import mx.contraloria.dap.models.Dependencias
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class DetalleServidorActivity : MyToolBarActivity() {

    var progressDialog = ProgressDialog(this@DetalleServidorActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_servidor)

        progressDialog.setMessage(getString(R.string.str_cargando))
        progressDialog.setCancelable(false)

        var sId = "" //aqui va el id
        val request = Request.Builder().url(getString(R.string.api_detalles_servidor_publico) + sId).build()


        var client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {

            override fun onResponse(call: okhttp3.Call, response: Response) {

                val body = response?.body()?.string()

                val gson = Gson()
                val listType = object : TypeToken<List<Dependencias>>() { }.type
                val vDependenciasJson = gson.fromJson<List<Dependencias>>(body, listType)
                runOnUiThread {
                    progressDialog.dismiss()
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("fallo peticion")
            }
        })

        tvNombreServidor.setText("José Manuel Guerrero Sánchez")
        tvPuestoOficial.setText("Jefe de departamento")
        tvFechaAlta.setText("16-10-2016")
        tvNivel.setText("9C")
        tvResena.setText("INGENIERO EN SISTEMAS COMPUTACIONALES POR EL ITH DEL AÑO 2015, EN EL AÑO 2015 SE DESEMPEÑÓ EN LA EMPRESA CREDIUNO DONDE OCUPÓ EL CARGO DE: DESARROLLADOR DE SOFTWARE, DEL AÑO 2015 AL 2016 SE DESEMPEÑÓ EN LA EMPRESA GRUPO LAN EDUCATION, OCUPO EL CARGO DE: DESARROLLADOR JUNIOR. ACTUALMENTE SE DESEMPEÑA EN LA SECRETARIA DE LA CONTRALORÍA GENERAL CON EL CARGO DE: JEFE DE DEPARTAMENTO.")
        tvDependencia.setText("Secretaria de la contraloria general")
        tvUnidadAdministrativa.setText("Subsecretaria de desarrollo administrativo y tecnologico")
        tvPuestoFuncional.setText("Jefe de departamento de planeación de tecnologia")
    }
}
