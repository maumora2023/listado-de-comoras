package com.example.lista_de_compras.ui.state

import com.example.lista_de_compras.data.modelo.Compra

data class ComprasUIState (
    val mensaje:String = "",
    val compras:List<Compra> = emptyList()
)