package logalyzes.server.utils;

import java.util.MissingResourceException;

enum LOG_LEVEL{
    INFO,
    WARNING,
    ERROR,
}

public class Logger  {
    private static Logger instance = null;
    private  String host;


    private Logger() {
        this.host = "Logalyzes_Server";
    }

    public static Logger getInstance() {
        if(instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    /**
     *  msg: message to log
     *  level: level of the message
     */
    public void log(LOG_LEVEL level,String msg) {
        String logMsg = String.format("%s - %s - %s",DateUtils.getFullStringDate(), level.toString(), msg);
        System.out.print(logMsg);
    }


}
