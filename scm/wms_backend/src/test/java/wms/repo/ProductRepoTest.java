package wms.repo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wms.entity.ProductEntity;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class ProductRepoTest {
    @Autowired
    private ProductRepo productRepoTest;
    @Test
    void getProductById() {


    }

    @Test
    void getProductByCode() {
        ProductEntity product = ProductEntity.builder()
                .code("PRO010")
                .brand("kdfjdj")
                .name("kdf")
                .status("active")
                .build();
        productRepoTest.save(product);
        ProductEntity product1 = productRepoTest.getProductByCode("PR0010");
        assertThat(product1).isEqualTo(product);
    }
}