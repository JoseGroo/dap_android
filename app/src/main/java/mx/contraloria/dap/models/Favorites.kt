package mx.contraloria.dap.models

import android.content.Context
import android.content.SharedPreferences
import android.support.design.widget.Snackbar
import android.view.View
import android.view.Window
import android.widget.ImageView
import mx.contraloria.dap.R
import java.lang.Exception


class Favorites(cTx: Context) {

    lateinit var sharedPreferences : SharedPreferences
    var MY_PREFS = "myPrefs"
    var V_KEY = "favorites"
    lateinit var context: Context
    init {
        this.context = cTx
       sharedPreferences = cTx.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
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

    fun InitPreferentImage(id: String,image: ImageView) {
        try{
            if(checkInPrefrence(id)){
                image.setImageResource(R.drawable.ic_star_yellow_24dp)
            }else{
                image.setImageResource(R.drawable.ic_star_border_black_24dp)
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