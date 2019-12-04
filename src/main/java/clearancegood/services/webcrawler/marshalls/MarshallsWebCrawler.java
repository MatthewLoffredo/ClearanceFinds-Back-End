
package clearancegood.services.webcrawler.marshalls;

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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MarshallsWebCrawler extends AbstractWebCrawler {

    private final static String MARSHALL_KEY = "marshall";

    public MarshallsWebCrawler(GoodService goodService) {
        super(MARSHALL_KEY, goodService);
    }

    @Override
    public SellerInfo readSellerInfo() {
        return new SellerInfo("Marshalls.com", "https://static.marshalls.com/content/logos/Marshalls-Logo.svg", "http://www.marshalls.com");
    }

    @Override
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void readData() {
        try {
            List<String[]> categories = readCategories();
            for (String[] c : categories) {
                readCategoryData(c);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

    }

    private List<String[]> readCategories() throws IOException {
        Document doc = Jsoup.connect("https://www.marshalls.com/us/store/shop/clearance/_/N-3951437597").get();

        List<String[]> categories = new ArrayList<>();
        Elements submenus = doc.select("#usmm-dd-cat3620196p .sub-menu__category");
        for (Element e : submenus) {
            String categoryName = e.select(".sub-menu__header").text();


            Elements subsubmenus = e.select(".sub-menu__item a");
            for (Element sube : subsubmenus) {
                String s = sube.text();
                if ("View All".equals(s)) continue;
                String localCategoryName = categoryName + "," + s;
                String categoryId = StringUtils.getFilename(sube.attr("abs:href"));
                categoryId = categoryId.substring(0, categoryId.indexOf("?"));
                categories.add(new String[]{categoryId, localCategoryName});
            }

        }

        return categories;
    }


    public void readCategoryData(String[] category) {

        try {
            int offset = 0;
            int count = 180;
            Document doc = Jsoup.connect("https://www.marshalls.com/us/store/products/clearance/_/" + category[0] + "?No=" + offset + "&Nrpp=" + count).get();
            Elements products = doc.select("section.content .product");
            for (Element e : products) {
                String productId = e.id();
                String productLink = e.select("a.product-link").attr("abs:href");
                productId = productId.substring(productId.indexOf("-") + 1);
                readGood(productId, productLink, category[1]);
            }

        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    private void readGood(String productId, String productLink, String category) {
        try {


            Document doc = Jsoup.connect("https://www.marshalls.com/us/store/modal/quickview.jsp?productId=" + productId).get();
            Element e = doc.selectFirst("section.product");
            Good g = new Good();
            g.setProviderId(productId);
            g.setLink(productLink);
            g.setCategory(category);
            g.setBrand(e.select(".product-brand").text());
            g.setName(e.select(".product-title").text());
            g.setSeller(this.seller);
            Element priceTag = e.select(".product-price").get(0);

            String priceStr = priceTag.select(".original-price").text();
            priceStr = priceStr.replaceAll(",", "");
            if (priceStr.length() > 0) {
                if (priceStr.indexOf("—") > 0) {
                    priceStr = priceStr.substring(0, priceStr.indexOf("—")).trim();
                }
                g.setRegPrice(Double.valueOf(priceStr.substring(1)));
            }

            g.setPicture(e.select(".product-image .zoom-main img").attr("abs:src"));
            g.setDescription(e.select(".product-description").html());
            priceTag.select(".original-price").remove();
            priceStr = priceTag.text();
            priceStr = priceStr.replaceAll(",", "");
            if (priceStr.indexOf("–") > 0) {
                priceStr = priceStr.substring(0, priceStr.indexOf("–")).trim();
            }
            if (priceStr.indexOf("—") > 0) {
                priceStr = priceStr.substring(0, priceStr.indexOf("—")).trim();
            }
            g.setPrice(Double.valueOf(priceStr.substring(1)));

            if(goodService.existsGoodByPictureOrName(g.getPicture(), g.getName()))
                return;
            goodService.saveGood(g);
        } catch (Exception ioe) {
            log.warn(ioe.getMessage());
        }

    }
}
