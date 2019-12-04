
package clearancegood.services.webcrawler.target;

import clearancegood.entities.Good;
import clearancegood.services.GoodService;
import clearancegood.services.webcrawler.AbstractWebCrawler;
import clearancegood.services.webcrawler.SellerInfo;
import clearancegood.services.webcrawler.target.response.ApiResponse;
import clearancegood.services.webcrawler.target.response.CategoryMetaDataInfo;
import clearancegood.services.webcrawler.target.response.CategoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TargetWebCrawler extends AbstractWebCrawler {

    private final static String TARGET_KEY = "target";

    private RestTemplate restTemplate;


    public TargetWebCrawler(GoodService goodService, RestTemplate restTemplate) {
        super(TARGET_KEY, goodService);
        this.restTemplate = restTemplate;
    }

    @Override
    public SellerInfo readSellerInfo() {
        return new SellerInfo("Target.com", "https://queencreekmarketplace.com/wp-content/uploads/sites/3/2014/07/Target-Logo.png", "http://www.target.com");
    }

    @Override
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void readData() {

        try {
            int offset = 0;
            int count = 96;
            int currentPage = 0;
            int totalPage = 100;
            do {
                Map<String, String> uriVariable = new HashMap<>();
                uriVariable.put("count", String.valueOf(count));
                uriVariable.put("offset", String.valueOf(offset));
                ApiResponse response = restTemplate.getForObject(
                        "https://redsky.target.com/v2/plp/search/?category=5q0ga&channel=web&count={count}&default_purchasability_filter=true&facet_recovery=false&offset={offset}&pageId=%2Fc%2F5q0ga&pricing_store_id=3272&key=eb2551e4accc14f38cc42d32fbc2b2ea",
                        ApiResponse.class,
                        uriVariable
                );

                if (response.getErrorResponse() != null) break;

                count = Integer.parseInt(response.getSearchResponse().getMetaData("count"));
                offset = Integer.parseInt(response.getSearchResponse().getMetaData("offset"));
                offset += count;

                totalPage = Integer.parseInt(response.getSearchResponse().getMetaData("totalPages"));
                currentPage = Integer.parseInt(response.getSearchResponse().getMetaData("currentPage"));

                response.getSearchResponse().getItems().getItems().forEach(i -> {
                    try {
                        if (i.getPrice() == null) return;
                        Good good = new Good();
                        good.setDescription(i.getDescription());
                        good.setBrand(i.getBrand());
                        good.setLink(seller.getWebsite() + i.getUrl());
                        if(i.getTitle().contains(" - ")) {
                            good.setName(i.getTitle().substring(0,i.getTitle().indexOf(" -")));
                        }
                        else good.setName(i.getTitle());
                        good.setPrice(i.getPrice().getPrice());
                        good.setProviderId(i.getTcin());
                        good.setCategory(this.readCategory(i.getUrl()));
                        if ("reg".equals(i.getPrice().getComparisonPriceType())) {
                            good.setRegPrice(Double.valueOf(i.getPrice().getComparisonPrice()));
                        }
                        good.setPicture(i.getImages().get(0).getURL());
                        good.setSeller(this.seller);
                        if(goodService.existsGoodByPictureOrName(good.getPicture(), good.getName()))
                            return;
                        goodService.saveGood(good);
                    } catch (Exception cve) {
                        log.warn(cve.getMessage(), cve);
                    }

                });
            } while (currentPage < totalPage);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

    }

    private String readCategory(String url) throws Exception {
        Map<String, String> uriVariable = new HashMap<>();
        uriVariable.put("url", url);
        CategoryResponse response = restTemplate.getForObject(
                "https://redoak.target.com/content-publish/pages/v1?url={url}&breadcrumbs=true",
                CategoryResponse.class,
                uriVariable
        );
        String s = "";
        for (CategoryMetaDataInfo i : response.getMetadata().getBreadcrumbs()) {
            s += i.getName() + ",";
        }
        return s;
    }
}
