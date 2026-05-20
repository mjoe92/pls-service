package de.vw.paso.client.stueckliste.compare;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VisibleColumnsCompareChangeEvent {

    private List<String> columns;
    private Class<?> senderClass;

}
