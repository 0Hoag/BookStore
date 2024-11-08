package com.example.messaging_service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.messaging_service.entity.Message;

@Repository
public interface MessagingRepository extends MongoRepository<Message, String> {
    // fetch all messenger in conversation, sort by real time
    List<Message> findByConversationIdOrderByTimestampDesc(String conversationId);

    Page<Message> findByConversationId(String senderId, Pageable pageable);

    // fetch by messenger last conversation
    Message findFirstByConversationIdOrderByTimestampDesc(String conversationId);

    // fetch all messenger with have many conversation
    // Phương thức 1: Sử dụng @Query
    @Query(value = "{ 'conversationId': { $in: ?0 } }", sort = "{ 'timestamp': -1 }")
    List<Message> findLastMessagesByConversationIds(List<String> conversationIds);

    // Phương thức 2: Sử dụng @Aggregation
    @Aggregation(
            pipeline = {
                "{ $match: { 'conversationId': { $in: ?0 } } }",
                "{ $sort: { 'conversationId': 1, 'timestamp': -1 } }", // Thay đổi từ 1 thành -1
                "{ $group: { '_id': '$conversationId', 'lastMessage': { $first: '$$ROOT' } } }",
                "{ $replaceRoot: { 'newRoot': '$lastMessage' } }"
            })
    List<Message> findLastMessagesByConversationIdsAggregation(List<String> conversationIds);

    // Phương thức 3: Sử dụng phương thức có tên tùy chỉnh
    List<Message> findFirstByConversationIdInOrderByTimestampDesc(List<String> conversationIds);
}
