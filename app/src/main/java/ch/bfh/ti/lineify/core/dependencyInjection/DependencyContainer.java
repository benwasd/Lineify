package ch.bfh.ti.lineify.core.dependencyInjection;

import java.util.HashMap;
import java.util.Map;

import ch.bfh.ti.lineify.core.IFactory;
import ch.bfh.ti.lineify.core.dependencyInjection.exceptions.ResolveError;
import ch.bfh.ti.lineify.core.dependencyInjection.lifetimeManagers.ContainerControlledLifetimeManager;
import ch.bfh.ti.lineify.core.dependencyInjection.lifetimeManagers.TransientLifetimeManager;

public class DependencyContainer implements IDependencyContainer {
    private final HashMap<Class<?>, Registration<?>> registrations = new HashMap<>();

    @Override
    public <T> T resolve(Class<T> classToResolve) {
        Registration<T> registration = this.getRegistrationByClass(classToResolve);
        if (registration == null) {
            throw new ResolveError(classToResolve);
        }

        try {
            return registration.getOrCreateInstance();
        }
        catch (Exception ex) {
            throw new ResolveError(classToResolve, ex);
        }
    }

    @Override
    public <T> void registerContainerControlled(Class<T> classToRegister, IFactory<T> factory) {
        Registration<T> registration = new Registration<>(factory, new ContainerControlledLifetimeManager<>());
        this.registrations.put(classToRegister, registration);
    }

    @Override
    public <T> void registerPerResolve(Class<T> classToRegister, IFactory<T> factory) {
        Registration<T> registration = new Registration<>(factory, new TransientLifetimeManager<>());
        this.registrations.put(classToRegister, registration);
    }

    @SuppressWarnings("unchecked") // the registrations map is under our control. this unchecked cast is safe -> suppress
    private <T> Registration<T> getRegistrationByClass(Class<T> $class) {
        return (Registration<T>) this.registrations.get($class);
    }

    private class Registration<T> {
        private IFactory<T> factory;
        private ILifetimeManager<T> lifetimeManager;

        public Registration(IFactory<T> factory, ILifetimeManager<T> lifetimeManager) {
            this.factory = factory;
            this.lifetimeManager = lifetimeManager;
        }

        public T getOrCreateInstance() {
            T instance = this.lifetimeManager.getInstance();
            if (instance == null) {
                instance = this.factory.apply();
                this.lifetimeManager.setInstance(instance);
            }

            return instance;
        }
    }
}