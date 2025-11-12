package com.example.petcaresistemadecontroleerotinaparapets.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresistemadecontroleerotinaparapets.data.local.entities.Evento
import com.example.petcaresistemadecontroleerotinaparapets.data.repository.EventoRepository
import com.example.petcaresistemadecontroleerotinaparapets.utils.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventoViewModel @Inject constructor(
    private val eventoRepository: EventoRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val scheduler = NotificationScheduler(context)

    // (Para PetDetailScreen)
    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos.asStateFlow()

    // (Para RemindersScreen)
    private val _todosOsEventos = MutableStateFlow<List<Evento>>(emptyList())
    val todosOsEventos: StateFlow<List<Evento>> = _todosOsEventos.asStateFlow()

    private val _uiState = MutableStateFlow<EventoUiState>(EventoUiState.Idle)
    val uiState: StateFlow<EventoUiState> = _uiState.asStateFlow()

    // ✅ ADIÇÃO:
    // Inicia a coleta de TODOS os eventos (para a tela de Lembretes)
    // assim que o ViewModel é criado.
    init {
        carregarTodosOsEventos()
    }
    // --- FIM DA ADIÇÃO ---

    fun adicionarEvento(evento: Evento) {
        viewModelScope.launch {
            eventoRepository.adicionarEvento(evento)
            scheduler.scheduleNotification(evento)
        }
    }

    // ✅ FUNÇÃO ADICIONADA
    fun excluirEvento(evento: Evento) {
        viewModelScope.launch {
            eventoRepository.excluirEvento(evento)
            // Também cancela qualquer notificação pendente (se houver)
            scheduler.cancelNotification(evento)
        }
    }
    // --- FIM DA ADIÇÃO ---

    fun carregarEventosDoPet(petId: Int) {
        viewModelScope.launch {
            _uiState.value = EventoUiState.Loading
            eventoRepository.getEventosDoPet(petId)
                .catch { e ->
                    _uiState.value = EventoUiState.Error(e.message ?: "Erro ao carregar eventos")
                }
                .collect { listaDeEventos ->
                    _eventos.value = listaDeEventos
                    _uiState.value = EventoUiState.Success
                }
        }
    }

    // (Esta função agora é chamada pelo init)
    fun carregarTodosOsEventos() {
        viewModelScope.launch {
            _uiState.value = EventoUiState.Loading
            eventoRepository.getAllEventosDoUsuario()
                .catch { e ->
                    _uiState.value = EventoUiState.Error(e.message ?: "Erro ao carregar lembretes")
                }
                .collect { listaDeEventos ->
                    _todosOsEventos.value = listaDeEventos
                    _uiState.value = EventoUiState.Success
                }
        }
    }
}

// (Classe de estado da UI)
sealed class EventoUiState {
    object Idle : EventoUiState()
    object Loading : EventoUiState()
    object Success : EventoUiState()
    data class Error(val message: String) : EventoUiState()
}