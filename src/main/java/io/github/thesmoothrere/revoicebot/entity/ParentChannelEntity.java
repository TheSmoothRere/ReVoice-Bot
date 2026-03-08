package io.github.thesmoothrere.revoicebot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "parent_channels", indexes = {
        @Index(name = "idx_parent_channels_channel_id", columnList = "channelId", unique = true)
})
public class ParentChannelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    private Long channelId;

    private Long guildId;

    private String prefix;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    private Boolean deleted;

    @OneToMany(mappedBy = "parentChannel")
    private List<ChildChannelEntity> childChannels;

    @PrePersist
    public void prePersist() {
        deleted = false;
    }
}
