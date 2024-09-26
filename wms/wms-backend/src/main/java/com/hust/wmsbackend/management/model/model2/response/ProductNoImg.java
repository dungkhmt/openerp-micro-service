package com.hust.wmsbackend.management.model.model2.response;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductNoImg {
        private UUID productId;
        private String name;
        private String code;
        private UUID categoryId;
}
