package TalonAssignment;

public class PeerAssignmentResponse {
    String tenantId;
    String peerId;
    String overlayNetworkId;
    String publicKey;
    String virtualAddress;

    public PeerAssignmentResponse() {}

    public PeerAssignmentResponse(String tenantId, String peerId, String overlayNetworkId, String publicKey, String virtualAddress) {
        this.tenantId = tenantId;
        this.peerId = peerId;
        this.overlayNetworkId = overlayNetworkId;
        this.publicKey = publicKey;
        this.virtualAddress = virtualAddress;
    }

}