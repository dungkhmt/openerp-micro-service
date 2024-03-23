package com.example.api.services.serviceA;

import com.example.api.services.serviceA.dto.ExampleAInput;
import com.example.api.services.serviceA.dto.ExampleAOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExampleServiceA {
    Page<ExampleAOutput> getExamplePage(ExampleAInput input, Pageable pageable);

    ExampleAOutput testKafka();
}
