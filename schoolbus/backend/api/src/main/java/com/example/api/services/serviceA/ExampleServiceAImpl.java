package com.example.api.services.serviceA;

import com.example.api.services.serviceA.dto.ExampleAInput;
import com.example.api.services.serviceA.dto.ExampleAOutput;
import com.example.shared.db.entities.Example;
import com.example.shared.db.repo.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExampleServiceAImpl implements ExampleServiceA{
    private final ExampleRepository exampleRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value(value = "${kafka.example-topic}")
    private String topic;

    @Override
    public Page<ExampleAOutput> getExamplePage(ExampleAInput input, Pageable pageable) {
        Page<Example> examplePage = exampleRepository.getExampleByDescription(
                input.getDescription(),
                pageable
        );
        return examplePage.map(ExampleAOutput::fromExample);
    }

    @Override
    public ExampleAOutput testKafka() {
        Example example = Example.builder()
                .id(1L)
                .description("test kafka")
                .build();
        kafkaTemplate.send(topic, example.getId().toString(), example);
        return ExampleAOutput.fromExample(example);
    }
}
