package clearancegood.services.webcrawler.tjmaxx;

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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TjMaxxWebCrawler extends AbstractWebCrawler {

    private final static String TJMAXX_KEY = "tjmaxx";

    public TjMaxxWebCrawler(GoodService goodService) {
        super(TJMAXX_KEY, goodService);
    }

    @Override
    public SellerInfo readSellerInfo() {
        return new SellerInfo("T.J.Maxx", "http://static.tjmaxx.com/store/resources/images/logos/tjmaxxlogo.png", "https://tjmaxx.tjx.com/");
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

        /*
        Connection.Response response = Jsoup.connect("https://tjmaxx.tjx.com/store/shop/clearance/_/N-3951437597?Nr=AND%28OR%28product.catalogId%3Atjmaxx%29%2Cproduct.siteId%3Atjmaxx%29&ln=mc:1")
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .referrer("http://www.google.com")
                .timeout(12000)
                .followRedirects(true)
                .execute();

        Document doc = response.parse();
        */
        Document doc = Jsoup.connect("https://tjmaxx.tjx.com/store/shop/clearance/_/N-3951437597?Nr=AND%28OR%28product.catalogId%3Atjmaxx%29%2Cproduct.siteId%3Atjmaxx%29&ln=mc:1")
                .userAgent("Mozilla/5.0")
                .timeout(30 * 1000)
                .followRedirects(true)
                .get();

        List<String[]> categories = new ArrayList<>();
        Elements elements = doc.select(".page-content .left-nav #category-nav li");
        for (int i = 2; i < elements.size(); i++) {
            Element e = elements.get(i);
            String href = e.select("a").attr("abs:href");
            String categoryName = e.text();

            /*
            Connection.Response response2 = Jsoup.connect(href)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .timeout(12000)
                    .followRedirects(true)
                    .execute();

            Document subCateDoc = response2.parse();
             */
            Document subCateDoc = Jsoup.connect(href)
                    .userAgent("Mozilla/5.0")
                    .timeout(30 * 1000)
                    .followRedirects(true)
                    .get();

            //log.info(subCateDoc.select(".page-content .left-nav #category-nav li").html());
            Elements subCateElements = subCateDoc.select(".page-content .left-nav #category-nav li");
            for (int j = 2; j < subCateElements.size(); j++) {
                Element subE = subCateElements.get(j);
                String subHref = subE.select("a").attr("abs:href");
                String localCategory = categoryName + "," + subE.text();
                categories.add(new String[]{subHref, localCategory});
            }
        }

        return categories;
    }


    public void readCategoryData(String[] category) {

        try {
            /*
            Connection.Response response3 = Jsoup.connect(category[0])
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .timeout(12000)
                    .followRedirects(true)
                    .execute();

            Document doc = response3.parse();
            */
            Document doc = Jsoup.connect(category[0])
                    .userAgent("Mozilla/5.0")
                    .timeout(30 * 1000)
                    .followRedirects(true)
                    .get();

            Elements products = doc.select("section.content .product");
            for (Element e : products) {
                String productId = e.id();
                String productLink = e.select("a.product-link").attr("abs:href");
                productId = productId.substring(productId.indexOf("-") + 1);
                readGood(productId, productLink, category[1]);
            }

        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    private void readGood(String productId, String productLink, String category) {
        try {
            /*
            Connection.Response response4 = Jsoup.connect("https://tjmaxx.tjx.com/store/modal/quickview.jsp?productId=" + productId)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .timeout(12000)
                    .followRedirects(true)
                    .execute();

            Document doc = response4.parse();
             */
            Document doc = Jsoup.connect("https://tjmaxx.tjx.com/store/modal/quickview.jsp?productId=" + productId)
                    .userAgent("Mozilla/5.0")
                    .timeout(30 * 1000)
                    .followRedirects(true)
                    .get();

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
            if(goodService.existsGoodByPictureOrName(g.getPicture(), g.getName())) {
                //System.out.println("good denied: " + g.getName());
                return;
            }
            goodService.saveGood(g);
        } catch (Exception ioe) {
            log.warn(ioe.getMessage() + productLink, ioe);
        }

    }
}
