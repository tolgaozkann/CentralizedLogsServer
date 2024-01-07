package logalyzes.server.repositories;


import com.logalyzes.health.dtos.HealthCheckResponse.ServingStatus;

public class HealthManager {
    ServingStatus status = null;

    public  HealthManager() {
        this.status = ServingStatus.SERVING;
    }

    public  ServingStatus getStatus() {
        return this.status;
    }

    public  void setStatus(ServingStatus status) {
        this.status = status;
    }

}
