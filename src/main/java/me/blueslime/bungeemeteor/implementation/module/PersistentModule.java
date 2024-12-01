package me.blueslime.bungeemeteor.implementation.module;

public interface PersistentModule extends RegisteredModule {
    @Override
    default boolean isPersistent() {
        return true;
    }

    @Override
    default void unregisterImplementedModule() {

    }
}
