package com.windsOfMoney.Models;

import java.util.List;
import java.util.Map;

public class StockModel {
   public String symbol;
   public double currentPrice;
   public Map<String, String> fundamentals;
   public List<Map<String, String>> news;
   public double sentimentScore;
   public double predictedReturn1M;
   public String outlook;
}
