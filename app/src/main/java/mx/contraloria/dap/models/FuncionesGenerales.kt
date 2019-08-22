package mx.contraloria.dap.models

import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.support.design.widget.Snackbar
import android.view.View
import mx.contraloria.dap.R
import java.lang.Exception
import java.util.*
import java.net.URLConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import mx.contraloria.dap.HomeTabActivity
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.*
import android.view.MotionEvent
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detalle_servidor.*
import mx.contraloria.dap.Adapters.CircleTransform
import mx.contraloria.dap.BusquedaActivity
import java.io.*
import java.net.URL
import kotlin.collections.ArrayList


class FuncionesGenerales(cTx: Context) {

    var CantidadMostrarBusquedas: Int = 10
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
        var intent = Intent(context, BusquedaActivity::class.java)
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

    //Normalizar Telefonos 01800 507 9222  "535 82 00"
    fun telefonos(tel: String, lada: String): ArrayList<String>{
        var arrayTel = tel.toCharArray()
        var telefonos: ArrayList<String> = ArrayList<String>()
        var buildTelefono=""
        // Normal: 0, lada : 1, 01800 = 2, Lada otro 3
        var tipo= 0
        var contador = 0
        var validaTipo = true
        for (item: Char in arrayTel) {
            //verficamos los numeros si los tiene bien
            when(tipo){
                0,3 -> {
                    if(buildTelefono.length == 7){
                        validaTipo= false
                    }
                }
                1 -> {
                    if(buildTelefono.length == 10){
                        validaTipo= false
                    }
                }
                2 -> {
                    if(buildTelefono.length == 12){
                        validaTipo= false
                    }
                }
            }
            //verificamos
            if(item.isDigit() && validaTipo){
                //guardamos el item
                if(buildTelefono != "" && buildTelefono.length > 2){
                    var subNum = buildTelefono.substring(0,1)
                    when(subNum){
                        "0" -> tipo = 2
                        "6"->{
                            var subtringnum = buildTelefono.substring(1,2)
                            if(subtringnum.toInt() == 6){
                                tipo = 1
                            }else{
                                tipo = 3
                            }
                        }
                        else -> {
                            tipo= 0
                        }
                    }
                }
                buildTelefono+= item

            }else{
                //verificamos si es espacio para sumer un score

                when(tipo){
                    0,3 -> {
                        if(buildTelefono.length == 7 || (item == '/' || item== ',')){
                            if(buildTelefono != ""){
                                telefonos.add(lada+" "+buildTelefono)
                                buildTelefono=""
                            }

                            validaTipo=true
                        }
                    }
                    1 -> {
                        if(buildTelefono.length == 10 || (item == '/' || item== ',')){

                            if(buildTelefono != ""){
                                telefonos.add(buildTelefono)
                                buildTelefono=""
                            }
                            validaTipo=true
                        }
                    }
                    2 -> {
                        if(buildTelefono.length == 12 || (item == '/' || item== ',')){

                            if(buildTelefono != ""){
                                telefonos.add(buildTelefono)
                                buildTelefono=""
                            }
                            validaTipo=true
                        }
                    }
                }

            }
            contador++

        }
        if(tipo == 0 || tipo == 3){
            if(buildTelefono!=""){
                telefonos.add(lada+" "+buildTelefono)
            }
        }
        else{
            if(buildTelefono!=""){
                telefonos.add(buildTelefono)
            }
        }


        return telefonos


    }

    fun emails(email: String): ArrayList<String>{
        var emails = email.split(",","/"," ")
        return emails as ArrayList<String>
    }

    //VCARD
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
            var listTelefonos = telefonos(item.telefono,item.lada)
            var telefonos = ""

