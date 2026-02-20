package ru.practicum.shareit.server.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.server.request.model.RequestItem;

public interface RequestItemRepository extends JpaRepository<RequestItem, Long> {

    @Query("SELECT r " +
            "FROM RequestItem r " +
            "WHERE r.requestor.id = :requestorId " +
            "ORDER BY r.created DESC")
    Page<RequestItem> findByRequestorId(@Param("requestorId") Long requestorId, Pageable pageable);

    @Query("SELECT r " +
            "FROM RequestItem r " +
            "WHERE r.requestor.id != :userId " +
            "ORDER BY r.created DESC")
    Page<RequestItem> findRequestItemByOtherUser(@Param("userId") Long userId, Pageable pageable);
}
