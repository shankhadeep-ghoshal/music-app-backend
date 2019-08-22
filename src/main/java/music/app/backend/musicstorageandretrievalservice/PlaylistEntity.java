package music.app.backend.musicstorageandretrievalservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import music.app.backend.Mongoable;
import music.app.backend.UtilityClass;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Introspected
public class PlaylistEntity implements Mongoable {
    private static final int LENGTH_OF_ALBUM_ID = 16;
    @NotNull @NotBlank @Size(min = 64, max = 64, message = "Required 64 characters in length")
    private String userId;
    private String albumId;
    @NotBlank @NotNull private String albumName;
    private LocalDateTime createdOn;
    private int actualSongCount;
    private int songCountForShow;

    /**
     * @apiNote congCntA - Actual Song count. It will never get decreased, only increment <br>
     * songCntS - Song count to be shown to end user
     * @return an unmodifiable map of the entity
     */
    @Override
    public Map<String, Object> makeEntityMapForMongo() {
        Map<String, Object> returnMap = new HashMap<String, Object>(){{
            put("userId", userId);
            put("albumId", UtilityClass.generateClientToken(LENGTH_OF_ALBUM_ID));
            put("albumName", albumName);
            put("createdOn", createdOn);
            put("songCntA", 0);
            put("songCntS", 0);
        }};

        return Collections.unmodifiableMap(returnMap);
    }
}