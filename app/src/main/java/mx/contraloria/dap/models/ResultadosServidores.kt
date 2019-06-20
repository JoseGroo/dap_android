package mx.contraloria.dap.models

import java.io.Serializable

data class ResultadosServidores (var pagina_actual: Int,
                       var total: Int,
                       var total_paginas: Int,
                       var resultados: ArrayList<Servidores>) : Serializable{
}