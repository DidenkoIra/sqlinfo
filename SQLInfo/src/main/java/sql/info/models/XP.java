package sql.info.models;

import java.sql.Time;

public class XP {
    int id;
    private Check check;
    private int xpAmount;

    public XP() {}

    public XP(int id, Check check, int xpAmount) {
        this.id = id;
        this.check = check;
        this.xpAmount = xpAmount;
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


    public int getXpAmount() {
        return xpAmount;
    }

    public void setXpAmount(int xpAmount) {
        this.xpAmount = xpAmount;
    }
    @Override
    public String toString() {
        return "XP{" +
                "id=" + id +
                ", check=" + check.toString() +
                ", XPAmount=" + xpAmount +
                '}';
    }
}
