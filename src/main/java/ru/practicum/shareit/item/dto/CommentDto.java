package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {

    @Positive
    private Integer id;

    private String text;

    private String authorName;

    private LocalDateTime created;
}
