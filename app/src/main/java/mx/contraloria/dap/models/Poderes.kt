package mx.contraloria.dap.models

public class Poderes
{
    lateinit var id:String
    lateinit var name:String

    constructor(id: String,name:String) {
        this.id = id
        this.name = name
    }

    constructor()
}