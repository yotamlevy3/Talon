package TalonAssignment;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class WireguardServer {
    static WireguardOrchestrationService orchestrationService = WireguardOrchestrationService.getInstance();

    public WireguardServer() {
        System.out.println("Your server is now up and running...");
    }

    public void startWebServer() {
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/wireguard/add", new AddHandler());
            server.createContext("/wireguard/remove", new DisconnectHandler());
            server.setExecutor(null); // use the default executor
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert server != null;
        server.start();
    }

    public static void startWebServerManual() {
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8088), 0);
            server.createContext("/wireguard-manual/add", new AddHandler());
            server.createContext("/wireguard-manual/remove", new DisconnectHandler());
            server.setExecutor(null); // use the default executor
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert server != null;
        server.start();
    }

    public static void main(String[] args) {
        //Start server for manual testing
        startWebServerManual();
    }

    static class AddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String requestBody = readRequestBody(exchange);
                PeerAssignmentResponse peerResponse = buildPeerResponse(requestBody.toString());

                // Create the response object
                JSONObject responseObject = new JSONObject();
                responseObject.put("tenantId", peerResponse.tenantId);
                responseObject.put("peerId", peerResponse.peerId);
                responseObject.put("overlayNetworkId", peerResponse.overlayNetworkId);
                responseObject.put("publicKey", peerResponse.publicKey);
                responseObject.put("virtualAddress", peerResponse.virtualAddress);
                responseObject.put("message", "welcome! peer-"+ peerResponse.peerId +" of tenantId-" + peerResponse.tenantId + ", this is your virtual address:" + peerResponse.virtualAddress);

                orchestrationService.assignNewPeer(peerResponse);
                // Set the response headers
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                // Convert the response object to bytes
                byte[] responseBytes = responseObject.toString().getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);

                // Write the response bytes to the output stream
                OutputStream responseBody = exchange.getResponseBody();
                responseBody.write(responseBytes);
                responseBody.close();
            }
        }
    }

    static class DisconnectHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestBody = readRequestBody(exchange);
            System.out.println("Delete Request Body: " + requestBody);

            String peerId = null;
            try {
                JSONObject json = new JSONObject(requestBody.toString());
                peerId = json.getString("peerId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            orchestrationService.unsubscribePeer(peerId);

            String response = "Peer delete request operated successfully";
            sendResponse(exchange, response);
        }
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        StringBuilder stringBuilder = new StringBuilder();
        int byteRead;
        while ((byteRead = requestBody.read()) != -1) {
            stringBuilder.append((char) byteRead);
        }
        requestBody.close();
        return stringBuilder.toString();
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    private static PeerAssignmentResponse buildPeerResponse(String requestBody) {
        // Parse the JSON body
        String tenantId = null;
        String peerId = null;
        String overlayNetworkId = null;
        String publicKey = null;
        try {
            JSONObject json = new JSONObject(requestBody);
            tenantId = json.getString("tenantId");
            peerId = json.getString("peerId");
            overlayNetworkId = json.getString("overlayNetworkId");
            publicKey = json.getString("publicKey");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new PeerAssignmentResponse(tenantId, peerId, overlayNetworkId, publicKey, UUID.randomUUID().toString());
    }
}
