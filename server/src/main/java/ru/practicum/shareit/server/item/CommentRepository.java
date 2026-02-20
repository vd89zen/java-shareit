package ru.practicum.shareit.server.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.server.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long itemId);

    @Query("SELECT c FROM Comment c " +
            "WHERE c.item.id IN :itemIds " +
            "ORDER BY c.created DESC")
    List<Comment> findAllByItemIds(@Param("itemIds") List<Long> itemIds);

}
