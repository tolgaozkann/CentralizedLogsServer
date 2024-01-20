package logalyzes.server.utils.logger;

import logalyzes.server.utils.DateUtils;

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
        String logMsg = String.format("%s - %s - %s", DateUtils.getFullStringDate(), level.toString(), msg);
        System.out.println(logMsg);
    }

    public void log(Exception e,String msg) {
        String logMsg = String.format("%s - %s - %s", DateUtils.getFullStringDate(), LOG_LEVEL.ERROR.toString(), msg);
        System.out.print(logMsg);
        e.printStackTrace();
    }


}
