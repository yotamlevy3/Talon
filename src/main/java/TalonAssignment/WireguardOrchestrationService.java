package TalonAssignment;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import java.util.HashMap;
import java.util.Map;


public class WireguardOrchestrationService {
    // Create Redis client - you must have redis installed on your machine to run it successfully
    RedisClient client = RedisClient.create("redis://localhost:6379");
    final static String TALON_CHANNEL = "channel";
    // Create connection and sync commands for publishing
    StatefulRedisPubSubConnection<String, String> pubSubConnection = client.connectPubSub();
    RedisPubSubCommands<String, String> pubSubCommands = pubSubConnection.sync();
    // Create connection and sync commands for subscribing
    StatefulRedisPubSubConnection<String, String> subscriptionConnection = client.connectPubSub();
    RedisPubSubCommands<String, String> subscriptionCommands = subscriptionConnection.sync();

    private Map<String, Tenant> tenants;
    private Map<String, Peer> peers;
    private Map<String, String> peersPublicKeys;

    private static WireguardOrchestrationService OrchestrationServiceInstance;

    //Singleton
    public static synchronized WireguardOrchestrationService getInstance() {
        if (OrchestrationServiceInstance == null) {
            OrchestrationServiceInstance = new WireguardOrchestrationService();
        }
        return OrchestrationServiceInstance;
    }

    // Private constructor to prevent direct instantiation as part of singleton pattern
    private WireguardOrchestrationService() {
        this.tenants = new HashMap<>();
        this.peers = new HashMap<>();
        this.peersPublicKeys = new HashMap<>();
        subScribeToChannel();
    }

    public void subScribeToChannel() {
        subscriptionCommands.subscribe(TALON_CHANNEL);
        subscriptionConnection.addListener(new RedisPubSubAdapter<String, String>() {
            @Override
            public void message(String channel, String message) {
                System.out.println("Received message: " + message);
                // Check if the message contains a new subscriber notification
                if (message.startsWith("Subscriber joined: ")) {
                    String newSubscriber = message.substring("Subscriber joined: ".length());
                    // Notify All about the new subscriber
                    pubSubCommands.publish("channel", "Hey *ALL peers* that are subscribed to me: there's a New subscriber: " + newSubscriber);
                }
            }
        });
    }

    public void notifyJoinToAllPeerSubscribers(String message, String publicKey) {
        pubSubCommands.publish(TALON_CHANNEL, "Subscriber joined: " + message + " and this that's his publicKey="+publicKey);
    }

    public void assignNewPeer(PeerAssignmentResponse response) {
        if (!peers.containsKey(response.peerId)) {
            addPeerToMapAndChannel(response);
            System.out.println("Service assigned a new peer with peerID="+ response.peerId);
            peersPublicKeys.put(response.peerId, response.publicKey);
            notifyJoinToAllPeerSubscribers("Service assigned a new peer:"+ response.peerId, response.publicKey);
            assignTenant(response);
        }
    }

    private void assignTenant(PeerAssignmentResponse response) {
        if (!tenants.containsKey(response.tenantId)) {
            Tenant tenant = new Tenant(response.tenantId, response.overlayNetworkId);
            tenants.put(tenant.getTenantId(), tenant);
            System.out.println("Service assigned a new tenant:"+ response.tenantId);
        }
    }

    private void addPeerToMapAndChannel(PeerAssignmentResponse response) {
        Peer peer = new Peer();
        peer.setPublicKey(response.publicKey);
        peer.setPeerId(response.peerId);
        peer.setVirtualIPv4(response.virtualAddress);
        peer.setTenantId(response.tenantId);
        peers.put(response.peerId, peer);
        peer.setSubscriptionConnection(client.connectPubSub());
        peer.setSubscriptionCommands();
        peer.setPubSubConnection(client.connectPubSub());
        peer.setPubSubCommands();
        peer.pubSubCommands.subscribe(TALON_CHANNEL, "Peer with peerId="+peer.getPeerId() +" has enlisted");
    }

    public void unsubscribePeer(String peerId) {
        notifyDisconnectToAllPeerSubscribers(peerId);
        Peer peerToUnsubscribe = peers.get(peerId);
        peerToUnsubscribe.subscriptionCommands.unsubscribe(TALON_CHANNEL);
        peers.remove(peerId);
        peersPublicKeys.remove(peerId);
    }

    public void notifyDisconnectToAllPeerSubscribers(String peerId) {
        pubSubCommands.publish(TALON_CHANNEL, "Hey *ALL peers* a peer with ID="+ peerId + " has disconnected.");
    }

    public void tearDownPubSubConnection() {
        //Close the connections and shutdown the client
        subscriptionConnection.close();
        pubSubConnection.close();
        client.shutdown();
    }

    public Map<String, Tenant> getTenants() {
        return tenants;
    }

    public Map<String, Peer> getPeers() {
        return peers;
    }

    public Map<String, String> getPeersPublicKeys() {
        return peersPublicKeys;
    }

}
