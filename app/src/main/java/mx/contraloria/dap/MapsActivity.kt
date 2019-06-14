package mx.contraloria.dap

import android.location.Address
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.location.Geocoder
import java.io.IOException

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detalle_servidor.*
import mx.contraloria.dap.Adapters.CircleTransform
import mx.contraloria.dap.models.FuncionesGenerales
import mx.contraloria.dap.models.Servidores


class MapsActivity : MyToolBarActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    lateinit var vServidor: Servidores
    var oFuncionesGenerales = FuncionesGenerales(this)
    lateinit var vCustomLayout: View
    lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        vServidor = intent.extras.get("servidor") as Servidores

        vCustomLayout = layoutInflater.inflate(R.layout.custom_marker_info_window, null)
        val tvServidor = vCustomLayout.findViewById(R.id.info_window_nombre_servidor) as TextView
        tvServidor.isSelected = true
        tvServidor.setText(oFuncionesGenerales.NormalizerTextNames(vServidor.nombre_completo))

        val tvPuesto = vCustomLayout.findViewById(R.id.info_window_puesto) as TextView
        tvPuesto.isSelected = true
        tvPuesto.setText(oFuncionesGenerales.NormalizerTextNames(vServidor.puesto_oficial))

        val tvDireccion = vCustomLayout.findViewById(R.id.info_window_domicilio) as TextView
        tvDireccion.isSelected = true
        tvDireccion.setText(vServidor.domicilio)



        if(vServidor.foto != ""){
            try
            {
                val ivImagePerfil = vCustomLayout.findViewById(R.id.info_window_imagen) as ImageView
                Picasso.with(this@MapsActivity)
                    .load(vServidor.foto)

                    .transform(CircleTransform())
                    .into(ivImagePerfil)

            }
            catch (e: IOException)
            {
                print(e)
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        /*mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/

        mMap = googleMap

        vServidor = intent.extras.get("servidor") as Servidores

        val geocoder = Geocoder(this@MapsActivity)
        var list: List<Address> = ArrayList()
        try {
            list = geocoder.getFromLocationName(vServidor.domicilio, 1)
        } catch (e: IOException) {

        }

        if (list.size > 0) {
            val address = list[0]
            moveCamera(LatLng(address.getLatitude(), address.getLongitude()), 15f,address.getAddressLine(0))
        }

        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                return vCustomLayout
            }
        })


        //Thread.sleep(3000)
        //marker.showInfoWindow()

    }


    private fun moveCamera(latLng: LatLng, zoom: Float, title: String) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))

        if (title != "My Location") {
            val options = MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet("Snippet")
            marker = mMap.addMarker(options)

        }

    }
}
