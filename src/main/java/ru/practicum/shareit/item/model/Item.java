package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items", schema = "public")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @Column(name = "owner_id")
    private int ownerId;

    @NotNull
    private Boolean available;

    @OneToMany(mappedBy = "item")
    private Set<Booking> bookings;

    @OneToMany(mappedBy = "item")
    private Set<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "item_request_id")
    private ItemRequest request;

    public Item(Integer id, String name, String description, int ownerId, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.available = available;
    }

    public Item(Integer id, String name, String description, int ownerId, Boolean available, ItemRequest itemRequest) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.available = available;
        this.request = itemRequest;
    }

    public Item(Integer id, String name, String description, int ownerId, Boolean available, Set<Comment> comments, ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.available = available;
        this.comments = comments;
        this.request = request;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
