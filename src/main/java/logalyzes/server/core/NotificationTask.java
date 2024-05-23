package logalyzes.server.core;

import logalyzes.server.models.User;
import logalyzes.server.models.UserDAO;
import logalyzes.server.templates.MailTemplate;
import logalyzes.server.templates.NotifyLogTemplate;
import logalyzes.server.utils.logger.LOG_LEVEL;

import java.util.List;

public class NotificationTask implements  Runnable{
    private  boolean isAnomany;
    private  boolean needAttention;
    private  int level;

    private String logId;



    public NotificationTask(String logId,int[] predictionResponse){
        if(predictionResponse.length == 3){
            isAnomany = predictionResponse[0] == 1;
            needAttention = predictionResponse[1] == 1;
            level = predictionResponse[2];
        }

        this.logId = logId;

    }





    @Override
    public void run() {

        if(this.isAnomany && this.needAttention){
            
            // Retrive user has attantion level
            int[] levelToSeach = {this.level};

            List<User> users = UserDAO.getInstance().findUsersByAttentionLevels(levelToSeach);

            // Mail Datas
            LOG_LEVEL logLevel = LOG_LEVEL.fromInt(this.level);
            String heding = "Attention for your servers";
            String message = "An anomlies detected on your applications, please look at your applications of this log: "+ this.logId;

            // Generate mail from template
            MailTemplate template = new NotifyLogTemplate(
                    logLevel.toString(),
                    heding,
                    message
            );

            // Send notification for all this users
            for(User user : users){
                EmailService.getInstance().sendEmail(user.getEmail(),"Attention - Logalyses api", template);
            }
        }



    }
}
