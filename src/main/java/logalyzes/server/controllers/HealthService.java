package logalyzes.server.controllers;

import io.grpc.stub.StreamObserver;

import io.grpc.stub.ServerCalls;

import com.logalyzes.health.dtos.HealthGrpc.HealthImplBase;
import com.logalyzes.health.dtos.HealthCheckRequest;
import com.logalyzes.health.dtos.HealthCheckResponse;

import logalyzes.server.repositories.HealthManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class HealthService extends  HealthImplBase{
    private final HealthManager healthStatusManager;

    public  HealthService(HealthManager healthStatusManager) {
        this.healthStatusManager = healthStatusManager;
    }

    public void check(

            HealthCheckRequest request,
            StreamObserver<HealthCheckResponse> responseObserver
    ) {
        HealthCheckResponse response = HealthCheckResponse.newBuilder()
                .setStatus(this.healthStatusManager.getStatus())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private  HealthCheckResponse.ServingStatus checkCurrentHealthStatus(){
        return this.healthStatusManager.getStatus();
    }

    public void watch(
            HealthCheckRequest request,
            StreamObserver<HealthCheckResponse> responseObserver
    ) {


        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            HealthCheckResponse.ServingStatus currentStatus = checkCurrentHealthStatus();
            HealthCheckResponse response = HealthCheckResponse.newBuilder()
                    .setStatus(currentStatus)
                    .build();
            responseObserver.onNext(response);
        }, 0, 1, TimeUnit.SECONDS);
    }


}
