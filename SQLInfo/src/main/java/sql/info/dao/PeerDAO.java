package sql.info.dao;

import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import sql.info.models.Peer;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

@Component
public class PeerDAO {
    private final JdbcTemplate jdbcTemplate;
    private final CopyManager copyManager;

    @Autowired
    public PeerDAO(JdbcTemplate jdbcTemplate, CopyManager copyManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.copyManager = copyManager;
    }

    public List<Peer> index() {
        return jdbcTemplate.query("SELECT * FROM peers", new BeanPropertyRowMapper<>(Peer.class));
    }

    public Peer show(String nickname) {
        return jdbcTemplate.query("SELECT * FROM peers WHERE nickname=?",
                        new Object[]{nickname}, new BeanPropertyRowMapper<>(Peer.class))
                .stream().findAny().orElse(null);
    }

    public void delete(String nickname) {
        jdbcTemplate.update("DELETE FROM peers where nickname=?", nickname);
    }

    public void update(String nickname, Peer newPeer) {
        jdbcTemplate.update("UPDATE peers SET birthday=? where nickname=?", newPeer.getBirthday(), nickname);
    }

    public void save(Peer peer) {
        jdbcTemplate.update("INSERT INTO peers VALUES(?, ?)", peer.getNickname(), peer.getBirthday());
    }

    public void importFromCsv(String path) throws SQLException, IOException {
        copyManager.copyIn("COPY peers FROM STDIN WITH CSV", new FileReader(path));
    }

    public void exportToCsv(OutputStream path) throws SQLException, IOException {
        copyManager.copyOut("COPY peers TO STDOUT WITH CSV", path);
    }
}
