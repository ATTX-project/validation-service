import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        int maxThreads = 6;
        int minThreads = 5;
        int timeOutMillis = 30000;

        port(4306);
        threadPool(maxThreads, minThreads, timeOutMillis);
        get("/health", (req, res) -> "Hello World");
    }
}