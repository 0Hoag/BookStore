package com.example.identityservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class SelectedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String selectedId;
    int quantity;
    String bookId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    User user;

//    @ManyToOne
//    @JoinColumn(name = "order_id")
////    @JsonBackReference
//    Orders orders;
}