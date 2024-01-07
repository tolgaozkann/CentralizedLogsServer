package logalyzes.server;

import com.logalyzes.health.dtos.HealthCheckResponse.ServingStatus;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import  logalyzes.server.controllers.HealthService;
import  logalyzes.server.repositories.HealthManager;

public class App {
    private  Server server;
    HealthManager healthStatusManager;

    public App() {
        this.healthStatusManager = new HealthManager();
        this.healthStatusManager.setStatus(ServingStatus.SERVING);
    }

    private void _start() throws Exception {

        this.server = ServerBuilder.forPort(8080)
                .addService(new HealthService(this.healthStatusManager))
                .build()
                .start();
        System.out.println("Server started");


        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                try {
                    System.out.println("Received shutdown request");
                    App.this.server.shutdown();
                    System.out.println("Successfully stopped the server");

                }catch (Exception e){
                    System.err.println("Error while stopping the server");
                    e.printStackTrace();
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
