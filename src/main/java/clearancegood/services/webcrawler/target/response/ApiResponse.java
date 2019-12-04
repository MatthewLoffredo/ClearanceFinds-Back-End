package clearancegood.services.webcrawler.target.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {

    @JsonProperty("search_response")
    private SearchResponse searchResponse;

    @JsonProperty("error_response")
    private ErrorResponse errorResponse;

    @Override
    public String toString() {
        return "ApiResponse{" +
                "searchResponse=" + searchResponse.toString() +
                '}';
    }
}
