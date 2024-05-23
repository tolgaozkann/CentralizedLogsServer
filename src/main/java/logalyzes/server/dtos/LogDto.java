package logalyzes.server.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.logalyzes.logs.dtos.LogsMessages;
import logalyzes.server.utils.logger.LOG_LEVEL;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import  java.util.UUID;
public class LogDto {
    @JsonProperty("id")
    private String id;
    @JsonProperty("logTime")
    private  String logTime;
    @JsonProperty("level")
    private LOG_LEVEL level;
    @JsonProperty("message")
    private String message;
    @JsonProperty("stackTrace")
    private String stackTrace;
    @JsonProperty("application")
    private Application application;


    enum APP_ENVIRONMENT{
        DEV ,
        TEST,
        PROD
    }


    static class Application {
        @JsonProperty("name")
        private String name;
        @JsonProperty("version")
        private String version;
        @JsonProperty("environment")
        private APP_ENVIRONMENT environment;

        public Application() {
            // Default constructor
        }

        @JsonCreator
        public Application(@JsonProperty("name") String name, @JsonProperty("version") String version, @JsonProperty("environment") APP_ENVIRONMENT environment) {
            this.name = name;
            this.version = version;
            this.environment = environment;
        }

    }


    @JsonCreator
    public LogDto(@JsonProperty("id") String id, @JsonProperty("logTime") String logTime, @JsonProperty("level") LOG_LEVEL level, @JsonProperty("message") String message, @JsonProperty("stackTrace") String stackTrace, @JsonProperty("application") Application application) {
        this.id = id;
        this.logTime = logTime;
        this.level = level;
        this.message = message;
        this.stackTrace = stackTrace;
        this.application = application;
    }


    public LogDto(LogsMessages.LogForCreate log) {
        this.id = UUID.randomUUID().toString();
        this.logTime = LocalDateTime.now().toString();
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

    public LogsMessages.Log toLog() {
        return LogsMessages.Log.newBuilder()
                .setId(this.id)
                .setLogTime(convertLogTimeToTimestamp(this.logTime)) // Convert logTime to timestamp
                .setLevel(convertLogLevel(this.level))
                .setMessage(this.message)
                .setStackTrace(this.stackTrace)
                .setApplication(LogsMessages.Application.newBuilder()
                        .setName(this.application.name)
                        .setEnvironment(convertAppEnvironment(this.application.environment))
                        .setVersion(this.application.version)
                        .build())
                .build();
    }

    private long convertLogTimeToTimestamp(String logTime) {
        // Parse the logTime string into a LocalDateTime object
        LocalDateTime dateTime = LocalDateTime.parse(logTime, DateTimeFormatter.ISO_DATE_TIME);

        // Convert LocalDateTime to a timestamp (milliseconds since Unix epoch)
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    private LogsMessages.APP_ENVIRONMENT convertAppEnvironment(LogDto.APP_ENVIRONMENT appEnv) {
        switch (appEnv) {
            case DEV:
                return LogsMessages.APP_ENVIRONMENT.DEV;
            case TEST:
                return LogsMessages.APP_ENVIRONMENT.TEST;
            case PROD:
                return LogsMessages.APP_ENVIRONMENT.PROD;
            default:
                throw new IllegalArgumentException("Invalid environment: " + appEnv);
        }
    }





    private LogsMessages.LOG_LEVEL convertLogLevel(LOG_LEVEL level) {
        // Convert LOG_LEVEL to LogsMessages.LOG_LEVEL
        switch (level) {
            case DEBUG:
                return LogsMessages.LOG_LEVEL.DEBUG;
            case INFO:
                return LogsMessages.LOG_LEVEL.INFO;
            case WARN:
                return LogsMessages.LOG_LEVEL.WARN;
            case ERROR:
                return LogsMessages.LOG_LEVEL.ERROR;
            case FATAL:
                return LogsMessages.LOG_LEVEL.FATAL;
            default:
                throw new IllegalArgumentException("Unknown log level: " + level);
        }
    }


    public String getId() {
        return id;
    }

    @Override
    public String toString(){
        return String.format("LogDto: %s - %s - %s - %s - %s - %s",this.id,this.logTime,this.level,this.message,this.stackTrace,this.application);
    }

}
