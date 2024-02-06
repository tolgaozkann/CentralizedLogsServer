package logalyzes.server.controllers;


import com.logalyzes.logs.dtos.LogCreatedResponse;
import com.logalyzes.logs.dtos.LogServiceGrpc.LogServiceImplBase;

import  com.logalyzes.logs.dtos.LogsMessages.*;
import io.grpc.Status;
import io.grpc.stub.*;
import logalyzes.server.dtos.LogDto;
import logalyzes.server.repositories.LogCollectorRepository;
import logalyzes.server.utils.logger.LOG_LEVEL;
import logalyzes.server.utils.logger.Logger;

import java.util.concurrent.CompletableFuture;


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

         CompletableFuture.runAsync(() -> {
            try {
                // Did not wait for the response
                responseObserver.onNext(LogCreatedResponse.newBuilder().setCreated(true).build());
                responseObserver.onCompleted();

                // Process the message
                this.repo.save(new LogDto(request));

            } catch (Exception e) {
                String msg = "Error while processing message";
                logger.log(LOG_LEVEL.ERROR,msg + e);
                responseObserver.onError(Status.INTERNAL.withDescription(msg).asException());
                throw new RuntimeException(e);
            }
        });

    }

}
