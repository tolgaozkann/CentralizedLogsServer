package logalyzes.server.controllers;

import io.grpc.Status;
import  io.grpc.stub.StreamObserver;

import com.logalyzes.logs.dtos.LogsServiceGrpc;
import com.logalyzes.logs.dtos.LogsServiceGrpc.LogsServiceImplBase;
import  com.logalyzes.logs.dtos.*;
import logalyzes.server.dtos.LogDto;
import logalyzes.server.repositories.LogCollectorRepository;
import logalyzes.server.repositories.LogingRepository;
import logalyzes.server.utils.logger.LOG_LEVEL;
import logalyzes.server.utils.logger.Logger;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;


public class LogController extends LogsServiceImplBase {
    private LogingRepository repository;
    private Logger logger = null;

    public LogController() throws Exception {
        this.repository = new LogingRepository();
        this.logger = Logger.getInstance();
    }

    /**
     */
    public void listIndexes(
            ListIndexesRequest request,
            StreamObserver<ListIndexesResponse> responseObserver) {

        this.repository.listIndexes().thenAccept(res -> {
            responseObserver.onNext(res);
            responseObserver.onCompleted();
        }).exceptionally(
                err -> {
                    String msg = "Error while processing message";
                    logger.log(LOG_LEVEL.ERROR,msg + err);
                    responseObserver.onError(Status.INTERNAL.withDescription(msg).asException());
                    throw new RuntimeException(err);
                }
        );

        logger.log(LOG_LEVEL.INFO,"Indexes listed");
    }



    /**
     */
    public void list(
            LogsRequest request,
            StreamObserver<LogsResponse> responseObserver)  {
        try {
            this.repository.list(request).thenAccept(res -> {
                responseObserver.onNext(res);
                responseObserver.onCompleted();
            }).exceptionally(
                    err -> {
                        logger.log(LOG_LEVEL.ERROR,err.toString());
                        responseObserver.onError(Status.INTERNAL.withDescription("").asException());
                        throw new RuntimeException(err);
                    }
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     */
    public void detail(LogRequest request,
                      StreamObserver<LogResponse> responseObserver) {
       responseObserver.onNext(LogResponse.newBuilder().build());
    }
}
