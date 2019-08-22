package music.app.backend.musicstorageandretrievalservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import music.app.backend.Mongoable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Introspected
public class SongEntity implements Mongoable {
    private String id;
    private String playlistId;
    @NotNull @NotBlank @Pattern(regexp = "^.*\\.(mp3|flac|MP3|FLAC)$") private String fileName;
    private String genre;
    private String artist;
    private long duration;

    @Override
    public Map<String, Object> makeEntityMapForMongo() {
        Map<String, Object> returnMap = new HashMap<String, Object>(){{
            put("id", id);
            put("playlistId", playlistId);
            put("name", fileName);
            put("genre", genre);
            put("artist", artist);
            put("duration", duration);
        }};

        return Collections.unmodifiableMap(returnMap);
    }
}