            for(item in listTelefonos){
                fw.write("TEL;TYPE=WORK,VOICE:${item}\r\n")

            }

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
            val permission_extorage = checkSelfPermission(context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val permission_read_extorage = checkSelfPermission(context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)
            var vMensaje = context.getString(R.string.str_version_android_no_compatible)

            if (permission_read_extorage != PackageManager.PERMISSION_GRANTED || permission_read_extorage != PackageManager.PERMISSION_GRANTED)
                vMensaje = context.getString(R.string.str_sin_permisos_otorgados)

            Toast.makeText(context,vMensaje,Toast.LENGTH_SHORT).show()
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



    /*BOTONES PARA FUNCIONES GENERALES*/
    //phones
    fun GenerarViewCallTelefonos( name: String, foto: String,lad:String,tel: String){
        //inicializamos
        val phonesDialog: Dialog = Dialog(context)
        phonesDialog.setContentView(R.layout.dialog_phones)
        //Layout Transparenge para que no se vea lo demas
        phonesDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //Eliminamos todas las dependencias
        val lineallayout = phonesDialog.findViewById(R.id.lyLstBtnPhones) as LinearLayout
        lineallayout.removeAllViews()
        //Creamos un btoon

        val phoneDtxtName: TextView = phonesDialog.findViewById(R.id.txtDNombre)
        val phoneDiImage: ImageView = phonesDialog.findViewById(R.id.iDImage)
        val btn_close: ImageView = phonesDialog.findViewById(R.id.btn_close_dialog)
        try{

            phoneDtxtName.text =name
            Picasso.get()
                .load(foto)
                .transform(CircleTransform())
                .error(R.drawable.profile_gray)
                .into(phoneDiImage)
            //creamos los telefonos
            var telefonos  = telefonos(tel,lad)
            for (item in telefonos) {
                CreateBtnPhones(phonesDialog,item)
            }
            if(btn_close != null){
                btn_close.setOnClickListener(View.OnClickListener {
                    phonesDialog.dismiss()

                })
            }
        }catch (e: IOException){
            print(e)
        }
        phonesDialog.show()
    }

    fun CreateBtnPhones(dialog: Dialog, tel: String){
        /* Aqui empieza el buttones dinaamoscs */
        val constraintLayout = dialog.findViewById(R.id.lyLstBtnPhones) as LinearLayout

        //creamos el linear layoutcontenedor
        val linearLayout = LinearLayout(dialog.context)
        /*Parametros para Linear Layout*/
        var Lparams : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // This will define text view width
            LinearLayout.LayoutParams.WRAP_CONTENT // This will define text view height
        )
        linearLayout.layoutParams = Lparams
        linearLayout.orientation = LinearLayout.HORIZONTAL




        val button = Button(dialog.context)
        /*Parametros*/
        var params : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            0, // This will define text view width
            LinearLayout.LayoutParams.WRAP_CONTENT, // This will define text view height
            .80f
        )
        /*Margin para poner borders*/
        //params.setMargins(0,10,0,10)

        button.layoutParams = params

        /*Padding*/
        button.setPadding(70,70,70,70)

