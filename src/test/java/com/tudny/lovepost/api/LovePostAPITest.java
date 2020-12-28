package com.tudny.lovepost.api;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LovePostAPITest {

    @Test
    void createAPI() throws GeneralSecurityException, IOException {
        final LovePostAPI lovePostAPI = new LovePostAPI();
        assertNotNull(lovePostAPI);
    }

    @Test
    void readData() throws GeneralSecurityException, IOException {
        final LovePostAPI lovePostAPI = new LovePostAPI();

        List<List<Object>> data =
                lovePostAPI.readData("1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms", "Class Data!A2:E");

        assertTrue(data != null && !data.isEmpty());
        assertEquals(30, data.size());
        assertEquals("Alexandra", data.get(0).get(0));
        assertEquals("English", data.get(0).get(4));
        assertEquals("Will", data.get(29).get(0));
        assertEquals("Math", data.get(29).get(4));
    }

    @Test
    void readDataNotAllowed() throws GeneralSecurityException, IOException {
        final LovePostAPI lovePostAPI = new LovePostAPI();

        assertThrows(com.google.api.client.googleapis.json.GoogleJsonResponseException.class,
                () -> lovePostAPI.readData("", ""));
    }

    @Test
    void readDataFromLoveSheet() throws GeneralSecurityException, IOException {
        final LovePostAPI lovePostAPI = new LovePostAPI();

        List<List<Object>> data =
                lovePostAPI.readData("1IQl0xVrQzrifaH0qiCFNcg4IUNKVVXHhWDr3XWziRcU", "LoveSheet!A1:C");

        assertTrue(data != null && !data.isEmpty());

        assertEquals("Id", data.get(0).get(0));
        assertEquals("Date", data.get(0).get(1));
        assertEquals("Message", data.get(0).get(2));

        assertEquals("0", data.get(1).get(0));
        assertEquals("13/12/2020 18:21:37", data.get(1).get(1));
        assertEquals("Hello, SpreadSheet", data.get(1).get(2));
    }

    @Test
    void createNewSpreadsheet() throws GeneralSecurityException, IOException {
        final LovePostAPI lovePostAPI = new LovePostAPI();

        String spreadsheetId = lovePostAPI.createSpreadsheet("Test Spreadsheet");
        assertNotEquals("", spreadsheetId);
    }

    @Test
    void appendData() throws GeneralSecurityException, IOException {
        final LovePostAPI lovePostAPI = new LovePostAPI();

        List<List<Object>> data = Collections.singletonList(
                Arrays.asList(
                        "2", "14/12/2020 19:19:19", "My second message"
                )
        );
        Integer updated =
                lovePostAPI.appendData("1IQl0xVrQzrifaH0qiCFNcg4IUNKVVXHhWDr3XWziRcU", "LoveSheet!A1:C", data);

        assertEquals(3, updated);
    }
}