package de.vw.paso.service.message;

import java.util.Date;

import de.vw.paso.message.UserMessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserMessageDTO {

    private Long id;
    private String message;
    private String userId;
    private int read = 0;
    private Date created;
    private UserMessageType type;
    private Long vehicleConfigId;
}
