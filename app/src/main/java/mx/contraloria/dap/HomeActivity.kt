package mx.contraloria.dap

import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : MyToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

    }

    fun BuscarServidores(view:View)
    {
        var filtro = etFiltroNombrePuesto.text.toString()
        var intent = Intent(this@HomeActivity, ListaServidoresActivity::class.java)
        intent.putExtra("filtro_nombre_servidor", filtro)
        intent.putExtra("busqueda_avanzada", false)
        startActivity(intent)
    }

    fun BusquedaAvanzada(view: View){
        var intent = Intent(this@HomeActivity, HomeTabActivity::class.java)
        startActivity(intent)
    }

}