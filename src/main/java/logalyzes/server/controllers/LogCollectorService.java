package logalyzes.server.controllers;


import com.logalyzes.logs.dtos.LogCreatedResponse;
import com.logalyzes.logs.dtos.LogCollectorServiceGrpc.LogCollectorServiceImplBase;

import  com.logalyzes.logs.dtos.LogsMessages.*;
import io.grpc.Status;
import io.grpc.stub.*;
import logalyzes.server.core.NotificationProcessor;
import logalyzes.server.core.NotificationTask;
import logalyzes.server.core.Predictor;
import logalyzes.server.dtos.LogDto;
import logalyzes.server.repositories.LogCollectorRepository;
import logalyzes.server.utils.logger.LOG_LEVEL;
import logalyzes.server.utils.logger.Logger;

import java.util.concurrent.CompletableFuture;


public class LogCollectorService extends LogCollectorServiceImplBase {
    private LogCollectorRepository repo;
    private  Logger logger = null;
    private Predictor predictor;

    // Notification processor
    NotificationProcessor processor;


    public LogCollectorService() {
        this.repo = new LogCollectorRepository();
        this.logger = Logger.getInstance();
        this.predictor = new Predictor();

        this.processor = new NotificationProcessor(3);
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


                LogDto dto = new LogDto(request);
                // Process the message
                this.repo.save(dto);


                // Handle and send request to prediction
                String log =  request.getApplication().getName()
                        + request.getApplication().getVersion()
                        + request.getMessage()
                        + request.getStackTrace();

                int[] res = predictor.sendLog(log);

                // Process output with async
                this.processor.queueTask(new NotificationTask(dto.getId(),res));

            } catch (Exception e) {
                String msg = "Error while processing message";
                logger.log(LOG_LEVEL.ERROR,msg + e);
                responseObserver.onError(Status.INTERNAL.withDescription(msg).asException());
                throw new RuntimeException(e);
            }
        });



    }

}
