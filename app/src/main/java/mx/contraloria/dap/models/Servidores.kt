package mx.contraloria.dap.models

import java.io.Serializable

data class Servidores (var id: Int,
                       var nombre_completo: String,
                       var titulo: String,
                       var dependencia: String,
                       var unidad_administrativa:String,
                       var fecha_de_alta:String,
                       var puesto_funcional:String,
                       var puesto_oficial:String,
                       var nivel:String,
                       var domicilio:String,
                       var lada:String,
                       var telefono:String,
                       var correo_electronico:String,
                       var reseña:String,
                       var foto: String) : Serializable{
}