package ch.bfh.ti.lineify.core.dependencyInjection.lifetimeManagers;

import ch.bfh.ti.lineify.core.dependencyInjection.ILifetimeManager;

public class ContainerControlledLifetimeManager<T> implements ILifetimeManager<T> {
    private T instance = null;

    @Override
    public T getInstance() {
        return this.instance;
    }

    @Override
    public void setInstance(T instance) {
        this.instance = instance;
    }
}
