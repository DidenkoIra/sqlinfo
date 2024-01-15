package sql.info.dao;

import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import sql.info.mappers.VerterMapper;
import sql.info.models.Verter;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

@Component
public class VerterDAO {
    private final JdbcTemplate jdbcTemplate;
    private final CopyManager copyManager;

    @Autowired
    public VerterDAO(JdbcTemplate jdbcTemplate, CopyManager copyManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.copyManager = copyManager;
    }

    public List<Verter> index() {
        return jdbcTemplate.query("SELECT * FROM verter", new VerterMapper());
    }

    public Verter show(int id) {
        return jdbcTemplate.query("SELECT * FROM verter WHERE id=?",
                        new Object[]{id}, new VerterMapper())
                .stream().findAny().orElse(null);
    }

    public void update(int id, Verter newVerter) {
        jdbcTemplate.update("UPDATE verter SET check_=?, state=?::state, time=? WHERE id=?",
                newVerter.getCheck().getId(),
                newVerter.getState().toString(),
                newVerter.getTime(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM verter where id=?", id);
    }

    public void save(Verter verter) {
        jdbcTemplate.update("INSERT INTO verter (check_, state, time) VALUES(?, ?::state, ?::Time)",
                verter.getCheck().getId(),
                verter.getState().toString(), verter.getTime());
    }

    public void importFromCsv(String path) throws SQLException, IOException{
        copyManager.copyIn("COPY verter FROM STDIN WITH CSV", new FileReader(path));
    }

    public void exportToCsv(OutputStream path) {
        try {
            copyManager.copyOut("COPY verter TO STDOUT WITH CSV", path);
        } catch (SQLException | IOException exception) {
            // дописать в логи
        }
    }
}
