package sql.info.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.util.Date;

public class TransferredPoints {
    private int id;
    private Peer checkingPeer;
    private Peer checkedPeer;
    private Integer pointsAmount;

    public TransferredPoints() {}

    public TransferredPoints(int id, Peer checkingPeer, Peer checkedPeer, int pointsAmount) {
        this.id = id;
        this.checkingPeer = checkingPeer;
        this.checkedPeer = checkedPeer;
        this.pointsAmount = pointsAmount;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Peer getCheckingPeer() {
        return checkingPeer;
    }

    public void setCheckingPeer(Peer checkingPeer) {
        this.checkingPeer = checkingPeer;
    }

    public Peer getCheckedPeer() {
        return checkedPeer;
    }

    public void setCheckedPeer(Peer checkedPeer) {
        this.checkedPeer = checkedPeer;
    }

    public Integer getPointsAmount() {
        return pointsAmount;
    }

    public void setPointsAmount(Integer pointsAmount) {
        this.pointsAmount = pointsAmount;
    }

    @Override
    public String toString() {
        return "TransferredPoints{" +
                "id=" + id +
                ", checkingPeer=" + checkingPeer +
                ", checkedPeer=" + checkedPeer +
                ", pointsAmount=" + pointsAmount +
                '}';
    }

}
