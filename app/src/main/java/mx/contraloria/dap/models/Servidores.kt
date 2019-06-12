package mx.contraloria.dap.models

import java.io.Serializable

data class Servidores (val id: Int,
                       val nombre_completo: String,
                       val titulo: String,
                       val dependencia: String,
                       val unidad_administrativa:String,
                       val fecha_de_alta:String,
                       val puesto_funcional:String,
                       val puesto_oficial:String,
                       val nivel:String,
                       val domicilio:String,
                       val lada:String,
                       val telefono:String,
                       val correo_electronico:String,
                       val reseña:String,
                       val foto: String) : Serializable