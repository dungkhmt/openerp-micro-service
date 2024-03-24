package com.example.consumer.listener;

import com.example.consumer.listener.dto.ExampleEvent;
import com.example.shared.db.entities.Example;
import com.example.shared.db.repo.ExampleRepository;
import com.example.shared.utils.JsonMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ExampleListener {
    private final ExampleRepository exampleRepository;

    @KafkaListener(topics = "${kafka.example-topic}", groupId = "example")
    public void listenExampleTopic(String eventString) {
        try {
            ExampleEvent event = JsonMapper.convertJsonToObject(eventString, ExampleEvent.class);
            if (event == null) {
                log.error("error parse event CustomerEvent, raw string: {}", eventString);
                return;
            }

            Example example = Example.builder()
                .id(event.getId())
                .description(event.getDescription())
                .createdAt(event.getCreatedAt())
                .createdBy(event.getCreatedBy())
                .build();
            exampleRepository.save(example);
            log.info("save success event with id {}", example.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}