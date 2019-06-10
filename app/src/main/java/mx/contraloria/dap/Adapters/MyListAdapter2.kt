package mx.contraloria.dap.Adapters

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import mx.contraloria.dap.models.mList
import mx.contraloria.dap.R
import java.net.URL
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import java.io.IOException
import android.os.StrictMode
import com.daimajia.swipe.SwipeLayout
import kotlinx.android.synthetic.main.dialog_phones.*
import kotlinx.android.synthetic.main.row_swipe.view.*
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.text.Layout
import android.view.*
import android.widget.*
import kotlinx.android.synthetic.main.dialog_email.*
import mx.contraloria.dap.ListaServidoresActivity
import java.security.Permission
import java.util.jar.Manifest


class MyListAdapter2(var mCtx: Context, var resource: Int, var items:List<mList>)
    :ArrayAdapter<mList>(mCtx,resource,items){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //Infleate Layout
        val layoutInflater:LayoutInflater =  LayoutInflater.from(mCtx)

        val view: View = layoutInflater.inflate(resource,null)

        val imageView:ImageView = view.findViewById(R.id.iImageR)
        val textView:TextView = view.findViewById(R.id.txtNombreR)
        val textView1:TextView = view.findViewById(R.id.txtPuestoR)
        val textView2:TextView = view.findViewById(R.id.txtUnidadR)


        var LayoutSwipe: SwipeLayout = view.findViewById(R.id.row_swipe_1)
        LayoutSwipe.addDrag(SwipeLayout.DragEdge.Left, LayoutSwipe.findViewById(R.id.bottom_wrapper))
        LayoutSwipe.addDrag(SwipeLayout.DragEdge.Right, LayoutSwipe.findViewById(R.id.bottom_wrapper_2))


        //Evento Geolocalizacion
        LayoutSwipe.Geolocalizacion.setOnClickListener(View.OnClickListener {
            Toast.makeText(mCtx, "Click Geolocalizacion", Toast.LENGTH_SHORT).show()
        })


        //Evento Emails
        LayoutSwipe.Emails.setOnClickListener(View.OnClickListener {
            Toast.makeText(mCtx, "Emails", Toast.LENGTH_SHORT).show()
        })
        var mItems:mList = items[position]


        if(mItems.profile_image != ""){
            try{
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
                var url:URL
                url = URL(mItems.profile_image)
                var bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                imageView.setImageBitmap(bmp)
            }catch (e: IOException){
                print(e)
            }
        }



        textView.text = mItems.employee_name
        textView1.text = "$"+mItems.employee_salary
        textView2.text = "Edad: "+mItems.employee_age


        //Evento Telefonos
        val phonesDialog: Dialog = Dialog(mCtx)
        phonesDialog.setContentView(R.layout.dialog_phones)
        //Layout Transparenge para que no se vea lo demas
        phonesDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //EventoClik de botones
        LayoutSwipe.Phones.setOnClickListener(View.OnClickListener {

            //Eliminamos todas las dependencias
            val lineallayout = phonesDialog.findViewById(R.id.lyLstBtnPhones) as LinearLayout
            lineallayout.removeAllViews()
            //Creamos un btoon
            CreateBtnPhones(phonesDialog,"6623849509")
            CreateBtnPhones(phonesDialog,"6629245519")
            val phoneDtxtName: TextView = phonesDialog.findViewById(R.id.txtDNombre)
            val phoneDiImage: ImageView = phonesDialog.findViewById(R.id.iDImage)
            try{
                phoneDtxtName.text = mItems.employee_name
            }catch (e: IOException){
                print(e)
            }
            phonesDialog.show()
        })

        //Evento Email
        val emailDialog: Dialog = Dialog(mCtx)
        emailDialog.setContentView(R.layout.dialog_email)
        emailDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        LayoutSwipe.Emails.setOnClickListener(View.OnClickListener {


            //Eliminamos todas las dependencias
            val lineallayout = emailDialog.findViewById(R.id.lyLstBtnEmails) as LinearLayout
            lineallayout.removeAllViews()
            //Creamos los emails
            CreateBtnEmails(emailDialog,"pablo12_jip3@hotmail.com",mItems)
            CreateBtnEmails(emailDialog,"pablo12jip3@gmail.com",mItems)

            val emailDtxtName: TextView = emailDialog.findViewById(R.id.txtDNombre)
            val emailDiImage: ImageView = emailDialog.findViewById(R.id.iDImage)
            try{
                emailDtxtName.text = mItems.employee_name
            }catch (e: IOException){
                print(e)
            }

            emailDialog.show()
        })

        return view
    }

    fun CreateBtnPhones(dialog: Dialog, tel: String){
        /* Aqui empieza el buttones dinaamoscs */
        val constraintLayout = dialog.findViewById(R.id.lyLstBtnPhones) as LinearLayout
        val button = Button(dialog.context)
        /*Parametros*/
        var params : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // This will define text view width
            LinearLayout.LayoutParams.WRAP_CONTENT // This will define text view height
        )
        /*Margin para poner borders*/
        //params.setMargins(0,0,0,0)

        button.layoutParams = params

        /*Padding*/
        button.setPadding(70,70,70,70)

        button.text = tel
        button.setBackgroundColor(Color.WHITE)
        button.setTextColor(Color.BLACK)
        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dialog_phone_icon, 0, 0, 0)
        button.setCompoundDrawablePadding(90)
        button.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        //Evento para llamar
        button.setOnClickListener(View.OnClickListener {
            if(ActivityCompat.checkSelfPermission(mCtx,android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_CALL);
                intent.data = Uri.parse("tel:"+tel)
                startActivity(mCtx,intent,null)
            }else{
                Toast.makeText(dialog.context,"No cuenta con permisos para hacer llamadas",Toast.LENGTH_LONG)
            }
        })

        //Efecto del boton para que se vea bien//
        buttonEffect(button)
        constraintLayout.addView(button)



        /**/
    }
    fun CreateBtnEmails(dialog: Dialog, email: String,model: mList){
        /* Aqui empieza el buttones dinaamoscs */
        val constraintLayout = dialog.findViewById(R.id.lyLstBtnEmails) as LinearLayout

        val button = Button(dialog.context)
        /*Parametros*/
        var params : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // This will define text view width
            LinearLayout.LayoutParams.WRAP_CONTENT // This will define text view height
        )
        /*Margin para poner borders*/
        //params.setMargins(0,0,0,0)

        button.layoutParams = params

        /*Padding*/
        button.setPadding(70,70,70,70)

        button.text = email
        button.setBackgroundColor(Color.WHITE)
        button.setTextColor(Color.BLACK)
        button.setCompoundDrawablesWithIntrinsicBounds( R.drawable.dialog_email_icon, 0, 0, 0)
        button.setCompoundDrawablePadding(90)
        button.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        //Evento para llamar
        button.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type= "text/html"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(email))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Enviado desde | DAP MOBIL ANDROID")
            intent.putExtra(Intent.EXTRA_TEXT, "Hola "+model.employee_name )
            startActivity(mCtx,intent,null)
        })

        //Efecto del boton para que se vea bien//
        buttonEffect(button)
        constraintLayout.addView(button)



        /**/
    }



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
}

