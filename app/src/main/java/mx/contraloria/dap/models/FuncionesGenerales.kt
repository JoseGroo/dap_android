package mx.contraloria.dap.models

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import mx.contraloria.dap.R
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.util.*
import android.support.v4.content.ContextCompat.startActivity
import java.net.URLConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import mx.contraloria.dap.HomeTabActivity
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.net.URL


class FuncionesGenerales(cTx: Context) {

    val months = arrayOf("Ene.", "Feb.", "Mar.", "Abr.", "May.", "Jun.", "Jul.", "Ago.", "Sep.", "Oct.", "Nov.", "Dic.")


    var VCF_DIRECTORY: String  = "/vcf_demonuts"
    lateinit var vcfFile: File
    lateinit var context: Context
    init {
        this.context = cTx
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    fun goIndex(view: View){
        var intent = Intent(context, HomeTabActivity::class.java)
        startActivity(context, intent, null)
    }


    fun NormalizerTextDependencia(text: String): String{

        return text.substring(0,1).toUpperCase() + text.substring(1).toLowerCase()


    }

    fun NormalizerTextNames(name: String) : String{

        var new_name=""
        try{
            var splitName = name.split(" ")
            for (i in splitName.indices) {
                when(splitName[i]){
                    "LA" -> new_name+= splitName[i].toLowerCase()+" "
                    "DE" -> new_name+= splitName[i].toLowerCase()+" "
                    "DEL" -> new_name+= splitName[i].toLowerCase()+" "
                    "Y" -> new_name+= splitName[i].toLowerCase()+" "
                    "LOS" -> new_name+= splitName[i].toLowerCase()+" "
                    "LAS" -> new_name+= splitName[i].toLowerCase()+" "
                    "EN" -> new_name+= splitName[i].toLowerCase()+" "
                    else -> {

                        if(splitName[i].length > 0){
                            new_name+= splitName[i].substring(0,1).toUpperCase()  +
                                    splitName[i].substring(1).toLowerCase()+" "
                        }

                    }

                }
            }
        }catch (e: Exception){
            new_name = name
        }



        return new_name
    }
    fun GenerarVcard(item: Servidores,data: String ){

        try{
            vcfFile = File(Environment.getExternalStorageDirectory().toString()+VCF_DIRECTORY)

            var delete = deleteFile(vcfFile)

            //Creamos el directorio si no existe
            if(!vcfFile.exists()){
                vcfFile.mkdirs()
            }

            vcfFile = File(vcfFile,"android_"+Calendar.getInstance().timeInMillis+".vcf")

            //instanciamos el FW
            val fw: FileWriter = FileWriter(vcfFile)
            fw.write("BEGIN:VCARD\r\n")
            fw.write("VERSION:3.0\r\n")
            /*Write the encoded version of image to vCard 2.1, NOTICE that no determining whether the image is GIF or JPEG is needed*/
            fw.write("PHOTO;ENCODING=BASE64:${data}\r\n")
            fw.write("N:${item.nombre_completo}\r\n")
            fw.write("FN:${item.nombre_completo}\r\n")
            fw.write("ORG:${item.dependencia}\r\n")


            fw.write("TITLE:${item.titulo}\r\n")
            fw.write("TEL;TYPE=WORK,VOICE:${item.telefono}\r\n")
            //fw.write("TEL;TYPE=HOME,VOICE:2181859\r\n")
            //fw.write("ADR;TYPE=WORK:;;tepoca;Hermosillo;estado;84000;country\r\n")
            fw.write("EMAIL;TYPE=PREF,INTERNET:${item.correo_electronico}\r\n")
            fw.write("END:VCARD\r\n")
            fw.close();

            /* Intent i = new Intent(); //this will import vcf in contact list
                    i.setAction(android.content.Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.fromFile(vcfFile), "text/x-vcard");
                    startActivity(i);*/
            var uri: Uri
            if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.N) {
                uri = Uri.parse("file://"+vcfFile.path)
            } else{
                uri = Uri.fromFile(File("file://"+vcfFile.path))
            }

            val intent = Intent(Intent.ACTION_SEND)
            var urisx = "file://"+ vcfFile.path
            intent.setType(URLConnection.guessContentTypeFromName(vcfFile.getName()))
            intent.putExtra(Intent.EXTRA_STREAM,  uri)
            startActivity(context,intent,null)



        }catch (e: Exception){
            Toast.makeText(context,"Error al generar la virtual card del servidor",Toast.LENGTH_SHORT).show();
        }


    }
    private fun deleteFile(file: File) : Boolean {
        try{
            return if(file.exists()){
                //Actual delete operation
                file.deleteRecursively()
                true
            } else {
                false
            }

        }catch (e: Exception){

        }
       return false
    }




}