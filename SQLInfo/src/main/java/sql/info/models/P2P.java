package sql.info.models;

import java.sql.Time;

public class P2P {
    int id;
    private Check check;
    private Peer checkingPeer;
    private CheckState state;
    private Time time;

    public P2P() {}

    public P2P(int id, Check check, Peer checkingPeer, CheckState state, Time time) {
        this.id = id;
        this.check = check;
        this.checkingPeer = checkingPeer;
        this.state = state;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Check getCheck() {
        return check;
    }

    public void setCheck(Check check) {
        this.check = check;
    }

    public Peer getCheckingPeer() {
        return checkingPeer;
    }

    public void setCheckingPeer(Peer checkingPeer) {
        this.checkingPeer = checkingPeer;
    }

    public CheckState getState() {
        return state;
    }

    public void setState(CheckState state) {
        this.state = state;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "P2P{" +
                "id=" + id +
                ", check=" + check.toString() +
                ", checkingPeer=" + checkingPeer.toString() +
                ", state=" + state.toString() +
                ", time=" + time.toString() +
                '}';
    }
}
