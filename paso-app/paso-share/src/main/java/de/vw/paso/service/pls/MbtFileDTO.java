package de.vw.paso.service.pls;

import java.time.Instant;

public record MbtFileDTO(String fileName, byte[] data, Instant date) {
}
