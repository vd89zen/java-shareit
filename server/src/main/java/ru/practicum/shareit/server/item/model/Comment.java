package ru.practicum.shareit.server.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", length = 1024, nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = true)
    @ToString.Exclude
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @ToString.Exclude
    private User author;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
