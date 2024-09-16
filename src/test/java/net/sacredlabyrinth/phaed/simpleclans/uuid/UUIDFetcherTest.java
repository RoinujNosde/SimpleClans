package net.sacredlabyrinth.phaed.simpleclans.uuid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
        try {
            assertEquals(ghostUUID, UUIDFetcher.getUUIDOf("GhostTheWolf"));
        }
        catch (IOException e) {
            if (e.getMessage().startsWith("Server returned HTTP response code: 403 for URL")) {
                System.out.println(
                        "You have been throttled by Mojang's API. Please wait a few minutes before trying again.");
            }
            else {
                throw e;
            }
        }
    }
    
    @Test
    public void getUUIDOfThrottled() throws IOException, InterruptedException {
        try {
            assertEquals(ghostUUID, UUIDFetcher.getUUIDOfThrottled("GhostTheWolf"));
        }
        catch (IOException e) {
            if (e.getMessage().startsWith("Server returned HTTP response code: 403 for URL")) {
                System.out.println(
                        "You have been throttled by Mojang's API. Please wait a few minutes before trying again.");
            }
            else {
                throw e;
            }
        }
    }
}