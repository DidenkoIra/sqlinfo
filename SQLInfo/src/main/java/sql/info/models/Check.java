package sql.info.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Check {
    int id;
    private Peer peer;
    private Task task;
    @DateTimeFormat(pattern = "dd.MM.yy")
    private Date date;

    public Check() {}

    public Check(int id) {
        this.id = id;
    }

    public Check(int id, Peer peer, Task task, Date date) {
        this.id = id;
        this.peer = peer;
        this.task = task;
        this.date = date;
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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Check{" +
                (id != 0 ? "id=" + id : "")+
                ", peer=" + peer.toString() +
                ", task=" + task.toString() +
                ", date=" + date.toString() +
                '}';
    }
}
