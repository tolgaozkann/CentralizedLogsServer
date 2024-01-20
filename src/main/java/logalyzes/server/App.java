package logalyzes.server;

import com.logalyzes.health.dtos.HealthCheckResponse.ServingStatus;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import  logalyzes.server.controllers.HealthService;
import logalyzes.server.controllers.LogCollectorService;
import logalyzes.server.core.ElkCore;
import  logalyzes.server.repositories.HealthManager;
import logalyzes.server.utils.Config;
import logalyzes.server.utils.logger.LOG_LEVEL;
import logalyzes.server.utils.logger.Logger;


public class App {
    private  Server server;
    private final int port = Config.PORT;
    HealthManager healthStatusManager;

    Logger logger = null;

    public App() {
        this.healthStatusManager = new HealthManager();
        this.healthStatusManager.setStatus(ServingStatus.SERVING);

        // Logger
        this.logger = Logger.getInstance();
    }

    private void _start() throws Exception {

        // Connect to ELK
        ElkCore elk = ElkCore.getInstance();


        this.server = ServerBuilder.forPort(this.port)
                .addService(new HealthService(this.healthStatusManager))
                .addService(new LogCollectorService())
                .build()
                .start();
        logger.log(LOG_LEVEL.INFO,"Server started, listening on " + this.port);


        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                try {
                    logger.log(LOG_LEVEL.WARN,"Received shutdown request" );
                    App.this.server.shutdown();
                    logger.log(LOG_LEVEL.WARN,"Successfully stopped the server" );

                }catch (Exception e){
                    logger.log(e,"Received shutdown request" );

                }
            }
        });
    }

    private void blockUntilShutdown() throws Exception {
        if (this.server != null) {
            this.server.awaitTermination();
        }
    }

    public void start() throws Exception {
        this._start();
        this.blockUntilShutdown();
    }
}
