package com.example.petcaresistemadecontroleerotinaparapets.data.repository

import com.example.petcaresistemadecontroleerotinaparapets.data.local.dao.PetDao
import com.example.petcaresistemadecontroleerotinaparapets.data.local.entities.Pet
import com.example.petcaresistemadecontroleerotinaparapets.data.remote.FirebaseAuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepository @Inject constructor(
    private val petDao: PetDao,
    private val authService: FirebaseAuthService
) {

    suspend fun addPet(pet: Pet) {
        petDao.insertPet(pet)
    }

    // ✅ FUNÇÃO ADICIONADA
    suspend fun updatePet(pet: Pet) {
        petDao.updatePet(pet)
    }
    // --- FIM DA ADIÇÃO ---

    suspend fun deletePet(pet: Pet) {
        petDao.deletePet(pet)
    }

    fun getPetsDoUsuario(): Flow<List<Pet>> {
        // Padrão reativo: Observa o ID do usuário e atualiza a lista de pets
        return authService.getUserIdFlow().flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList()) // Retorna lista vazia se não houver usuário
            } else {
                petDao.getPetsDoUsuario(userId)
            }
        }
    }

    suspend fun getPetById(petId: Int): Pet? {
        return petDao.getPetById(petId)
    }
}