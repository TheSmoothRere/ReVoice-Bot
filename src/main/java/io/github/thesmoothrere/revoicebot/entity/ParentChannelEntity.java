package io.github.thesmoothrere.revoicebot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

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

    private String prefix;

    @OneToMany(mappedBy = "parentChannel")
    private ChildChannelEntity childChannels;
}
