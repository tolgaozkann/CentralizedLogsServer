package logalyzes.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Mapper extends ObjectMapper {

    private static Mapper instance = null;

    private Mapper() {
        super();
        this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public static Mapper getInstance() {
        if(instance == null) {
            instance = new Mapper();
        }
        return instance;
    }
}
