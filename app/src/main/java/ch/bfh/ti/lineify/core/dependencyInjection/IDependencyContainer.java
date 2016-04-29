package ch.bfh.ti.lineify.core.dependencyInjection;

import ch.bfh.ti.lineify.core.IFactory;

public interface IDependencyContainer {
    <T> T resolve(Class<T> classToResolve);
    <T> void registerContainerControlled(Class<T> classToRegister, IFactory<T> factory);
    <T> void registerPerResolve(Class<T> classToRegister, IFactory<T> factory);
}