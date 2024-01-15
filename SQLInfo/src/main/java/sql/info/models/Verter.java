package sql.info.models;

import java.sql.Time;

public class Verter {
    int id;
    private Check check;

    private CheckState state;
    private Time time;

    public Verter() {}

    public Verter(int id, Check check, CheckState state, Time time) {
        this.id = id;
        this.check = check;
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
        return "Verter{" +
                "id=" + id +
                ", check=" + check.toString() +
                ", state=" + state.toString() +
                ", time=" + time.toString() +
                '}';
    }
}
