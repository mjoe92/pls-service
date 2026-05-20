package de.vw.paso.service.userproperty;

import de.vw.paso.user.PropertyType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserPropertyDTO {
  private Long id;
  private String userId;
  private PropertyType type;
  private String userData;
}
