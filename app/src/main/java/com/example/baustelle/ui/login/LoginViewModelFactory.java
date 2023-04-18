package com.example.baustelle.ui.login;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.example.baustelle.data.DBHandler;
import com.example.baustelle.data.LoginDataSource;
import com.example.baustelle.data.LoginRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private final DBHandler db;

    LoginViewModelFactory(DBHandler db){
        this.db = db;
    }
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(LoginRepository.getInstance(new LoginDataSource(db)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}