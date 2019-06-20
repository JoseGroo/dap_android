package mx.contraloria.dap

import android.app.ActionBar
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlin.system.exitProcess
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_busqueda.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_busqueda.*


abstract class MyToolBarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
lateinit var drawer_layout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when(this){
            BusquedaActivity::class.java ->{
                setContentView(R.layout.activity_busqueda)
            }
            else -> {
                setContentView(R.layout.activity_busqueda)
            }
        }


        drawer_layout = findViewById(R.id.drawer_layout)


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        val rootView: ViewGroup = findViewById(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r);

            val heightDiff = rootView.rootView.height - (r.bottom - r.top);

            footer.visibility = if (heightDiff > rootView.rootView.height / 4) View.INVISIBLE else View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        var imagen = findViewById<ImageView>(R.id.bottom_white_background)

        if(imagen != null)
        {
            val rnds = (1000..1800).random()
            imagen.animate().translationY(rnds.toFloat()).setStartDelay(1000).setDuration(1000).start()
        }
    }

    fun BackButtonCustom(view: View)
    {
        onBackPressed()
    }

    fun OpenMenu(view: View){
        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        if(drawer != null)
        {
            drawer.openDrawer(Gravity.LEFT)

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


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.busqueda, menu)
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
                Toast.makeText(
                    this,
                    "Camara...",
                    Toast.LENGTH_LONG
                ).show()
            }
            R.id.nav_gallery -> {
                Toast.makeText(
                    this,
                    "nav gallery...",
                    Toast.LENGTH_LONG
                ).show()
            }
            R.id.nav_slideshow -> {
                Toast.makeText(
                    this,
                    "nav slideshow...",
                    Toast.LENGTH_LONG
                ).show()
            }
            R.id.nav_manage -> {
                Toast.makeText(
                    this,
                    "nav manage...",
                    Toast.LENGTH_LONG
                ).show()
            }
            R.id.nav_share -> {
                Toast.makeText(
                    this,
                    "nav share...",
                    Toast.LENGTH_LONG
                ).show()
            }
            R.id.nav_send -> {
                Toast.makeText(
                    this,
                    "nav send...",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
