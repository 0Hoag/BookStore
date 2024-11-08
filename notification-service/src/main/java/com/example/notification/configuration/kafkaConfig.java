package com.example.notification.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.example.event.dto.NotificationEvent;
import com.example.notification.dto.response.MessagingResponse;
import com.example.notification.entity.CartNotification;
import com.example.notification.entity.SendFriend;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableKafka
public class kafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> getCommonProducerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return config;
    }

    private Map<String, Object> getCommonConsumerConfig(String groupId) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return config;
    }

    // cart-notification
    @Value("${spring.kafka.cart-notification.group-id}")
    private String cartGroupId;

    @Bean
    public ProducerFactory<String, CartNotification> cartProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getCommonProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, CartNotification> cartKafkaTemplate() {
        return new KafkaTemplate<>(cartProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, CartNotification> cartConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                getCommonConsumerConfig(cartGroupId),
                new StringDeserializer(),
                new JsonDeserializer<>(CartNotification.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CartNotification> cartKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CartNotification> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cartConsumerFactory());
        return factory;
    }

    // friend-notification
    @Value("${spring.kafka.friend-notification.group-id}")
    private String friendGroupId;

    @Bean
    public ProducerFactory<String, SendFriend> sendFriendProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getCommonProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, SendFriend> sendFriendKafkaTemplate() {
        return new KafkaTemplate<String, SendFriend>(sendFriendProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, SendFriend> sendFriendConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                getCommonConsumerConfig(friendGroupId),
                new StringDeserializer(),
                new JsonDeserializer<>(SendFriend.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SendFriend> sendFriendListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SendFriend> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sendFriendConsumerFactory());
        return factory;
    }

    // email-notification
    @Value("${spring.kafka.consumer.group-id}")
    private String emailGroup;

    @Bean
    public ProducerFactory<String, NotificationEvent> notificationEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getCommonProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, NotificationEvent> notificationEventKafkaTemplate() {
        return new KafkaTemplate<>(notificationEventProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, NotificationEvent> notificationEventKafkaConsumer() {
        return new DefaultKafkaConsumerFactory<>(
                getCommonConsumerConfig(emailGroup),
                new StringDeserializer(),
                new JsonDeserializer<>(NotificationEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationEvent>
            notificationEventConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(notificationEventKafkaConsumer());
        return factory;
    }

    @Value("${spring.kafka.messaging-notification.group-id}")
    private String messageGroup;

    @Bean
    public ProducerFactory<String, MessagingResponse> messageProducerFactory() {
        log.info("group-id: {}", messageGroup);
        return new DefaultKafkaProducerFactory<>(getCommonProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, MessagingResponse> messageKafkaTemplate() {
        return new KafkaTemplate<>(messageProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, MessagingResponse> messageConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                getCommonConsumerConfig(messageGroup),
                new StringDeserializer(),
                new JsonDeserializer<>(MessagingResponse.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessagingResponse> messageKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessagingResponse> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(messageConsumerFactory());
        return factory;
    }
}
