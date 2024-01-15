package sql.info.models;

public class Friend {
    private int id;
    private Peer peer1;
    private Peer peer2;

    public Friend() {}

    public Friend(int id, Peer peer1, Peer peer2) {
        this.id = id;
        this.peer1 = peer1;
        this.peer2 = peer2;
    }

    public Peer getPeer1() {
        return peer1;
    }

    public void setPeer1(Peer peer1) {
        this.peer1 = peer1;
    }

    public Peer getPeer2() {
        return peer2;
    }

    public void setPeer2(Peer peer2) {
        this.peer2 = peer2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "id=" + id +
                ", peer1=" + peer1.toString() +
                ", peer2=" + peer2.toString() +
                '}';
    }
}
