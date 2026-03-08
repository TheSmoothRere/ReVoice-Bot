package io.github.thesmoothrere.revoicebot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "child_channels", indexes = {
        @Index(name = "idx_child_channels_channel_id", columnList = "channelId", unique = true)
})
public class ChildChannelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    private Long channelId;

    private Long ownerId;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    private Boolean deleted;

    @ManyToOne
    @JoinColumn(name = "parent_channel_id")
    private ParentChannelEntity parentChannel;

    @PrePersist
    public void prePersist() {
        deleted = false;
    }
}
