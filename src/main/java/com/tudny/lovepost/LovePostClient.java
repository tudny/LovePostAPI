package com.tudny.lovepost;

import com.github.tudny.tudlogger.Log;
import com.tudny.lovepost.api.LovePostAPI;
import com.tudny.lovepost.data.Message;
import com.tudny.lovepost.data.MessageJson;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LovePostClient {

    public final static String DATE_TIME_FORMATTER = "yyyy/MM/dd HH:mm:ss";
    private final static String SPREADSHEET_DEFAULT_NAME = "Love Post Default Testing Spreadsheet";
    private final static String SPREADSHEET_DEFAULT_SHEET = "LoveSheet";
    private final static String SPREADSHEET_DEFAULT_RANGE = SPREADSHEET_DEFAULT_SHEET + "!A1:C";
    private final static List<Object> SPREADSHEET_DEFAULT_HEADER = Arrays.asList("ID", "DATE", "MESSAGE");
    private final static String FORMULA_FOR_AUTO_INCREMENT =
            "= IFERROR( INDIRECT ( ADDRESS ( ROW() - 1, COLUMN() ) ) + 1, 1)";

    final LovePostAPI lovePostAPI;
    final String spreadsheetID;

    public LovePostClient() throws GeneralSecurityException, IOException {

        Log.d("Creating new LovePostClient and Spreadsheet.");

        this.lovePostAPI = new LovePostAPI();
        this.spreadsheetID = lovePostAPI.createSpreadsheet(SPREADSHEET_DEFAULT_NAME);

        List<Integer> sheetsIds = lovePostAPI.getSheets(spreadsheetID);
        Integer newSheetId = lovePostAPI.addSheet(spreadsheetID, SPREADSHEET_DEFAULT_SHEET);
        for(Integer id : sheetsIds){ lovePostAPI.deleteSheet(spreadsheetID, id); }
        lovePostAPI.appendData(spreadsheetID,
                SPREADSHEET_DEFAULT_RANGE,
                Collections.singletonList(SPREADSHEET_DEFAULT_HEADER));

        Log.d("Created new LovePostClient and Spreadsheet.");
    }

    public LovePostClient(String spreadsheetID) throws GeneralSecurityException, IOException {
        Log.d("Creating new LovePostClient with existing Spreadsheet.");
        this.lovePostAPI = new LovePostAPI();
        this.spreadsheetID = spreadsheetID;
        Log.d("Created new LovePostClient with existing Spreadsheet.");
    }

    private String getActualTimeAsString() {
        final LocalDateTime localDateTime = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        return formatter.format(localDateTime);
    }

    public boolean addNewMessage(String message) throws IOException {
        final String date = getActualTimeAsString();

        List<List<Object>> data = Collections.singletonList(
                Arrays.asList(
                        FORMULA_FOR_AUTO_INCREMENT, date, message
                )
        );

        Log.df("Adding message\"%s\" to spreadsheet \"%s\"", message, spreadsheetID);

        int appended = lovePostAPI.appendData(spreadsheetID, SPREADSHEET_DEFAULT_RANGE, data);
        return appended != 0;
    }

    public List<List<Object>> readAllMessagesList() throws IOException {
        return lovePostAPI.readData(spreadsheetID, SPREADSHEET_DEFAULT_RANGE);
    }

    public List<Message> readAllMessages() throws IOException {
        List<List<Object>> messagesJson = readAllMessagesList();
        List<Message> messages = new ArrayList<>();
        messagesJson.forEach(object -> {
            try {
                MessageJson messageJson =
                        new MessageJson(Integer.parseInt(object.get(0).toString()),
                                object.get(1).toString(), object.get(2).toString());
                Message message = Message.convert(messageJson);
                messages.add(message);
            } catch (Exception ignored) {}
        });
        return messages;
    }
}
