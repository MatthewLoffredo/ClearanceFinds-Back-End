/*
package clearancegood.services.webcrawler.ulta;

import clearancegood.entities.Good;
import clearancegood.services.GoodService;
import clearancegood.services.webcrawler.AbstractWebCrawler;
import clearancegood.services.webcrawler.SellerInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class UltaWebCrawler extends AbstractWebCrawler {

    private final static String ULTA_KEY = "ulta";

    public UltaWebCrawler(GoodService goodService) {
        super(ULTA_KEY, goodService);
    }

    @Override
    public SellerInfo readSellerInfo() {
        return new SellerInfo("Ulta Beauty", "https://c7.uihere.com/files/149/652/1020/5bfc7501791d3-thumb.jpg", "https://www.ulta.com/");
    }

    @Override
    @Scheduled(fixedDelay = 3 * 60 * 60 * 1000, initialDelay = 2 * 1000)
    public void readData() {
        try {
            int offset = 0;
            int count = 98;
            boolean hasNextPage = false;
            do {
                Document doc = Jsoup.connect("https://www.ulta.com/promotion/sale?N=1z13uvl&No=" + offset + "&Nrpp=" + count).get();
                Elements lis = doc.select("#search-prod li .productQvContainer .quick-view-prod");

                hasNextPage = doc.select(".next-prev").text().contains("Next");
                for (Element e : lis) {
                    readProduct(e.select("a").attr("abs:href"));
                }
                offset += count;
            } while (hasNextPage);

        } catch (IOException ioe) {
            log.warn(ioe.getMessage());
        }

    }

    private void readProduct(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Element wrapper = doc.selectFirst(".ProductDetail__wrapper");

            Good good = new Good();

            good.setSeller(this.seller);

            Elements breadcrumbs = wrapper.select(".Breadcrumb ul>li>a");
            String category = "";
            for (int i = 1; i < breadcrumbs.size() - 1; i++) {
                category += "," + breadcrumbs.get(i).text();
            }
            good.setCategory(category);
            good.setBrand(wrapper.select(".ProductMainSection__brandName").text());
            good.setPicture(doc.select("meta[property='og:image']").attr("content"));
            good.setLink(url);
            good.setProviderId(url.substring(url.indexOf("productId=") + "productId".length()));
            good.setDescription(wrapper.select(".ProductDetail__productContent:eq(0)").text());
            good.setName(wrapper.select(".ProductMainSection__productName").text());


            if (wrapper.select(".ProductPricingPanel__salePrice").size() > 0) {
                wrapper.select(".ProductPricingPanel__salePrice>span>label").remove();
                good.setPrice(Double.valueOf(wrapper.select(".ProductPricingPanel__salePrice>span:eq(0)").text().substring(1)));
                good.setRegPrice(Double.valueOf(wrapper.select(".ProductPricingPanel__salePrice>span:eq(1)").text().substring(1)));
            } else {
                wrapper.select(".ProductPricingPanel>span>label").remove();
                good.setPrice(Double.valueOf(wrapper.select(".ProductPricingPanel>span:eq(0)").text().substring(1)));
            }

            goodService.saveGood(good);
        } catch (Exception ioe) {
            log.warn(ioe.getMessage() + ":" + url);
        }
    }
}
*/