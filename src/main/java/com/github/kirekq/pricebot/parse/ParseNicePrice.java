package com.github.kirekq.pricebot.parse;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ParseNicePrice {
    public Map<String, String> parse(String Article) throws IOException {
        Map<String, String> fullData = new HashMap<>();
        String URL = "https://sort.diginetica.net/search?st="+ Article + "&apiKey=2513IW97F8&fullData=true";
        String jsonText = Jsoup.connect(URL)
                .ignoreContentType(true)
                .execute()
                .body();
        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode = mapper.readTree(jsonText);
        int totalHits = jsonNode.at("/totalHits").asInt();
        if (isParse(totalHits)) {
            String valuePrice = jsonNode.at("/products/0/price").asText();
            String valueName = jsonNode.at("/products/0/name").asText();

            fullData.put("Name", valueName);
            fullData.put("Price", valuePrice);
            System.out.println("Выполнена операция по артикулу " + Article);
        } else {
            System.out.println("Операция прервана по артикулу " + Article);
        }
        return fullData;
    }
    private boolean isParse(int totalHits) {
        return totalHits == 1;
    }
}
