package sql.info.dao;

import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import sql.info.mappers.FriendMapper;
import sql.info.models.Friend;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

@Component
public class FriendDAO {
    private final JdbcTemplate jdbcTemplate;
    private final CopyManager copyManager;

    @Autowired
    public FriendDAO(JdbcTemplate jdbcTemplate, CopyManager copyManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.copyManager = copyManager;
    }

    public List<Friend> index() {
        return jdbcTemplate.query("SELECT * FROM friends", new FriendMapper());
    }

    public Friend show(int id) {
        return jdbcTemplate.query("SELECT * FROM friends WHERE id=?",
                        new Object[]{id}, new FriendMapper())
                .stream().findAny().orElse(null);
    }

    public void update(int id, Friend newFriend) {
        jdbcTemplate.update("UPDATE friends SET peer1=?, peer2=? WHERE id=?", newFriend.getPeer1().getNickname(),
                newFriend.getPeer2().getNickname(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM friends where id=?", id);
    }

    public void save(Friend friend) {
        jdbcTemplate.update("INSERT INTO friends (peer1, peer2) VALUES(?, ?)", friend.getPeer1().getNickname(), friend.getPeer2().getNickname());
    }

    public void importFromCsv(String path) throws SQLException, IOException  {
       copyManager.copyIn("COPY friends FROM STDIN WITH CSV", new FileReader(path));
    }

    public void exportToCsv(OutputStream path) throws SQLException, IOException {
        copyManager.copyOut("COPY friends TO STDOUT WITH CSV", path);
    }
}
