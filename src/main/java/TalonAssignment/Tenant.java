package TalonAssignment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Tenant {

    private String tenantId;
    private String overlayNetworkId;

    public Tenant() {};

    public Tenant(String tenantId, String overlayNetworkId) {
        this.tenantId = tenantId;
        this.overlayNetworkId = overlayNetworkId;
    }

    public String getOverlayNetworkId() {
        return overlayNetworkId;
    }

    public void setOverlayNetworkId(String overlayNetworkId) {
        this.overlayNetworkId = overlayNetworkId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void sendPostRequestToAssignNewPeer(String peerId, String peerPublicKey) throws IOException, JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("tenantId", tenantId);
        requestBody.put("peerId", peerId);
        requestBody.put("overlayNetworkId", overlayNetworkId);
        requestBody.put("publicKey", peerPublicKey);

        // Open a connection to the API endpoint
        URL url = new URL("http://localhost:8080/wireguard/add");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Write the request body to the connection's output stream
        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] requestBodyBytes = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            outputStream.write(requestBodyBytes);
        }

        // Read the response from the connection's input stream
        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
        }
        connection.disconnect();
    }

    public void sendPostDisconnectAPeer(String peerId) throws IOException, JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("tenantId", tenantId);
        requestBody.put("peerId", peerId);

        // Open a connection to the API endpoint
        URL url = new URL("http://localhost:8080/wireguard/remove");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Write the request body to the connection's output stream
        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] requestBodyBytes = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            outputStream.write(requestBodyBytes);
        }

        // Read the response from the connection's input stream
        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
        }
        connection.disconnect();
    }

}