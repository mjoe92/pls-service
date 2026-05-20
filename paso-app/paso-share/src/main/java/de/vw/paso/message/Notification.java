package de.vw.paso.message;

import java.io.Serializable;
import java.util.List;

import de.vw.paso.service.message.UserMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<UserMessageDTO> userMessages;
}
