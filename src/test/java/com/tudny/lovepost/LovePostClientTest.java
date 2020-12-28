package com.tudny.lovepost;

import com.tudny.lovepost.data.Message;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LovePostClientTest {

    private static final String SPREADSHEET_ID = "1yEAHqV_57_rTauYGIP9vUlRxUGj8j6ElCHQRVg_1slc";

    @Test
    void addNewMessage() throws GeneralSecurityException, IOException {
        final LovePostClient lovePostClient = new LovePostClient(SPREADSHEET_ID);

        // use windows-1250 encoding for proper Sheets display
        boolean wasUpdated = lovePostClient.addNewMessage("Love you so freaking much!");

        assertTrue(wasUpdated);
    }

    @Test
    void createNewSpreadsheet() throws GeneralSecurityException, IOException {
        final LovePostClient lovePostClient = new LovePostClient();

        boolean wasUpdated = lovePostClient.addNewMessage("Love you so much!");

        assertTrue(wasUpdated);
    }

    @Test
    void readAllData() throws GeneralSecurityException, IOException {
        final LovePostClient lovePostClient = new LovePostClient(SPREADSHEET_ID);

        List<List<Object>> data = lovePostClient.readAllMessagesList();
        assertNotNull(data);
        assertFalse(data.isEmpty());

        List<Message> data2 = lovePostClient.readAllMessages();
        assertNotNull(data2);
        assertFalse(data2.isEmpty());

        System.out.println(data2);
    }
}