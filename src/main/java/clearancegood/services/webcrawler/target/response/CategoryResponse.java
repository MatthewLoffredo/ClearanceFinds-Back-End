package clearancegood.services.webcrawler.target.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryResponse {

    private CategoryMetaData metadata;
}
