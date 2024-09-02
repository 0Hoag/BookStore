package com.example.post_service.service;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class B2StorageService {

    private final OkHttpClient client;
    private final String accountId;
    private final String applicationKey;
    private final String bucketId;

    private String apiUrl;
    private String downloadUrl;
    private String authorizationToken;

    public B2StorageService() {
        this.client = new OkHttpClient();
        this.accountId = "YOUR_ACCOUNT_ID";
        this.applicationKey = "YOUR_APPLICATION_KEY";
        this.bucketId = "YOUR_BUCKET_ID";

        try {
            authorizeAccount();
        } catch (IOException e) {
            throw new RuntimeException("Failed to authorize B2 account", e);
        }
    }

    public String authorizeAccount() throws IOException {
        String credentials = Base64.getEncoder().encodeToString((accountId + ":" + applicationKey).getBytes());

        Request request = new Request.Builder()
                .url("YOUR_AUTHORIZE_URL")
                .header("Authorization", "Basic " + credentials)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);

            this.apiUrl = json.getString("apiUrl");
            this.downloadUrl = json.getString("downloadUrl");
            this.authorizationToken = json.getString("authorizationToken");
        }

        return credentials;
    }

    public String getNativeDownloadUrl(String fileId) throws IOException {
        Request request = new Request.Builder()
                .url(apiUrl + "YOU_DOWNLOAD_URL")
                .header("Authorization", authorizationToken)
                .post(new FormBody.Builder()
                        .add("fileId", fileId)
                        .build())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.header("X-Bz-Download-Url");
        }
    }

    private String getUploadUrl() throws IOException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("bucketId", bucketId);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonBody.toString()
        );

        Request request = new Request.Builder()
                .url(apiUrl + "YOU_UPLOAD_URL")
                .header("Authorization", authorizationToken)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response Code: " + response.code());
            System.out.println("Response Message: " + response.message());

            // Print all headers
            for (String name : response.headers().names()) {
                System.out.println(name + ": " + response.header(name));
            }

            String responseBody = response.body().string();
            System.out.println("Response Body: " + responseBody);

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            JSONObject json = new JSONObject(responseBody);
            String uploadUrl = json.getString("uploadUrl");
            String uploadAuthToken = json.getString("authorizationToken");

            return uploadUrl + "|" + uploadAuthToken; // Trả về cả URL và token
        }
    }

    public String uploadFiles(MultipartFile file) throws IOException {
        // Step 1: Get upload URL and token
        String uploadUrlAndToken = getUploadUrl();
        String[] parts = uploadUrlAndToken.split("\\|");
        String uploadUrl = parts[0];
        String uploadAuthToken = parts[1];

        // Step 2: Upload file
        return uploadFile(uploadUrl, uploadAuthToken, file);
    }

    private String uploadFile(String uploadUrl, String uploadAuthToken, MultipartFile file) throws IOException {
        RequestBody requestBody = RequestBody.create(file.getBytes(), MediaType.parse(file.getContentType()));

        Request request = new Request.Builder()
                .url(uploadUrl)
                .header("Authorization", uploadAuthToken) // Sử dụng token mới
                .header("X-Bz-File-Name", file.getOriginalFilename())
                .header("Content-Type", file.getContentType())
                .header("X-Bz-Content-Sha1", "do_not_verify")
                .post(requestBody)
                .build();


        System.out.println("Upload URL: " + uploadUrl);
        System.out.println("Authorization Token: " + authorizationToken);
        System.out.println("File Name: " + file.getOriginalFilename());
        System.out.println("Content Type: " + file.getContentType());

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Upload Response Code: " + response.code());
            System.out.println("Upload Response Message: " + response.message());
            String responseBody = response.body().string();
            System.out.println("Upload Response Body: " + responseBody);

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(responseBody);
            return json.getString("fileId");
        }
    }
}