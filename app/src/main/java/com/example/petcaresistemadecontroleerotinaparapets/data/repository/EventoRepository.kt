package com.example.petcaresistemadecontroleerotinaparapets.data.repository

import com.example.petcaresistemadecontroleerotinaparapets.data.local.dao.EventoDao
import com.example.petcaresistemadecontroleerotinaparapets.data.local.entities.Evento
import com.example.petcaresistemadecontroleerotinaparapets.data.remote.FirebaseAuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
// ✅ IMPORTS ADICIONADOS
import kotlinx.coroutines.flow.flatMapLatest
// FIM DA ADIÇÃO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventoRepository @Inject constructor(
    private val eventoDao: EventoDao,
    private val authService: FirebaseAuthService
) {

    fun getEventosDoPet(petId: Int): Flow<List<Evento>> {
        return eventoDao.getEventosDoPet(petId)
    }

    suspend fun adicionarEvento(evento: Evento) {
        eventoDao.insertEvento(evento)
    }

    // ✅ FUNÇÃO ADICIONADA
    suspend fun excluirEvento(evento: Evento) {
        eventoDao.deleteEvento(evento)
    }
    // --- FIM DA ADIÇÃO ---


    /**
     * Busca todos os eventos (para RF03).
     * ✅ CORREÇÃO: Aplicando o mesmo padrão reativo do PetRepository.
     */
    fun getAllEventosDoUsuario(): Flow<List<Evento>> {
        return authService.getUserIdFlow().flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                eventoDao.getAllEventosDoUsuario(userId)
            }
        }
    }
}