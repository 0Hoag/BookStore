package com.example.friend_service.entity;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@CompoundIndex(name = "userId_blockedUserId_index", def = "{'userId': 1, 'blockedUserId': 1}", unique = true) // 1 user allowed block 1 user one more
@Document(collection = "blockList")
public class BlockList {
    @Id
    @Column(name = "block_id")
    String blockId;

    @Column(name = "user_id")
    String userId;

    @Column(name = "block_user_id")
    String blockedUserId;

    String reason;
}
