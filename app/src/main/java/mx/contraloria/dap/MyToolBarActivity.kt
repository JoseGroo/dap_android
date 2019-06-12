package mx.contraloria.dap

import android.app.ActionBar
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlin.system.exitProcess
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.Toolbar





abstract class MyToolBarActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tool_bar)

        supportActionBar!!.title = getString(R.string.app_name)
        if(this !is HomeActivity)
        {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }else if(this is HomeActivity)
        {
            //supportActionBar!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
            //supportActionBar!!.setCustomView(R.layout.my_toolbar)

        }
    }
/*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_view, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.design_menu_item_action_area_stub) {
            val intent = Intent(this, DetalleServidorActivity::class.java)
            startActivity(intent)
            return true
        }
        when(item.itemId) {
            R.id.nav_acerca_de -> {
                startActivity(Intent(this, DetalleServidorActivity::class.java))
            }
            R.id.nav_inicio -> {
                startActivity(Intent(this, HomeActivity::class.java))
            }
            R.id.nav_salir ->{
                moveTaskToBack(true);
                exitProcess(-1)
                finishAffinity()
            }
        }

        return super.onOptionsItemSelected(item)
    }
*/
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
