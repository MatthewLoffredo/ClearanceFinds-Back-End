package clearancegood.services.webcrawler.target.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaDataPair {

    private String name;
    private String value;

    @Override
    public String toString() {
        return "MetaDataPair{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
