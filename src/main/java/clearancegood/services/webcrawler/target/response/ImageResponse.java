package clearancegood.services.webcrawler.target.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageResponse {

    @JsonProperty("base_url")
    private String baseUrl;

    private String primary;

    public String getURL() {
        return baseUrl + primary;
    }
}
