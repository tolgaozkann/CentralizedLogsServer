package logalyzes.server.templates;

import java.util.HashMap;
import java.util.Map;

public class NotifyLogTemplate extends  MailTemplate{
    private  final  static String tempName = "email_template.ftl";
    private  final  static String title = "Logalyzes Notification - Attention Need";


    public NotifyLogTemplate(String level, String heading, String message) {
        super(tempName, createTemplateData(level, heading, message));
    }

    private static Map<String, Object> createTemplateData(String level ,String heading, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("logId", level);
        data.put("heading", heading);
        data.put("message", message);
        return data;
    }
}
