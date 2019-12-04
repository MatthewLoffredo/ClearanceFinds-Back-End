package clearancegood.services.webcrawler.target.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SearchResponse {

    private SearchResponseItems items;

    @JsonProperty
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<MetaDataPair> metaData;


    public String getMetaData(String key) {
        for (MetaDataPair p : metaData) {
            if (p.getName().equals(key)) return p.getValue();
        }
        return null;
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "items=" + items +
                ", metaData=" + metaData +
                '}';
    }
}
