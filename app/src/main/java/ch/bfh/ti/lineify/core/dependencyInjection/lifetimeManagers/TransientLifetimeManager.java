package ch.bfh.ti.lineify.core.dependencyInjection.lifetimeManagers;

import ch.bfh.ti.lineify.core.dependencyInjection.ILifetimeManager;

public class TransientLifetimeManager<T> implements ILifetimeManager<T> {
    @Override
    public T getInstance() {
        return null;
    }

    @Override
    public void setInstance(T instance) {
    }
}