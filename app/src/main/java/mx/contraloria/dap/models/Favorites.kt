package mx.contraloria.dap.models

import android.content.Context
import android.content.SharedPreferences
import android.support.design.widget.Snackbar
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import mx.contraloria.dap.R
import java.lang.Exception
import android.R.attr.key
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken








class Favorites(cTx: Context) {

    lateinit var sharedPreferences : SharedPreferences
    var MY_PREFS = "myPrefs"
    var V_KEY = "favorites"
    lateinit var context: Context
    init {
        this.context = cTx
       sharedPreferences = cTx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
    }

    // JsonJsonJsonJsonJsonJsonJsonJsonJsonJsonJsonJsonJsonJsonJsonJsonJsonJsonJson
    fun saveServidoresSharedClass(model: Servidores,key: String): Boolean{
        try{
            val gson = Gson()
            val json = gson.toJson(model)
            val editor = sharedPreferences.edit()
            editor.putString(key, json)
            editor.commit()

        }catch (e: Exception){

            return false
        }

        return true
    }
    private fun getServidoresFromShared(key: String): Servidores {
        val gson = Gson()
        var productFromShared: Servidores
        val jsonPreferences = sharedPreferences.getString(key, "")

        val type = object : TypeToken<Servidores>() {}.type
        productFromShared = gson.fromJson(jsonPreferences, type)

        return productFromShared
    }
    fun saveFavoritosJson(list: List<Servidores>): Boolean{
        try{
            val gson = Gson()
            val json = gson.toJson(list)
            val editor = sharedPreferences.edit()
            editor.putString(V_KEY, json)
            editor.commit()

        }catch (e: Exception){

            return false
        }

        return true
    }
     fun getFavoritosFromSharedJsonToListServidores(): List<Servidores> {
        val gson = Gson()
        var productFromShared: List<Servidores> = ArrayList()
        try{
            val jsonPreferences = sharedPreferences.getString(V_KEY, "")
            val listType = object : TypeToken<ArrayList<Servidores>>() { }.type
            productFromShared = gson.fromJson(jsonPreferences, listType)
        }catch (e: Exception){

            print(e)
        }

        return productFromShared
    }

