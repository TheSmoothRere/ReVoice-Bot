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
@Table(name = "guilds", indexes = {
        @Index(name = "idx_guild_guildid", columnList = "guildId")
})
public class GuildEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(nullable = false)
    private Long guildId;

    @Column(nullable = false)
    private Boolean premium;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    private Boolean deleted;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParentChannelEntity> parentChannels = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        deleted = false;
        premium = false;
    }
}
