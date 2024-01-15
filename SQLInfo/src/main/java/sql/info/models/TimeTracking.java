package sql.info.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.util.Date;

public class TimeTracking {
    private int id;
    private Peer peer;
    @DateTimeFormat(pattern = "dd.MM.yy")
    private Date date;
    private Time time;
    private int state;


    public TimeTracking() {}
    public TimeTracking(int id, Peer peer, Date date, Time time, int state) {
        this.id = id;
        this.peer = peer;
        this.date = date;
        this.time = time;
        this.state = state;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }



    @Override
    public String toString() {
        return "TimeTracking{" +
                "id=" + id +
                ", Peer=" + peer +
                ", Date=" + date +
                ", Time=" + time +
                ", state=" + state +
                '}';
    }

}
