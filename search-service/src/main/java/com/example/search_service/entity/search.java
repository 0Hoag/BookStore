package com.example.search_service.entity;

import jakarta.persistence.Entity;

import org.springframework.data.neo4j.core.schema.Id;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@Setter
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Entity
public class search {
    @Id
    String searchId;
}
