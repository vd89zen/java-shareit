package ru.practicum.shareit.server.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.server.item.dto.ItemShortProjection;
import ru.practicum.shareit.server.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE CONCAT('%', :text, '%') " +
            "OR LOWER(i.description) LIKE CONCAT('%', :text, '%'))")
    List<Item> searchItems(@Param("text") String text);

    List<Item> findAllByOwnerId(Long ownerId);

    @Query("SELECT i.id AS id, i.name AS name, i.owner.id AS ownerId, i.request.id AS requestId " +
            "FROM Item i WHERE i.request.id IN :requestIds")
    List<ItemShortProjection> findItemsByRequestIds(@Param("requestIds") List<Long> requestIds);

    @Query("SELECT i.id AS id, i.name AS name, i.owner.id AS ownerId, i.request.id AS requestId " +
            "FROM Item i WHERE i.request.id = :requestId")
    List<ItemShortProjection> findItemsByRequestId(@Param("requestId") Long requestId);
}
