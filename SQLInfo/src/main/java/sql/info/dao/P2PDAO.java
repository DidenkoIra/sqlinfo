package sql.info.dao;

import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import sql.info.mappers.P2PMapper;
import sql.info.models.P2P;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

@Component
public class P2PDAO {
    private final JdbcTemplate jdbcTemplate;
    private final CopyManager copyManager;

    @Autowired
    public P2PDAO(JdbcTemplate jdbcTemplate, CopyManager copyManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.copyManager = copyManager;
    }

    public List<P2P> index() {
        return jdbcTemplate.query("SELECT * FROM p2p", new P2PMapper());
    }

    public P2P show(int id) {
        return jdbcTemplate.query("SELECT * FROM p2p WHERE id=?",
                        new Object[]{id}, new P2PMapper())
                .stream().findAny().orElse(null);
    }

    public void update(int id, P2P newP2P) {
        jdbcTemplate.update("UPDATE p2p SET check_=?, checkingpeer=?, state=?::state, time=? WHERE id=?",
                newP2P.getCheck().getId(),
                newP2P.getCheckingPeer().getNickname(),
                newP2P.getState().toString(),
                newP2P.getTime(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM p2p where id=?", id);
    }

    public void save(P2P p2p) {
        jdbcTemplate.update("INSERT INTO p2p (check_, checkingpeer, state, time) VALUES(?, ?, ?::state, ?::Time)",
                p2p.getCheck().getId(),
                p2p.getCheckingPeer().getNickname(),
                p2p.getState().toString(), p2p.getTime());
    }

    public void importFromCsv(String path) throws SQLException, IOException {
        copyManager.copyIn("COPY p2p FROM STDIN WITH CSV", new FileReader(path));
    }

    public void exportToCsv(OutputStream path) throws SQLException, IOException {
        copyManager.copyOut("COPY p2p TO STDOUT WITH CSV", path);
    }
}
