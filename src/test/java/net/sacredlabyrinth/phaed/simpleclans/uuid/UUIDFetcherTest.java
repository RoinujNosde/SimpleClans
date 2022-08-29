package net.sacredlabyrinth.phaed.simpleclans.uuid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

public class UUIDFetcherTest {

    private UUID ghostUUID;

    @Before
    public void setup() {
        ghostUUID = UUID.fromString("bfe9a44f-e44d-453b-8219-74eb186c3932");
    }

    @Test
    public void getUUIDOf() throws Exception {
        Assert.assertEquals(ghostUUID, UUIDFetcher.getUUIDOf("GhostTheWolf"));
    }

    @Test
    public void getUUIDOfThrottled() throws IOException, InterruptedException {
        Assert.assertEquals(ghostUUID, UUIDFetcher.getUUIDOfThrottled("GhostTheWolf"));
    }
}