package logalyzes.server.controllers;


import com.logalyzes.logs.dtos.LogCreatedResponse;
import com.logalyzes.logs.dtos.LogServiceGrpc.LogServiceImplBase;

import  com.logalyzes.logs.dtos.LogsMessages.*;
import io.grpc.stub.*;
import logalyzes.server.dtos.LogDto;
import logalyzes.server.repositories.LogCollectorRepository;
import logalyzes.server.utils.Mapper;
import logalyzes.server.utils.logger.LOG_LEVEL;
import logalyzes.server.utils.logger.Logger;

import javax.annotation.processing.SupportedSourceVersion;

public class LogCollectorService extends LogServiceImplBase {
    private LogCollectorRepository repo;
    private  Logger logger = null;


    public LogCollectorService() {
        this.repo = new LogCollectorRepository();
        this.logger = Logger.getInstance();
    }

    @Override
    public void create(
            LogForCreate request,
            StreamObserver<LogCreatedResponse> responseObserver)  {

        try {
            this.repo.save(new LogDto(request)).thenApply(res -> {
                    responseObserver.onNext(LogCreatedResponse.newBuilder().setCreated(res).build());
                    responseObserver.onCompleted();
                    return  true;
            }).exceptionallyAsync(e -> {
                    String msg = "Error while processing message";
                    logger.log(LOG_LEVEL.ERROR,msg + e);
                    responseObserver.onError(e);
                    return null;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public StreamObserver<LogForCreate> createStream(
            StreamObserver<LogCreatedResponse> responseObserver
    ) {
        return new StreamObserver<LogForCreate>() {
            @Override
            public void onNext(LogForCreate value) {
                try {
                    // Process each incoming LogForCreate message
                    repo.save(new LogDto(value)).thenApply(res -> {
                        responseObserver.onNext(LogCreatedResponse.newBuilder().setCreated(res).build());
                        responseObserver.onCompleted();
                        return  true;
                    }).exceptionallyAsync(e -> {
                        String msg = "Error while processing message";
                        logger.log(LOG_LEVEL.ERROR,msg + e);
                        responseObserver.onError(e);
                        return null;
                    });

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error");
                responseObserver.onError(t);
                throw new RuntimeException(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
