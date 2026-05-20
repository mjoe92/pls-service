package de.vw.paso.pls.model.dto;

import java.time.Instant;

public record MbtFileDTO(String fileName, byte[] data, Instant date) {
}
