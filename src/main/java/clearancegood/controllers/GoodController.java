package clearancegood.controllers;

import clearancegood.entities.Good;
import clearancegood.services.GoodSearch;
import clearancegood.services.GoodService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins="http://localhost:4200/")
@RestController
public class GoodController {

    private GoodService goodService;
    private GoodSearch goodSearch;

    public GoodController(GoodService goodService, GoodSearch goodSearch) {
        this.goodService = goodService;
        this.goodSearch = goodSearch;
    }

    @GetMapping(value = "/goods", produces = "application/json")
    public @ResponseBody
    List<Good> getAllGoods(@RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "count", required = false) Integer count) { // converted into JSON
        if (page == null) page = 1;
        if (count == null) count = 30;
        List<Good> allGoods = goodService.getAllGoods(page, count);

        return allGoods;
    }

    @GetMapping(value = "/goods/{id}", produces = "application/json")
    public @ResponseBody
    Good getTopic(@PathVariable Long id) {
        return goodService.getGood(id);
    }

    @GetMapping(value = "/goods", produces = "application/json", params = {"q"})
    public @ResponseBody
    List<Good> search(@RequestParam("q") String q,
                      @RequestParam(value = "page", required = false) Integer page,
                      @RequestParam(value = "count", required = false) Integer count) {
        if (page == null) page = 1;
        if (count == null) count = 30;
        Integer start = (page - 1) * count;
        List<Good> allGoods = goodSearch.search(q, start, count);

        return allGoods;
    }

}
