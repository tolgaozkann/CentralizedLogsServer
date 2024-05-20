package logalyzes.server.controllers;

import com.logalyzes.notifications.dtos.NotificationServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import logalyzes.server.models.User;
import logalyzes.server.models.UserDAO;
import logalyzes.server.utils.logger.LOG_LEVEL;
import logalyzes.server.utils.logger.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationSettingService extends NotificationServiceGrpc.NotificationServiceImplBase {
    private UserDAO usersRepo;
    private Logger logger;


    public NotificationSettingService(){
        this.usersRepo = new UserDAO();
        this.logger = Logger.getInstance();

    }


    public void listNotifications(com.logalyzes.notifications.dtos.ListNotificationsRequest request,
                                  StreamObserver<com.logalyzes.notifications.dtos.ListNotificationsResponse> responseObserver) {
        try {
            List<com.logalyzes.notifications.dtos.User> users = this.usersRepo.selectAll().stream().map( user -> {
                return com.logalyzes.notifications.dtos.User.newBuilder().setId(user.getId()).setUsername(user.getUsername())
                        .setEmail(user.getEmail())
                        .addAllAttentionLevels(Arrays.stream(user.getAttentionLevels()).boxed().collect(Collectors.toList()))
                        .build();
            }).toList();

            com.logalyzes.notifications.dtos.ListNotificationsResponse res = com.logalyzes.notifications.dtos.ListNotificationsResponse.newBuilder()
                            .addAllUsers(users)
                            .setTotal(users.size())
                            .build();

            responseObserver.onNext(res);
            responseObserver.onCompleted();



        }catch (Error e){
            logger.log(LOG_LEVEL.ERROR, "Error on user list, " + e.toString());
            responseObserver.onError(e);
        }
    }


    public void setNotification(com.logalyzes.notifications.dtos.SetNotificationRequest request,
                                StreamObserver<com.logalyzes.notifications.dtos.User> responseObserver) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setAttentionLevels(request.getAttentionLevelsList().stream().mapToInt(i -> i).toArray());

            this.usersRepo.insert(user);



            com.logalyzes.notifications.dtos.User responseUser = com.logalyzes.notifications.dtos.User.newBuilder()
                    .setId(user.getId())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .addAllAttentionLevels(Arrays.stream(user.getAttentionLevels()).boxed().collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(responseUser);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.log(LOG_LEVEL.ERROR, "Error in setNotification, " + e.toString());
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }


    public void updateNotification(com.logalyzes.notifications.dtos.UpdateNotificationRequest request,
                                   StreamObserver<com.logalyzes.notifications.dtos.User> responseObserver) {

        try {

            User user = this.usersRepo.getById(request.getUserId());
            user.setAttentionLevels(request.getAttentionLevelsList().stream().mapToInt(i -> i).toArray());


            this.usersRepo.update(user); // Update user in database

            com.logalyzes.notifications.dtos.User responseUser = com.logalyzes.notifications.dtos.User.newBuilder()
                    .setId(user.getId())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .addAllAttentionLevels(Arrays.stream(user.getAttentionLevels()).boxed().collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(responseUser);
            responseObserver.onCompleted();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            logger.log(LOG_LEVEL.ERROR, "Error in updateNotification, " + e.toString());
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }


    public void removeNotification(com.logalyzes.notifications.dtos.RemoveNotificationRequest request,
                                   StreamObserver<com.logalyzes.notifications.dtos.User> responseObserver) {
        try {
            this.usersRepo.delete(request.getUserId()); // Delete user from database
            responseObserver.onNext(com.logalyzes.notifications.dtos.User.newBuilder()
                    .setId(request.getUserId())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.log(LOG_LEVEL.ERROR, "Error in removeNotification, " + e.toString());
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }
}
