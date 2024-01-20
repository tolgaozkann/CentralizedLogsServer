package logalyzes.server.dtos;

import com.logalyzes.logs.dtos.LogsMessages;
import logalyzes.server.utils.logger.LOG_LEVEL;

import java.time.LocalDateTime;
import  java.util.UUID;
public class Log {
    private String id;
    private  LocalDateTime logTime;
    private LOG_LEVEL level;
    private String message;
    private String stackTrace;
    private Application application;



    enum APP_ENVIRONMENT{
        DEV ,
        TEST,
        PROD
    }

    class Application {
        private  String name;
        private  String version;
        private APP_ENVIRONMENT environment;

        public Application(String name, String version, APP_ENVIRONMENT environment) {
            this.name = name;
            this.version = version;
            this.environment = environment;
        }

    }

    public Log(String id, LocalDateTime logTime, LOG_LEVEL level, String message, String stackTrace, Application application) {
        this.id = id;
        this.logTime = logTime;
        this.level = level;
        this.message = message;
        this.stackTrace = stackTrace;
        this.application = application;
    }

    public Log(LogsMessages.LogForCreate log) {
        this.id = UUID.randomUUID().toString();
        this.logTime = LocalDateTime.now();
        this.level = getLogLevel(log);
        this.message = log.getMessage();
        this.stackTrace = log.getStackTrace();
        this.application = getApplication(log);
    }



    private  LOG_LEVEL getLogLevel(LogsMessages.LogForCreate log){
        switch (log.getLevel()){
            case INFO:
                return LOG_LEVEL.INFO;
            case WARN:
                return LOG_LEVEL.WARN;
            case ERROR:
                return LOG_LEVEL.ERROR;
            case FATAL:
                return LOG_LEVEL.FATAL;
            case DEBUG:
                return LOG_LEVEL.DEBUG;
            default:
                return LOG_LEVEL.INFO;
        }
    }

    private Application getApplication(LogsMessages.LogForCreate log){
        APP_ENVIRONMENT env = APP_ENVIRONMENT.DEV;

        switch (log.getApplication().getEnvironment()){
            case DEV:
                env = APP_ENVIRONMENT.DEV;
                break;
            case TEST:
                env = APP_ENVIRONMENT.TEST;
                break;
            case PROD:
                env = APP_ENVIRONMENT.PROD;
                break;
        }

        return new Application(log.getApplication().getName(),log.getApplication().getVersion(),env);
    }

}
