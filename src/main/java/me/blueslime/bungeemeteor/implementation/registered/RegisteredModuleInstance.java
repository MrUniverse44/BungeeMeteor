package me.blueslime.bungeemeteor.implementation.registered;

import me.blueslime.bungeemeteor.implementation.module.PersistentModule;

public class RegisteredModuleInstance implements PersistentModule {
    private static final RegisteredModuleInstance instance = new RegisteredModuleInstance();

    public static RegisteredModuleInstance getInstance() {
        return instance;
    }
}
