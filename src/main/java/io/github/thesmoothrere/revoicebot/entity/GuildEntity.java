package io.github.thesmoothrere.revoicebot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "guilds", indexes = {
        @Index(name = "idx_guild_guild_id", columnList = "guildId", unique = true)
})
public class GuildEntity extends BaseEntity {
    @NaturalId
    @Column(nullable = false)
    private Long guildId;

    @Column(nullable = false)
    private Boolean premium = false;

    private Instant premiumStartedAt;

    private Instant premiumExpiresAt;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParentChannelEntity> parentChannels = new ArrayList<>();
}
