package clearancegood.services;

import clearancegood.entities.Good;
import clearancegood.entities.Seller;
import clearancegood.repositories.GoodRepository;
import clearancegood.repositories.SellerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodService {

    private GoodRepository goodRepository;

    private SellerRepository sellerRepository;

    public GoodService(GoodRepository goodRepository, SellerRepository sellerRepository) {
        this.goodRepository = goodRepository;
        this.sellerRepository = sellerRepository;
    }

    public Seller getSeller(String key) {
        return sellerRepository.findByKkey(key).orElse(null);
    }

    public Seller getSeller(Long id) {
        return sellerRepository.findById(id).orElse(null);
    }

    public void saveSeller(Seller seller) {
        sellerRepository.save(seller);
    }

    public Good getGood(Long id) {
        return goodRepository.findById(id).orElse(null);
    }

    public void saveGood(Good good) {
        goodRepository.save(good);
    }

    public boolean existsGoodByPictureOrName(String picture, String name) {return (goodRepository.existsByPicture(picture) || goodRepository.existsByName(name));}

    public List<Good> getAllGoods(Integer page, Integer count) {
        return goodRepository.findAll(PageRequest.of(page - 1, count)).toList();
        //return goodRepository.findAll();
    }
}
