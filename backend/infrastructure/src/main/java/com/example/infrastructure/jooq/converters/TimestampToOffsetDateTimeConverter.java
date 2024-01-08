package com.example.infrastructure.jooq.converters;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TimestampToOffsetDateTimeConverter implements Converter<LocalDateTime, OffsetDateTime> {
    @Override
    public OffsetDateTime from(LocalDateTime databaseObject) {
        return databaseObject.atOffset(ZoneOffset.ofHours(9));
    }

    @Override
    public LocalDateTime to(OffsetDateTime userObject) {
        return userObject.toLocalDateTime();
    }

    @Override
    public @NotNull Class<LocalDateTime> fromType() {
        return LocalDateTime.class;
    }

    @Override
    public @NotNull Class<OffsetDateTime> toType() {
        return OffsetDateTime.class;
    }
}
