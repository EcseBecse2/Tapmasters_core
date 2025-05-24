package org.egyse.tapmasters_core.data;

import org.egyse.tapmasters_core.models.Booster;
import org.egyse.tapmasters_core.models.User;

import java.util.List;
import java.util.UUID;

public interface DataManager {

    void createUser(User user);
    User getUser(String uuid);
    Booster getGlobalBooster();
    void setGlobalBooster(Booster booster);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(UUID uuid);
    void disconnect();

}
