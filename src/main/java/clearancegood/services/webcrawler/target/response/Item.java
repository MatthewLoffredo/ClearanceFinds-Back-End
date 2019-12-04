package clearancegood.services.webcrawler.target.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

    private String url;
    private String brand;
    private String description;
    private String title;
    private String tcin;
    private Price price;

    @JsonProperty
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<ImageResponse> images;

    @Override
    public String toString() {
        return "Item{" +
                "url='" + url + '\'' +
                ", brand='" + brand + '\'' +
                ", title='" + title + '\'' +
                ", tcin='" + tcin + '\'' +
                ", price=" + price +
                '}';
    }
}
