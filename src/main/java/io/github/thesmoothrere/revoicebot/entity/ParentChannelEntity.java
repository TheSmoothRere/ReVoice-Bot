package io.github.thesmoothrere.revoicebot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "parent_channels", indexes = {
        @Index(name = "idx_parent_channels_channel_id", columnList = "channelId", unique = true),
        @Index(name = "idx_parent_channel", columnList = "prefix, deleted")
})
public class ParentChannelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(nullable = false)
    private Long channelId;

    @Column(nullable = false)
    private String prefix;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    private Boolean deleted;

    @OneToMany(mappedBy = "parentChannel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChildChannelEntity> childChannels = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "guild_id")
    private GuildEntity guild;

    @PrePersist
    public void prePersist() {
        deleted = false;
    }
}
