package logalyzes.server.controllers;

import  io.grpc.stub.StreamObserver;

import com.logalyzes.logs.dtos.LogsServiceGrpc;
import com.logalyzes.logs.dtos.LogsServiceGrpc.LogsServiceImplBase;
import  com.logalyzes.logs.dtos.*;


public class LogController extends LogsServiceImplBase {

    /**
     */
    public void list(
            LogsRequest request,
            StreamObserver<LogsResponse> responseObserver) {
        responseObserver.onNext(LogsResponse.newBuilder().build());
    }

    /**
     */
    public void detail(LogRequest request,
                      StreamObserver<LogResponse> responseObserver) {
       responseObserver.onNext(LogResponse.newBuilder().build());
    }
}
