package com.github.kirekq.pricebot.parse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ParseWildberries {
    private static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36";

    public Map<String, String> parse(String Article) throws IOException, InterruptedException {
        Map<String, String> fullData = new HashMap<>();
        String url = "https://card.wb.ru/cards/v4/detail"
                + "?appType=1&curr=rub&dest=1259570991&spp=30"
                + "&hide_vflags=4294967296&mtype=257&lang=ru&ab_testing=false"
                + "&nm=" + Article;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "*/*")
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.out.println("Ошибка запроса, статус: " + response.statusCode());
            return fullData;
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());
        JsonNode product = root.path("products").get(0);

        if (product == null) {
            System.out.println("Товар не найден по артикулу " + Article);
            return fullData;
        }

        String valueName = product.path("name").asText();
        String brand = product.path("brand").asText();
        valueName += " " + brand;
        JsonNode price = product.path("sizes").get(0).path("price");

        if (price.isMissingNode()) {
            System.out.println(valueName + " — нет в наличии");
        } else {
            long valuePriceLong = price.path("product").asLong();
            String valuePrice = Long.toString(valuePriceLong / 100);

            fullData.put("Name", valueName);
            fullData.put("Price", valuePrice);
            System.out.println("Выполнена операция по артикулу " + Article);
        }
        return fullData;
    }
}
