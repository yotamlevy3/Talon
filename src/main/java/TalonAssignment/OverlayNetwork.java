package TalonAssignment;

import java.util.List;

class OverlayNetwork {
    private String tenantId;
    private List<String> activePeers;

    public List<String> getActivePeers() {
        return activePeers;
    }

    public void setActivePeers(List<String> activePeers) {
        this.activePeers = activePeers;
    }
}