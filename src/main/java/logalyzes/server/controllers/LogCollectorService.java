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
            StreamObserver<LogCreatedResponse> responseObserver)
    {
        try{
            System.out.println(request);
            String _req = Mapper.getInstance().writeValueAsString(new LogDto(request));
            boolean res = this.repo.save(_req).get();
            responseObserver.onNext(LogCreatedResponse.newBuilder().setCreated(res).build());
            responseObserver.onCompleted();
        }catch (Exception e){
            logger.log(e,"Error while processing message");
            responseObserver.onError(e);
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
                    boolean res = repo.save(value).get();

                    // Send a response for each processed message
                    responseObserver.onNext(LogCreatedResponse.newBuilder().setCreated(res).build());
                } catch (Exception e) {
                    // Handle exceptions and report errors
                    logger.log(e,"Error while processing message");
                    responseObserver.onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error");
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
