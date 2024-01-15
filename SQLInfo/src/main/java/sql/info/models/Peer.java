package sql.info.models;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

public class Peer {
    @NotEmpty(message = "Name should not be empty")
    private String nickname;
    @DateTimeFormat(pattern = "dd.MM.yy")
    private Date birthday;

    public Peer() {}

    public Peer(String nickname, Date birthday) {
        this.nickname = nickname;
        this.birthday = birthday;
    }

    public Peer(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "nickname='" + nickname + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
