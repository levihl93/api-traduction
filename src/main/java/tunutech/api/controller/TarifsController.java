package tunutech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tunutech.api.services.PriceService;

@RestController
@RequestMapping("/price/")
public class TarifsController {
    @Autowired
    private PriceService priceService;
    @GetMapping("getprice/{typeDocument}")
    public ResponseEntity<?>getPrice(@PathVariable String typeDocument)
    {
        return ResponseEntity.ok(priceService.gettarif(typeDocument));
    }
}
