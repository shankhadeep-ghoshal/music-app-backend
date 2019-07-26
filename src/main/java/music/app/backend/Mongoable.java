package music.app.backend;

import java.util.Map;


public interface Mongoable {
    Map<String, Object> makeEntityMapForMongo();
}