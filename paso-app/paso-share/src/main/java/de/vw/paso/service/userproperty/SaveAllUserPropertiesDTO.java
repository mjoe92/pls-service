package de.vw.paso.service.userproperty;

import java.util.List;

import de.vw.paso.user.PropertyType;

public record SaveAllUserPropertiesDTO(PropertyType type, List<String> userData) { }
