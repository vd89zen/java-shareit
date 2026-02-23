package ru.practicum.shareit.server.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.server.request.model.RequestItem;
import ru.practicum.shareit.server.user.model.User;

@Entity
@Table(name = "items")
@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "description", length = 1024, nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = true)
    @ToString.Exclude
    private RequestItem request;
}