        button.text = tel
        button.setBackgroundColor(Color.WHITE)
        button.setTextColor(Color.BLACK)
        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dialog_phone_icon, 0, 0, 0)
        button.setCompoundDrawablePadding(70)
        button.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        button.textSize = 15f
        //Evento para llamar
        button.setOnClickListener(View.OnClickListener {
            if(ActivityCompat.checkSelfPermission(context,android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_CALL);
                intent.data = Uri.parse("tel:"+tel)
                ContextCompat.startActivity(context, intent, null)
            }else{
                Toast.makeText(dialog.context,"No cuenta con permisos para hacer llamadas",Toast.LENGTH_LONG)
            }
        })

        //Efecto del boton para que se vea bien//
        buttonEffect(button)
        linearLayout.addView(button)
        /**/





        //Creamos el Cpy Button
        val button_coppy = ImageView(dialog.context)
        /*Parametros*/
        var copy_params : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            0, // This will define text view width
            LinearLayout.LayoutParams.MATCH_PARENT, // This will define text view height
            .16f
        )
        /*Margin para poner borders*/
        //params.setMargins(0,0,0,0)

        button_coppy.layoutParams = copy_params


        /*Padding*/
        button_coppy.setPadding(45,45,45,45)

        button_coppy.setBackgroundColor(Color.WHITE)
        button_coppy.setImageResource( R.drawable.ic_content_copy_black_24dp)
        //Evento para llamar
        button_coppy.setOnClickListener(View.OnClickListener {
            var myClipboard =  context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            var myClip = ClipData.newPlainText("text", tel)
            myClipboard?.setPrimaryClip(myClip);

            Toast.makeText(context, "El telefono fue copiado en el portapapeles", Toast.LENGTH_SHORT).show();
        })

        //Efecto del boton para que se vea bien//
        buttonEffect(button_coppy)
        linearLayout.addView(button_coppy)

        //Agreamos el linear layout al otro LinearLayout
        constraintLayout.addView(linearLayout)
    }

    //efecto de botones
    fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
    }


    // EMAILS
    fun GenerarviewCallEmail( name: String, foto: String,email: String){

        //inicializamod
        val emailDialog: Dialog = Dialog(context)
        emailDialog.setContentView(R.layout.dialog_email)
        emailDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //Eliminamos todas las dependencias
        val lineallayout = emailDialog.findViewById(R.id.lyLstBtnEmails) as LinearLayout
        lineallayout.removeAllViews()

        val emailDtxtName: TextView = emailDialog.findViewById(R.id.txtDNombre)
        val emailDiImage: ImageView = emailDialog.findViewById(R.id.iDImage)
        val btn_close: ImageView = emailDialog.findViewById(R.id.btn_close_dialog)
        try{
            emailDtxtName.text = name
            Picasso.get()
                .load(foto)
                .transform(CircleTransform())
                .error(R.drawable.profile_gray)
                .into(emailDiImage)

            //creamos los emails
            var emails  = emails(email)
            for (item in emails) {
                CreateBtnEmails(emailDialog,item,name)
            }
            if(btn_close != null){
                btn_close.setOnClickListener(View.OnClickListener {
                    emailDialog.dismiss()

                })
            }
        }catch (e: IOException){
            print(e)
        }

        emailDialog.show()
    }
    fun CreateBtnEmails(dialog: Dialog, email: String,name: String){
        /* Aqui empieza el buttones dinaamoscs */
        val constraintLayout = dialog.findViewById(R.id.lyLstBtnEmails) as LinearLayout

        //creamos el linear layoutcontenedor
        val linearLayout = LinearLayout(dialog.context)
        /*Parametros para Linear Layout*/
        var Lparams : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // This will define text view width
            LinearLayout.LayoutParams.WRAP_CONTENT // This will define text view height
        )
        linearLayout.layoutParams = Lparams
        linearLayout.orientation = LinearLayout.HORIZONTAL


        //Creamos el botones
        val button = Button(dialog.context)
        /*Parametros*/
        var params : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            0, // This will define text view width
            LinearLayout.LayoutParams.WRAP_CONTENT, // This will define text view height
        .80f
        )
        /*Margin para poner borders*/
        //params.setMargins(0,0,0,0)

        button.layoutParams = params


        /*Padding*/
        button.setPadding(30,30,30,30)

        button.text = email
        button.setBackgroundColor(Color.WHITE)
        button.setTextColor(Color.BLACK)
        button.setCompoundDrawablesWithIntrinsicBounds( R.drawable.dialog_email_icon, 0, 0, 0)
        button.setCompoundDrawablePadding(70)
        button.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        //Evento para llamar
        button.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type= "text/html"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(email))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Enviado desde | DAP APP ANDROID")
            intent.putExtra(Intent.EXTRA_TEXT, "Hola "+name )
            startActivity(context,intent,null)
        })

        //Efecto del boton para que se vea bien//
        buttonEffect(button)
        linearLayout.addView(button)

        //Creamos el Cpy Button
        val button_coppy = ImageView(dialog.context)
        /*Parametros*/
        var copy_params : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            0, // This will define text view width
            LinearLayout.LayoutParams.MATCH_PARENT, // This will define text view height
            .16f
        )
        /*Margin para poner borders*/
        //params.setMargins(0,0,0,0)

        button_coppy.layoutParams = copy_params


        /*Padding*/
        button_coppy.setPadding(45,45,45,45)

        button_coppy.setBackgroundColor(Color.WHITE)
        button_coppy.setImageResource( R.drawable.ic_content_copy_black_24dp)

        //Evento para llamar
        button_coppy.setOnClickListener(View.OnClickListener {
            var myClipboard =  context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            var myClip = ClipData.newPlainText("text", email)
            myClipboard?.setPrimaryClip(myClip);

            Toast.makeText(context, "Email fue copiado en el portapapeles", Toast.LENGTH_SHORT).show();
        })

        //Efecto del boton para que se vea bien//
        buttonEffect(button_coppy)
        linearLayout.addView(button_coppy)

        //Agreamos el linear layout al otro LinearLayout
        constraintLayout.addView(linearLayout)

        /**/
    }

    var V_KEY_BUSQUEDAS = "busquedas"
    var MY_PREFS = "myPrefs"

    fun saveSearchJson(list: List<Busquedas>): Boolean{

        var sharedPreferences = this.context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        try{
            val gson = Gson()
            val json = gson.toJson(list)
            val editor = sharedPreferences.edit()
            editor.putString(V_KEY_BUSQUEDAS, json)
            editor.commit()

        }catch (e: Exception){

            return false
        }

        return true
    }

    fun getSearchFromSharedPreferences(): List<Busquedas> {
        var sharedPreferences = this.context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        val gson = Gson()
        var productFromShared: List<Busquedas> = ArrayList()
        try{
            val jsonPreferences = sharedPreferences.getString(V_KEY_BUSQUEDAS, "")
            val listType = object : TypeToken<ArrayList<Busquedas>>() { }.type
            productFromShared = gson.fromJson(jsonPreferences, listType)
        }catch (e: Exception){

            print(e)
        }

        return productFromShared
    }
}