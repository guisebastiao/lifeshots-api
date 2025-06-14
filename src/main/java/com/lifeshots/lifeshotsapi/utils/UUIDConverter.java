package com.lifeshots.lifeshotsapi.utils;

import com.lifeshots.lifeshotsapi.exceptions.BadRequestException;

import java.util.UUID;

public class UUIDConverter {
    public static UUID toUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (Exception e) {
            throw new BadRequestException("O id está inválido");
        }
    }
}
