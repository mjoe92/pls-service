package de.vw.paso.message.domain;

import java.util.Date;
import java.util.Objects;

import de.vw.paso.core.domain.AbstractEntity;
import de.vw.paso.message.UserMessageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity
@Table(name = UserMessage.TABLE_USER_MESSAGE)
public final class UserMessage extends AbstractEntity<Long> {

    public static final Integer MESSAGE_UNREAD = 0;
    public static final Integer MESSAGE_READ = 1;

    static final String TABLE_USER_MESSAGE = "USER_MESSAGE";

    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_MESSAGE = "MESSAGE";
    private static final String COLUMN_USER_ID = "USER_ID";
    private static final String COLUMN_READ = "IS_READ";
    private static final String COLUMN_CREATED = "CREATED";
    private static final String COLUMN_TYPE = "TYPE";
    private static final String COLUMN_CONFIG_ID = "VEHICLE_CONFIG_ID";

    @Id
    @Column(name = COLUMN_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = COLUMN_MESSAGE, columnDefinition = "longtext")
    private String message;

    @Column(name = COLUMN_USER_ID)
    private String userId;

    @Column(name = COLUMN_READ, nullable = false, columnDefinition = "int(1)")
    private int read = MESSAGE_UNREAD;

    @Column(name = COLUMN_CREATED, columnDefinition = "TIMESTAMP")
    private Date created;

    @Column(name = COLUMN_TYPE, columnDefinition = "varchar(100)")
    @Enumerated(EnumType.STRING)
    private UserMessageType type;

    @Column(name = COLUMN_CONFIG_ID)
    private Long vehicleConfigId;

    public Boolean isRead() {
        return Objects.equals(getRead(), MESSAGE_READ);
    }

    public static UserMessage createPartListUpdate(String userId, long vehicleConfigId, UserMessageType type) {
        UserMessage um = new UserMessage();
        um.setUserId(userId);
        um.setVehicleConfigId(vehicleConfigId);
        um.setCreated(new Date());
        um.setMessage("Part list status changed to " + type.name());
        um.setType(type);
        return um;
    }

    public static UserMessage createPartListInCreationMessage(String userId, long vehicleConfigId) {
        return createPartListUpdate(userId, vehicleConfigId, UserMessageType.PART_LIST_IN_CREATION);
    }

    public static UserMessage createPartListCreatedMessage(String userId, long vehicleConfigId) {
        return createPartListUpdate(userId, vehicleConfigId, UserMessageType.PART_LIST_CREATED);
    }

    public static UserMessage createPartListErrorMessage(String userId, long vehicleConfigId) {
        return createPartListUpdate(userId, vehicleConfigId, UserMessageType.PART_LIST_ERROR);
    }
}
