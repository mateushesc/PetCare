package com.example.petcaresistemadecontroleerotinaparapets.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update // ✅ IMPORT ADICIONADO
import com.example.petcaresistemadecontroleerotinaparapets.data.local.entities.Pet
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: Pet)

    // ✅ FUNÇÃO ADICIONADA
    @Update
    suspend fun updatePet(pet: Pet)

    @Delete
    suspend fun deletePet(pet: Pet)

    @Query("SELECT * FROM pets WHERE userId = :userId")
    fun getPetsDoUsuario(userId: String): Flow<List<Pet>>

    @Query("SELECT * FROM pets WHERE petId = :petId")
    suspend fun getPetById(petId: Int): Pet?
}