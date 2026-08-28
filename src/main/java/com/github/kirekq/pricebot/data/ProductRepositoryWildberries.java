package com.github.kirekq.pricebot.data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepositoryWildberries extends JpaRepository<ProductWildberries, Long> {
    long deleteByChatIdAndArticle(Long chatId, String article);
    boolean existsByChatIdAndArticle(Long chatId, String article);
    @Query(value = "SELECT * FROM productsWildberries WHERE chat_id = :chatId AND price_new > price", nativeQuery = true)
    List<ProductWildberries> findIncreasedPrice(@Param("chatId") Long chatId);

    @Query(value = "SELECT * FROM productsWildberries WHERE chat_id = :chatId AND price_new < price", nativeQuery = true)
    List<ProductWildberries> findDiscountedPrice(@Param("chatId") Long chatId);
}