package io.github.thesmoothrere.revoicebot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "guilds", indexes = {
        @Index(name = "idx_guild_guildid", columnList = "guildId")
})
public class GuildEntity extends BaseEntity {
    @NaturalId
    @Column(nullable = false)
    private Long guildId;

    @Column(nullable = false)
    private Boolean premium;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParentChannelEntity> parentChannels = new ArrayList<>();

    @Override
    public void prePersist() {
        super.prePersist();
        premium = false;
    }
}
