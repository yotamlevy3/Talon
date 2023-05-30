import TalonAssignment.Peer;
import TalonAssignment.Tenant;
import TalonAssignment.WireguardOrchestrationService;
import TalonAssignment.WireguardServer;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

//you can test manually with postman like this: POST: http://localhost:8080/wireguard-manual/add  requestBody: {tenantId, peerID, overlayNetworkId, publicKey}
//you can also test removal manually with postman like this: DELETE: http://localhost:8080/wireguard-manual/remove  requestBody: {tenantId, peerID}
public class ITest {

    @Test
    public void OrchestrationServiceTest() throws IOException {
        WireguardServer wireguardServer = new WireguardServer();
        wireguardServer.startWebServer();
        WireguardOrchestrationService service = WireguardOrchestrationService.getInstance();

        Tenant tenant = new Tenant();
        tenant.setTenantId("tenant1");
        tenant.setOverlayNetworkId("overlayNetwork1");

        Peer peer1 = new Peer();
        peer1.setPeerId("peer1");
        peer1.setTenantId("tenant1");
        peer1.setPublicKey("publicKey1");

        //tenant asks server's API through an HTTP request to assign their peer
        tenant.sendPostRequestToAssignNewPeer(peer1.getPeerId(), peer1.getPublicKey());
        waitForServerToHandleRequest();
        assertEquals(1, service.getTenants().size());
        assertEquals(1, service.getPeers().size());

        Peer peer2 = new Peer();
        peer2.setPeerId("peer2");
        peer2.setTenantId("tenant1");
        peer2.setPublicKey("publicKey2");
        tenant.sendPostRequestToAssignNewPeer(peer2.getPeerId(), peer2.getPublicKey());
        waitForServerToHandleRequest();
        assertEquals(1, service.getTenants().size());
        assertEquals(2 ,service.getPeers().size());

        //multi-tenancy - another tenant asks server's API through an HTTP request to assign their peer
        Tenant tenant2 = new Tenant();
        tenant2.setTenantId("tenant2");
        tenant2.setOverlayNetworkId("overlayNetwork2");

        Peer peer3 = new Peer();
        peer3.setPeerId("peer13");
        peer3.setTenantId("tenant2");
        peer3.setPublicKey("publicKey3");
        tenant2.sendPostRequestToAssignNewPeer(peer3.getPeerId(), peer3.getPublicKey());
        waitForServerToHandleRequest();
        assertEquals(2, service.getTenants().size());
        assertEquals(3 ,service.getPeers().size());
        assertEquals(3, service.getPeersPublicKeys().size());

        tenant.sendDisconnectAPeer("peer1"); //of tenant1
        waitForServerToHandleRequest();
        assertEquals(2, service.getTenants().size()); //tenant1 still has another peer in the orchestrator
        assertEquals(2 ,service.getPeers().size());
        assertEquals(2, service.getPeersPublicKeys().size());

        service.tearDownPubSubConnection();
    }

    private void waitForServerToHandleRequest() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
