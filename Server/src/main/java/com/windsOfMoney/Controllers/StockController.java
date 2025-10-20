package com.windsOfMoney.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.windsOfMoney.Models.StockModel;
import com.windsOfMoney.Services.StockService;

@RestController
@RequestMapping({"/api"})
@CrossOrigin(
   origins = {"*"}
)
public class StockController {
   @Autowired
   private StockService stockService;

   @GetMapping({"/stock"})
   public StockModel getStockInfo(@RequestParam String symbol) {
      return this.stockService.fetchStockInfo(symbol);
   }
}
