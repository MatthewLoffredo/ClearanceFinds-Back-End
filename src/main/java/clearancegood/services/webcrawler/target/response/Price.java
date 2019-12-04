package clearancegood.services.webcrawler.target.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Price {

    @JsonProperty("current_retail")
    private Double price;


    private Double comparisonPrice;

    @JsonProperty("formatted_comparison_price_type")
    private String comparisonPriceType;

    @JsonProperty("current_retail_min")
    private void setP(Double price) {
        this.price = price;
    }

    @JsonProperty("formatted_comparison_price")
    private void setComparisonPrice(String comparisonPrice) {
        int i = comparisonPrice.indexOf("-");
        if (i > 0) {
            this.comparisonPrice = Double.valueOf(comparisonPrice.substring(1, i));
        } else this.comparisonPrice = Double.valueOf(comparisonPrice.substring(1));
    }

    @Override
    public String toString() {
        return "Price{" +
                "price=" + price +
                ", comparisonPrice='" + comparisonPrice + '\'' +
                ", comparisonPriceType='" + comparisonPriceType + '\'' +
                '}';
    }
}
