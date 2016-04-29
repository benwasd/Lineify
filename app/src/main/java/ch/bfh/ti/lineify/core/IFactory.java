package ch.bfh.ti.lineify.core;

public interface IFactory<TResult> {
    TResult apply();
}
