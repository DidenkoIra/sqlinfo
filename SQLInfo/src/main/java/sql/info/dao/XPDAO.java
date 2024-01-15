package sql.info.dao;

import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import sql.info.mappers.XPMapper;
import sql.info.models.XP;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

@Component
public class XPDAO {
    private final JdbcTemplate jdbcTemplate;
    private final CopyManager copyManager;

    @Autowired
    public XPDAO(JdbcTemplate jdbcTemplate, CopyManager copyManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.copyManager = copyManager;
    }

    public List<XP> index() {
        return jdbcTemplate.query("SELECT * FROM xp", new XPMapper());
    }

    public XP show(int id) {
        return jdbcTemplate.query("SELECT * FROM xp WHERE id=?",
                        new Object[]{id}, new XPMapper())
                .stream().findAny().orElse(null);
    }

    public void update(int id, XP newXP) {
        jdbcTemplate.update("UPDATE xp SET check_=?, xpamount=? WHERE id=?",
                newXP.getCheck().getId(),
                newXP.getXpAmount(),
                id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM xp where id=?", id);
    }

    public void save(XP xp) {
        jdbcTemplate.update("INSERT INTO xp (check_, xpAmount) VALUES(?, ?)",
                xp.getCheck().getId(),
                xp.getXpAmount());
    }

    public void importFromCsv(String path) throws SQLException, IOException{
        copyManager.copyIn("COPY xp FROM STDIN WITH CSV", new FileReader(path));
    }

    public void exportToCsv(OutputStream path) {
        try {
            copyManager.copyOut("COPY xp TO STDOUT WITH CSV", path);
        } catch (SQLException | IOException exception) {
            // дописать в логи
        }
    }
}
