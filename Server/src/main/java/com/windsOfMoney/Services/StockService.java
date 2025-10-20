package com.windsOfMoney.Services;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.windsOfMoney.Models.StockModel;

@Service
public class StockService {
   private RestTemplate rest = new RestTemplate();
   @Value("${news.api.key}")
   private String newsKey;
   @Value("${sentiment.service.url}")
   private String sentimentUrl;

   public StockModel fetchStockInfo(String symbol) {
      StockModel resp = new StockModel();
      resp.symbol = symbol.toUpperCase();
      String url = "https://api.tradient.org/v1/api/market/technicals?symbol=" + symbol + "&duration=1";

      try {
         Map<String, Object> responseMap = (Map)this.rest.getForObject(url, Map.class, new Object[0]);
         if (responseMap != null && !responseMap.isEmpty()) {
            Object priceObj = responseMap.get("close|1");
            double price = 0.0D;
            if (priceObj != null) {
               price = Double.parseDouble(priceObj.toString());
            }

            resp.currentPrice = price;
            resp.fundamentals = new HashMap();
            double rsi = Double.parseDouble(responseMap.get("RSI|1").toString());
            double ema20 = Double.parseDouble(responseMap.get("EMA20|1").toString());
            double macd = Double.parseDouble(responseMap.get("MACD.macd|1").toString());
            double rec = Double.parseDouble(responseMap.get("Recommend.All|1").toString());
            String rsiInterpretation;
            if (rsi < 30.0D) {
               rsiInterpretation = "Oversold (possible upward reversal)";
            } else if (rsi > 70.0D) {
               rsiInterpretation = "Overbought (possible correction)";
            } else {
               rsiInterpretation = "Neutral momentum";
            }

            String macdInterpretation = macd > 0.0D ? "Bullish trend" : "Bearish trend";
            String recInterpretation;
            if (rec > 0.3D) {
               recInterpretation = "Strong Buy Signal";
            } else if (rec > 0.0D) {
               recInterpretation = "Buy Bias";
            } else if (rec < -0.3D) {
               recInterpretation = "Strong Sell Signal";
            } else if (rec < 0.0D) {
               recInterpretation = "Sell Bias";
            } else {
               recInterpretation = "Neutral";
            }

            resp.fundamentals.put("Relative Strength Index (RSI)", String.format("%.2f - %s", rsi, rsiInterpretation));
            resp.fundamentals.put("Exponential Moving Average (20-day EMA)", String.format("%.2f", ema20));
            resp.fundamentals.put("MACD Indicator", String.format("%.2f - %s", macd, macdInterpretation));
            resp.fundamentals.put("Market Recommendation", recInterpretation);
         } else {
            resp.currentPrice = 0.0D;
            resp.fundamentals = new HashMap();
         }
      } catch (Exception var21) {
         var21.printStackTrace();
         resp.currentPrice = 0.0D;
         resp.fundamentals = new HashMap();
      }

      try {
         String query = symbol + " stock India";
         String var10000 = URLEncoder.encode(query, "UTF-8");
         String newsUrl = "https://newsapi.org/v2/everything?q=" + var10000 + "&pageSize=5&apiKey=" + this.newsKey;
         Map newsResponse = (Map)this.rest.getForObject(newsUrl, Map.class, new Object[0]);
         List articles = (List)newsResponse.get("articles");
         List<Map<String, String>> newsList = new ArrayList();
         if (articles != null) {
            Iterator var34 = articles.iterator();

            while(var34.hasNext()) {
               Object aObj = var34.next();
               Map a = (Map)aObj;
               Map<String, String> m = new HashMap();
               m.put("title", (String)a.get("title"));
               m.put("url", (String)a.get("url"));
               m.put("publishedAt", (String)a.get("publishedAt"));
               newsList.add(m);
            }
         }

         resp.news = newsList;
      } catch (Exception var20) {
         resp.news = new ArrayList();
      }

      StringBuilder allText = new StringBuilder();
      Iterator var28 = resp.news.iterator();

      while(var28.hasNext()) {
         Map<String, String> n = (Map)var28.next();
         allText.append((String)n.get("title")).append(". ");
      }

      double sentiment = 0.0D;

      try {
         Map req = new HashMap();
         req.put("text", allText.toString());
         Map result = (Map)this.rest.postForObject(this.sentimentUrl, req, Map.class, new Object[0]);
         sentiment = result != null && result.get("sentiment") != null ? ((Number)result.get("sentiment")).doubleValue() : 0.0D;
      } catch (Exception var19) {
         sentiment = 0.0D;
      }

      resp.sentimentScore = sentiment;
      double base = 0.5D;
      double sentimentFactor = sentiment * 5.0D;
      double momentum = 0.0D;
      if (resp.currentPrice > 0.0D) {
         momentum = Math.min(5.0D, Math.max(-5.0D, resp.currentPrice % 10.0D - 5.0D));
      }

      double predicted1M = sentimentFactor + momentum * 0.2D;
      resp.predictedReturn1M = (double)Math.round(predicted1M * 10.0D) / 10.0D;
      if (resp.predictedReturn1M > 2.0D) {
         resp.outlook = "Bullish";
      } else if (resp.predictedReturn1M < -2.0D) {
         resp.outlook = "Bearish";
      } else {
         resp.outlook = "Neutral";
      }

      return resp;
   }
}
