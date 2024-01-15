package sql.info.models;

public class Recommendation {
    private int id;
    private Peer peer;
    private Peer recommendedPeer;

    public Recommendation() {}

    public Recommendation(int id, Peer peer, Peer recommendedPeer) {
        this.id = id;
        this.peer = peer;
        this.recommendedPeer = recommendedPeer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Peer getPeer() {
        return peer;
    }

    public void setPeer(Peer peer) {
        this.peer = peer;
    }

    public Peer getRecommendedPeer() {
        return recommendedPeer;
    }

    public void setRecommendedPeer(Peer recommendedPeer) {
        this.recommendedPeer = recommendedPeer;
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "id=" + id +
                ", peer=" + peer.toString() +
                ", recommendedPeer=" + recommendedPeer.toString() +
                '}';
    }
}
