package TalonAssignment;


import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import java.util.UUID;

public class Peer {
    private String peerId;
    private String tenantId;
    private String publicKey;
    private String privateKey;
    private String virtualIPv4;
    // Create connection and sync commands for subscribing
    StatefulRedisPubSubConnection<String, String> subscriptionConnection;
    RedisPubSubCommands<String, String> subscriptionCommands;
    // Create connection and sync commands for publishing
    StatefulRedisPubSubConnection<String, String> pubSubConnection;
    RedisPubSubCommands<String, String> pubSubCommands;

    public Peer() {
        privateKey = UUID.randomUUID().toString();
    }

    public void setSubscriptionConnection(StatefulRedisPubSubConnection<String, String> pubSubClient) {
        this.subscriptionConnection = pubSubClient;
    }

    public void setSubscriptionCommands() {
        if (subscriptionConnection != null) subscriptionCommands = subscriptionConnection.sync();
    }

    public void setPubSubConnection(StatefulRedisPubSubConnection<String, String> pubSubConnection) {
        this.pubSubConnection = pubSubConnection;
    }

    public void setPubSubCommands() {
        if (pubSubConnection != null) this.pubSubCommands = pubSubConnection.sync();
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getVirtualIPv4() {
        return virtualIPv4;
    }

    public void setVirtualIPv4(String virtualIPv4) {
        this.virtualIPv4 = virtualIPv4;
    }

}
