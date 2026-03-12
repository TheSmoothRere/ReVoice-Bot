package io.github.thesmoothrere.revoicebot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Getter
@Setter
@Entity
@Table(name = "child_channels", indexes = {
        @Index(name = "idx_child_channels_channel_id", columnList = "channelId", unique = true),
        @Index(name = "idx_child_channel", columnList = "parent_channel_id, count, deleted")
})
public class ChildChannelEntity extends BaseEntity {
    @NaturalId
    private Long channelId;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private Integer count;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_channel_id")
    private ParentChannelEntity parentChannel;
}
