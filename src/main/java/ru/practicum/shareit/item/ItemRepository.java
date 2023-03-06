package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    Page<Item> findAllByOwnerId(Integer ownerId, PageRequest pageRequest);

    @Query("select it " +
            "from Item as it " +
            "where (lower(it.name) like lower(concat('%', :text, '%')) " +
            "or lower(it.description) like lower(concat('%', :text, '%'))) and it.available = true")
    Page<Item> findAllByAvailableIsTrueAndNameAndDescriptionContainingIgnoreCase(String text, PageRequest pageRequest);

    @Query(value = "select * " +
            "from items as i " +
            "left join bookings as b on i.id = b.item_id " +
            "where i.id = ? and b.booker_id = ? and b.end_date < current_time", nativeQuery = true)
    List<Item> findItemsByIdAndBookerIdAndEndBeforeCurrent(Integer itemId, Integer bookerId, LocalDateTime localDateTime);
}
