package ch.bfh.ti.lineify.core.dependencyInjection.exceptions;

public class ResolveError extends Error {
    public ResolveError(String message) {
        super(message);
    }

    public ResolveError(Class notResolvableClass) {
        super(String.format("Can not resolve '%s', it is not registered on the dependency container.", notResolvableClass.getName()));
    }

    public ResolveError(Class notResolvableClass, Exception innerException) {
        super(String.format("Unexpected exception resolving '%s', see inner exception for detailed information.", notResolvableClass.getName()), innerException);
    }
}