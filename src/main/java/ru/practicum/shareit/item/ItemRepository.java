package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByOwnerId(Integer ownerId);

    @Query("select it " +
            "from Item as it " +
            "where (lower(it.name) like lower(concat('%', :text, '%')) or lower(it.description) like lower(concat('%', :text, '%'))) and it.available = true")
    List<Item> findAllByAvailableIsTrueAndNameAndDescriptionContainingIgnoreCase(String text);
}
