package com.example.baustelle.data;
import android.content.Context;

import com.example.baustelle.MainActivity;
import com.example.baustelle.data.model.LoggedInUser;
import java.io.IOException;
import com.example.baustelle.data.DBHandler;
import com.example.baustelle.ui.login.LoginActivity;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final DBHandler db;

    public LoginDataSource(DBHandler db){
        this.db = db;
    }

    public Result<LoggedInUser> login(String username, String password) {
        String fullName = db.getUser(username);
        try {

            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            fullName, username );
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}