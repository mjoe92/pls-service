package de.vw.paso.service.userproperty;

import de.vw.paso.user.PropertyType;

public record SaveUserPropertyDTO(PropertyType type, String userData) {
}
