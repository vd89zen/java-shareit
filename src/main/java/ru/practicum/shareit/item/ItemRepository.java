package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE CONCAT('%', :text, '%') " +
            "OR LOWER(i.description) LIKE CONCAT('%', :text, '%'))")
    List<Item> searchItems(@Param("text") String text);

    List<Item> findAllByOwnerId(Long ownerId);

}
