package logalyzes.server;

import logalyzes.server.App;
import logalyzes.server.utils.Config;

public class Main {

    public static void main(String[] args) throws Exception {
        // Reading env variables from .env file
        Config config = new Config(".env");
        App app = new App();
        app.start();
    }
}
