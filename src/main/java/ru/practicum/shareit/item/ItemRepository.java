package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByOwnerId(Integer ownerId);

    @Query("select it " +
            "from Item as it " +
            "where (lower(it.name) like lower(concat('%', :text, '%')) " +
            "or lower(it.description) like lower(concat('%', :text, '%'))) and it.available = true")
    List<Item> findAllByAvailableIsTrueAndNameAndDescriptionContainingIgnoreCase(String text);

    @Query(value = "select * " +
            "from items as i " +
            "left join bookings as b on i.id = b.item_id " +
            "where i.id = ? and b.booker_id = ? and b.end_date < current_time", nativeQuery = true)
    List<Item> findItemsByIdAndBookerIdAndEndBeforeCurrent(Integer itemId, Integer bookerId, LocalDateTime localDateTime);
}
