package mx.contraloria.dap.models

public class Dependencias
{
    lateinit var id:String
    lateinit var iniciales:String
    lateinit var nombre:String

    constructor(id: String,iniciales:String,nombre:String) {
        this.id = id
        this.iniciales = iniciales
        this.nombre = nombre
    }

    constructor()
}