package ch.bfh.ti.lineify.core.dependencyInjection;

public interface ILifetimeManager<T> {
    T getInstance();
    void setInstance(T instance);
}