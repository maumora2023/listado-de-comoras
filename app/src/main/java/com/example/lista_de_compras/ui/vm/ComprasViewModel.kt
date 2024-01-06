package com.example.lista_de_compras.ui.theme.viewmodels




import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lista_de_compras.data.ComprasRepository
import com.example.lista_de_compras.data.modelo.Compra
import com.example.lista_de_compras.ui.state.ComprasUIState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID

class ComprasViewModel(
    private val comprasRepository: ComprasRepository = ComprasRepository()
) : ViewModel() {

    companion object {
        const val FILE_NAME = "compras.data"
    }

    private var job: Job? = null

    private val _uiState = MutableStateFlow(ComprasUIState())

    val uiState:StateFlow<ComprasUIState> = _uiState.asStateFlow()

    init {
        obtenerCompras()
    }

    fun obtenerComprasGuardadasEnDisco(fileInputStream: FileInputStream) {
        comprasRepository.getComprasEnDisco(fileInputStream)
    }

    fun guardarComprasEnDisco(fileOutputStream: FileOutputStream) {
        comprasRepository.guardarComprasEnDisco(fileOutputStream)
    }

    private fun obtenerCompras() {
        job?.cancel()
        job = viewModelScope.launch {
            val comprasStream = comprasRepository.getComprasStream()
            comprasStream.collect { comprasActualizadas ->
                Log.v("ComprasViewModel", "obtenerCompras() update{}")
                _uiState.update { currentState ->
                    currentState.copy(
                        compras = comprasActualizadas
                    )
                }
            }
        }
    }

    fun agregarCompra(compra:String) {
        job = viewModelScope.launch {
            val t = Compra(UUID.randomUUID().toString(), compra)
            comprasRepository.insertar(t)
            _uiState.update {
                it.copy(mensaje = "Compra agregada: ${t.descripcion}")
            }
            obtenerCompras()
        }
    }

    fun eliminarCompra(compra:Compra) {
        job = viewModelScope.launch {
            comprasRepository.eliminar(compra)
            _uiState.update {
                it.copy(mensaje = "Compra eliminada: ${compra.descripcion}")
            }
            obtenerCompras()
        }
    }
}