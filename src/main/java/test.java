import java.sql.Connection;

public interface test<R, T > {
    R func(T v, T v1) throws InterruptedException;
}
