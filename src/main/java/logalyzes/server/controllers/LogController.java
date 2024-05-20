package logalyzes.server.controllers;

import io.grpc.Status;
import  io.grpc.stub.StreamObserver;

import com.logalyzes.logs.dtos.LogsServiceGrpc.LogsServiceImplBase;

import logalyzes.server.repositories.LogingRepository;
import logalyzes.server.utils.logger.LOG_LEVEL;
import logalyzes.server.utils.logger.Logger;




public class LogController extends LogsServiceImplBase {
    private LogingRepository repository;
    private Logger logger;

    public LogController() throws Exception {
        this.repository = new LogingRepository();
        this.logger = Logger.getInstance();
    }

    /**
     */
    public void listIndexes(
            com.logalyzes.logs.dtos.ListIndexesRequest request,
            StreamObserver<com.logalyzes.logs.dtos.ListIndexesResponse> responseObserver) {

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
            com.logalyzes.logs.dtos.LogsRequest request,
            StreamObserver<com.logalyzes.logs.dtos.LogsResponse> responseObserver)  {
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
    public void detail(com.logalyzes.logs.dtos.LogRequest request,
                       StreamObserver<com.logalyzes.logs.dtos.LogResponse> responseObserver) {
       responseObserver.onNext(com.logalyzes.logs.dtos.LogResponse.newBuilder().build());
    }
}
