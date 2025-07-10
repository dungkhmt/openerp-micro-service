package com.hust.wmsbackend.management.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.wmsbackend.WmsBackendApplication;
import com.hust.wmsbackend.management.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = WmsBackendApplication.class)
class RedisCacheServiceTest {

    @Autowired
    RedisCacheService redisCacheService;

    @Test
    public void setCachedValueWithExpireTest() {
        List<com.hust.wmsbackend.management.entity.Product> products = new ArrayList<>();
        com.hust.wmsbackend.management.entity.Product p1 = com.hust.wmsbackend.management.entity.Product.builder().productId(UUID.randomUUID()).name("San pham 1").build();
        com.hust.wmsbackend.management.entity.Product p2 = com.hust.wmsbackend.management.entity.Product.builder().productId(UUID.randomUUID()).name("San pham 2").build();
        products.add(p1);
        products.add(p2);

        redisCacheService.setCachedValueWithExpire("PRODUCTS", products, 60);

        List<Product> cachedProducts = redisCacheService.getCachedListObject("PRODUCTS", Product.class);
        assertEquals(products.size(), cachedProducts.size());
        System.out.println(String.format("Get Cached list object %s", cachedProducts));
    }

    @Test
    public void convertListTest() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Object object = "[{\"productId\":\"24730cbc-71dc-43be-8773-4bdfd8e00436\",\"code\":\"WA10T5260BY/SV\",\"name\":\"Máy giặt Samsung Inverter 10 kg\",\"description\":null,\"height\":107.50,\"weight\":41.00,\"area\":3600.00,\"uom\":\"Cái\",\"categoryId\":\"729aca08-ef0a-11ed-b27c-02420a000304\",\"imageContentType\":\"image/jpeg\",\"imageSize\":126065,\"imageData\":\"\"},{\"productId\":\"16eb3684-4477-4afc-9f7e-e9859705e703\",\"code\":\"AW-L805AV (SG)\",\"name\":\"Máy giặt Toshiba 7 Kg\",\"description\":null,\"height\":92.00,\"weight\":33.00,\"area\":2500.00,\"uom\":\"Cái\",\"categoryId\":\"729aca08-ef0a-11ed-b27c-02420a000304\",\"imageContentType\":\"image/jpeg\",\"imageSize\":176539,\"imageData\":\"\"}]";
        try {
            List<Product> products = objectMapper.readValue((String)object, new TypeReference<List<Product>>() { });
            System.out.println(products);
        } catch (Exception e ) {
            e.printStackTrace();
        }

    }

}