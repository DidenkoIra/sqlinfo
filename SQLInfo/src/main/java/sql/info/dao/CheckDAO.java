package sql.info.dao;

import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import sql.info.mappers.CheckMapper;
import sql.info.models.Check;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

@Component
public class CheckDAO {
    private final JdbcTemplate jdbcTemplate;
    private final CopyManager copyManager;

    @Autowired
    public CheckDAO(JdbcTemplate jdbcTemplate, CopyManager copyManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.copyManager = copyManager;
    }

    public List<Check> index() {
        return jdbcTemplate.query("SELECT * FROM checks", new CheckMapper());
    }

    public Check show(int id) {
        return jdbcTemplate.query("SELECT * FROM checks WHERE id=?",
                        new Object[]{id}, new CheckMapper())
                .stream().findAny().orElse(null);
    }

    public void update(int id, Check newCheck) {
        jdbcTemplate.update("UPDATE checks SET peer=?, task=?, date=? WHERE id=?", newCheck.getPeer().getNickname(),
                newCheck.getTask().getTitle(), newCheck.getDate(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM checks where id=?", id);
    }

    public void save(Check check) {
        jdbcTemplate.update("INSERT INTO checks (peer, task, date) VALUES(?, ?, ?)", check.getPeer().getNickname(),
                check.getTask().getTitle(), check.getDate());
    }

    public void importFromCsv(String path) throws SQLException, IOException {
        copyManager.copyIn("COPY checks FROM STDIN WITH CSV", new FileReader(path));
    }

    public void exportToCsv(OutputStream path) throws SQLException, IOException {
        copyManager.copyOut("COPY checks TO STDOUT WITH CSV", path);
    }
}
