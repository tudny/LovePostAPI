package com.tudny.lovepost.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LovePostAPI {
    private static final String APPLICATION_NAME = "LovePost API Java Application";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = LovePostAPI.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private final Sheets service;

    public LovePostAPI() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<List<Object>> readData(String spreadsheetId, String range) throws IOException {
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        return response.getValues();
    }

    @SuppressWarnings("unused")
    public int writeData(String spreadsheetId, String range, List<List<Object>> value) throws IOException {
        ValueRange body = new ValueRange()
                .setValues(value);

        UpdateValuesResponse result =
                service.spreadsheets().values().update(spreadsheetId, range, body)
                        .setValueInputOption("USER_ENTERED")
                        .execute();

        return result.getUpdatedCells();
    }

    public int appendData(String spreadsheetId, String range, List<List<Object>> values) throws IOException {
        ValueRange body = new ValueRange()
                .setValues(values);

        AppendValuesResponse result =
                service.spreadsheets().values().append(spreadsheetId, range, body)
                        .setValueInputOption("USER_ENTERED")
                        .execute();

        return result.getUpdates().getUpdatedCells();
    }

    public String createSpreadsheet(String title) throws IOException {
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                .setTitle(title));

        spreadsheet = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();

        return spreadsheet.getSpreadsheetId();
    }

    private Integer getSheetId(BatchUpdateSpreadsheetResponse response) throws JsonProcessingException {
        JsonMapper mapper = new JsonMapper();
        JsonNode jsonTree = mapper.readTree(response.toString());
        return jsonTree.get("replies").get(0).get("addSheet").get("properties").get("sheetId").asInt();
    }

    public Integer addSheet(String spreadsheetId, String sheetName) throws IOException {
        List<Request> requests = new ArrayList<>();

        requests.add(new Request()
                .setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle(sheetName))));

        BatchUpdateSpreadsheetRequest body =
                new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response =
                service.spreadsheets().batchUpdate(spreadsheetId, body).execute();

        return getSheetId(response);
    }

    public void deleteSheet(String spreadsheetId, Integer sheetId) throws IOException {
        List<Request> requests = new ArrayList<>();

        requests.add(new Request().
                setDeleteSheet(new DeleteSheetRequest().setSheetId(sheetId)));

        BatchUpdateSpreadsheetRequest body =
                new BatchUpdateSpreadsheetRequest().setRequests(requests);
        @SuppressWarnings("unused")
        BatchUpdateSpreadsheetResponse response =
                service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
    }

    public ArrayList<Integer> getSheets(String spreadsheetID) throws IOException {
        Spreadsheet response = service.spreadsheets().get(spreadsheetID).execute();
        Object sheets = response.get("sheets");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonTree = mapper.readTree(sheets.toString());
        ArrayList<Integer> sheetsIds = new ArrayList<>();
        jsonTree.forEach(jsonNode -> sheetsIds.add(jsonNode.get("properties").get("sheetId").intValue()));

        return sheetsIds;
    }
}
