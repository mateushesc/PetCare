package com.example.petcaresistemadecontroleerotinaparapets.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresistemadecontroleerotinaparapets.data.local.entities.Pet
import com.example.petcaresistemadecontroleerotinaparapets.data.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val petRepository: PetRepository
) : ViewModel() {

    // (Para MyPetsScreen)
    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: StateFlow<List<Pet>> = _pets.asStateFlow()

    // (Para PetDetailScreen)
    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet: StateFlow<Pet?> = _selectedPet.asStateFlow()

    private val _uiState = MutableStateFlow<PetUiState>(PetUiState.Idle)
    val uiState: StateFlow<PetUiState> = _uiState.asStateFlow()

    init {
        carregarPetsDoUsuario()
    }

    fun addPet(pet: Pet) {
        viewModelScope.launch {
            petRepository.addPet(pet)
        }
    }

    // ✅ FUNÇÃO ADICIONADA
    fun updatePet(pet: Pet) {
        viewModelScope.launch {
            petRepository.updatePet(pet)
        }
    }
    // --- FIM DA ADIÇÃO ---

    fun deletePet(pet: Pet) {
        viewModelScope.launch {
            petRepository.deletePet(pet)
        }
    }

    fun carregarPetsDoUsuario() {
        viewModelScope.launch {
            _uiState.value = PetUiState.Loading
            petRepository.getPetsDoUsuario()
                .catch { e ->
                    _uiState.value = PetUiState.Error(e.message ?: "Erro ao carregar pets")
                }
                .collect { listaDePets ->
                    _pets.value = listaDePets
                    _uiState.value = PetUiState.Success
                }
        }
    }

    fun carregarPetPorId(petId: Int) {
        viewModelScope.launch {
            _selectedPet.value = petRepository.getPetById(petId)
        }
    }

    // ✅ FUNÇÃO ADICIONADA
    // (Usada pela AddPetScreen para buscar o pet no modo de edição)
    suspend fun getPetParaEdicao(petId: Int): Pet? {
        return petRepository.getPetById(petId)
    }
    // --- FIM DA ADIÇÃO ---
}

// (Classe de estado da UI)
sealed class PetUiState {
    object Idle : PetUiState()
    object Loading : PetUiState()
    object Success : PetUiState()
    data class Error(val message: String) : PetUiState()
}