    fun AddDeleteFavoritosJson(view: View, servidores: Servidores,  text: TextView){

        var lista = getFavoritosFromSharedJsonToListServidores()

        if(checkInPrefrenceJson(servidores)){
            var newLista = lista.dropWhile { s -> s.id == servidores.id }
            saveFavoritosJson(newLista)
            text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_border_black_24dp,0,0,0)
            Snackbar.make(view,  servidores.nombre_completo+ " se elimino de favoritos.",Snackbar.LENGTH_LONG).show()


        }else{
            var ArrayLista = lista as ArrayList<Servidores>
            ArrayLista.add(servidores)
            saveFavoritosJson(ArrayLista)
            text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_yellow_24dp,0,0,0)
            Snackbar.make(view, "Se añadio a favoritos a: "+ servidores.nombre_completo, Snackbar.LENGTH_LONG).show()

        }

    }
    fun AddDeleteFavoritosJson(view: View, servidores: Servidores,  text: ImageView){

        var lista = getFavoritosFromSharedJsonToListServidores()

        if(checkInPrefrenceJson(servidores)){
            var newLista = lista.dropWhile { s -> s.id == servidores.id }
            saveFavoritosJson(newLista)
            text.setImageResource(R.drawable.ic_star_border_white_24dp)
            Snackbar.make(view,  servidores.nombre_completo+ " se elimino de favoritos.",Snackbar.LENGTH_LONG).show()


        }else{
            var ArrayLista = lista as ArrayList<Servidores>
            ArrayLista.add(servidores)
            saveFavoritosJson(ArrayLista)
            text.setImageResource(R.drawable.ic_star_white_24dp)
            Snackbar.make(view, "Se añadio a favoritos a: "+ servidores.nombre_completo, Snackbar.LENGTH_LONG).show()

        }

    }
    fun checkInPrefrenceJson(servidores: Servidores): Boolean {
        var lista = getFavoritosFromSharedJsonToListServidores()
        return  (lista.filter { s -> s.id ==  servidores.id}.count() > 0)
    }



    fun AddDeleteFavoritos(view: View, id_servidor: String, nombre_servidor: String, image: ImageView){


        if(!checkInPrefrence(id_servidor)){
            if(AddFavorite(id_servidor)){
                image.setImageResource(R.drawable.ic_star_yellow_24dp)
                Snackbar.make(view, "Se añadio a favoritos a: "+ nombre_servidor, Snackbar.LENGTH_LONG).show()
            }
        }else{

            if(deleteFromPreference(id_servidor)){
                image.setImageResource(R.drawable.ic_star_border_black_24dp)
                Snackbar.make(view,  nombre_servidor+ " se elimino de favoritos.",Snackbar.LENGTH_LONG).show()
            }
        }
    }
    fun AddDeleteFavoritosYext(view: View, id_servidor: String, nombre_servidor: String, text: TextView){


        if(!checkInPrefrence(id_servidor)){
            if(AddFavorite(id_servidor)){
                text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_yellow_24dp,0,0,0)
                Snackbar.make(view, "Se añadio a favoritos a: "+ nombre_servidor, Snackbar.LENGTH_LONG).show()
            }
        }else{

            if(deleteFromPreference(id_servidor)){
                text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_border_black_24dp,0,0,0)
                Snackbar.make(view,  nombre_servidor+ " se elimino de favoritos.",Snackbar.LENGTH_LONG).show()
            }
        }
    }


    fun InitPreferentImage(id: String,image: ImageView) {
        try{
            if(checkInPrefrence(id)){
                image.setImageResource(R.drawable.ic_star_white_24dp)
            }else{
                image.setImageResource(R.drawable.ic_star_border_white_24dp)
            }
        }catch (e:Exception){

        }

    }
    //imageview class
    fun InitPreferentImage(servidores: Servidores,image: ImageView) {
        try{
            if(checkInPrefrenceJson(servidores)){
                image.setImageResource(R.drawable.ic_star_white_24dp)
            }else{
                image.setImageResource(R.drawable.ic_star_border_white_24dp)
            }
        }catch (e:Exception){

        }

    }
    //textview class
    fun InitPreferentImage(servidores: Servidores,text: TextView) {
        try{
            if(checkInPrefrenceJson(servidores)){
                text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_white_24dp,0,0,0)
            }else{
                text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_border_white_24dp,0,0,0)
            }
        }catch (e:Exception){

        }

    }


    fun InitPreferentImageText(id: String,text: TextView) {
        try{
            if(checkInPrefrence(id)){
                text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_yellow_24dp,0,0,0)
            }else{
                text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_border_black_24dp,0,0,0)
            }
        }catch (e:Exception){

        }

    }
    fun AddFavorite(favoriteItem: String): Boolean {
        //Get previous favorite items
        var favoriteList =  getStringFromPreferences(V_KEY,"")
        // Append new Favorite item
        if (favoriteList != "") {
            favoriteList = favoriteList + "&" + favoriteItem
        } else {
            favoriteList = favoriteItem
        }
        // Save in Shared Preferences
        return putStringInPreferences(favoriteList,V_KEY)
    }

    fun getFavoriteList(): Array<String> {
        val favoriteList =  getStringFromPreferences(V_KEY,"")
        return convertStringToArray(favoriteList)
    }

    fun checkInPrefrence(Details: String): Boolean {
        val Favorites = getFavoriteList()
        for (i in Favorites.indices) {
            if (Favorites[i] == Details)
                return true
        }
        return false
    }
    fun deleteFromPreference(Details: String): Boolean {

        try{
            val Favorites = getFavoriteList()
            var favoriteString = ""
            for (i in Favorites.indices) {
                if (Favorites[i] != Details){
                    if(favoriteString !=""){
                        favoriteString += "&"+Favorites[i]
                    }else{
                        favoriteString = Favorites[i]
                    }

                }
            }
            return putStringInPreferences(favoriteString,V_KEY)
        }catch (e:Exception){
            return false
        }
    }

    fun clearPreferences() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()
    }

    fun checkIsClear(): Boolean {
        return if (getStringFromPreferences(V_KEY, "") == null)
            true
        else
            false
    }

    private fun putStringInPreferences(value: String, key: String): Boolean {
        try{
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.commit()
        }catch (e: Exception){
            return false
        }
        return true
    }
    fun getStringFromPreferences(key: String,defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue)
    }

    private fun convertStringToArray(str: String): Array<String> {
        return str.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

}