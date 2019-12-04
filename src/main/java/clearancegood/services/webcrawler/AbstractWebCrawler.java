package clearancegood.services.webcrawler;

import clearancegood.entities.Seller;
import clearancegood.services.GoodService;

public abstract class AbstractWebCrawler implements WebCrawler {

    protected String kkey;
    protected GoodService goodService;
    protected Seller seller;

    public AbstractWebCrawler(String key, GoodService goodService) {
        this.kkey = key;
        this.goodService = goodService;
        this.initSeller();
    }

    protected void initSeller(){
        Seller s=goodService.getSeller(kkey);
        if(s==null) this.refreshSeller();
        else this.seller=s;
    }

    protected void refreshSeller() {
        SellerInfo info = readSellerInfo();
        if (this.seller == null) this.seller = new Seller();
        seller.setKkey(this.kkey);
        seller.setLogo(info.getLogo());
        seller.setName(info.getName());
        seller.setWebsite(info.getWebsite());
        goodService.saveSeller(this.seller);
    }

    ;

    public abstract SellerInfo readSellerInfo();
}
