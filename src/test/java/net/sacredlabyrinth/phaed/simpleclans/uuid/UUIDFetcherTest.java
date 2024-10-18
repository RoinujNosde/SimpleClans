package net.sacredlabyrinth.phaed.simpleclans.uuid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UUIDFetcherTest {

    private UUID ghostUUID;

    @BeforeEach
    public void setup() {
        ghostUUID = UUID.fromString("bfe9a44f-e44d-453b-8219-74eb186c3932");
    }

    @Test
    public void getUUIDOf() throws Exception {
        assertEquals(ghostUUID, UUIDFetcher.getUUIDOf("GhostTheWolf"));
    }
}