package com.example.biblioteisandroid2.Componentes.Usuario;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.biblioteisandroid2.API.models.User;
import com.example.biblioteisandroid2.API.repository.UserRepository;
import com.example.biblioteisandroid2.API.repository.BookRepository;

/**
 * ViewModel para gestionar la información del usuario en la aplicación.
 * Actúa como intermediario entre la interfaz de usuario y el UserRepository.
 */
public class UsuarioViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    /**
     * Constructor del ViewModel. Inicializa el UserRepository.
     */
    public UsuarioViewModel() {
        userRepository = new UserRepository();
    }

    /**
     * Obtiene los datos del usuario como LiveData.
     * @return LiveData con la información del usuario.
     */
    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    /**
     * Obtiene el estado de carga.
     * @return LiveData con un valor booleano que indica si la carga está en curso.
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Carga la información de un usuario específico por su ID.
     * @param userId Identificador único del usuario.
     */
    public void loadUserData(int userId) {
        isLoading.setValue(true);
        userRepository.getUserById(userId, new BookRepository.ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                userLiveData.setValue(user);
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Throwable t) {
                isLoading.setValue(false);
            }
        });
    }
}
