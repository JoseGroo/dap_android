package mx.contraloria.dap.fragments


import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ListView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.row_swipe.view.*
import mx.contraloria.dap.Adapters.MyListAdapter2
import mx.contraloria.dap.Adapters.ServidorAdapter
import mx.contraloria.dap.DetalleServidorActivity
import mx.contraloria.dap.ListaServidoresActivity
import mx.contraloria.dap.R
import mx.contraloria.dap.models.Favorites
import mx.contraloria.dap.models.Servidores
import mx.contraloria.dap.models.mList
import okhttp3.*
import java.io.IOException
import java.io.Serializable
import java.lang.Exception

class FavoritosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return  inflater.inflate(R.layout.fragment_favoritos, container, false)
    }






}
