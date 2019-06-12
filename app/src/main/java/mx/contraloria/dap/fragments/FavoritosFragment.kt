package mx.contraloria.dap.fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mx.contraloria.dap.Adapters.MyListAdapter2
import mx.contraloria.dap.DetalleServidorActivity
import mx.contraloria.dap.R
import mx.contraloria.dap.models.mList
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class FavoritosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favoritos, container, false)
    }


}
