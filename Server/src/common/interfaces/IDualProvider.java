package common.interfaces;

public interface IDualProvider<T, U> {
    
    void invoke (T value1, U value2);
    
}
