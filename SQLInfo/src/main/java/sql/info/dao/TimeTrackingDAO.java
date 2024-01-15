package sql.info.dao;

import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import sql.info.mappers.TaskMapper;
import sql.info.mappers.TimeTrackingMapper;
import sql.info.models.Task;
import sql.info.models.TimeTracking;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;


@Component
public class TimeTrackingDAO {
    private final JdbcTemplate jdbcTemplate;
    private final CopyManager copyManager;

    @Autowired
    public TimeTrackingDAO(JdbcTemplate jdbcTemplate, CopyManager copyManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.copyManager = copyManager;
    }

    public List<TimeTracking> index() {
        return jdbcTemplate.query("SELECT * FROM timetracking", new TimeTrackingMapper());
    }

    public TimeTracking show(int id) {
        return jdbcTemplate.query("SELECT * FROM timetracking WHERE id=?",
                        new Object[]{id}, new TimeTrackingMapper())
                .stream().findAny().orElse(null);
    }

    public void update(int id, TimeTracking newTimeTracking) {
        jdbcTemplate.update("UPDATE timetracking SET peer=?, date=?, time=?, state=? WHERE id=?", newTimeTracking.getPeer().getNickname(),
                newTimeTracking.getDate(), newTimeTracking.getTime(), newTimeTracking.getState(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM timetracking where id=?", id);
    }

    public void save(TimeTracking timeTracking) {
        jdbcTemplate.update("INSERT INTO timetracking (peer, date, time, state) VALUES(?, ?, ?, ?)", timeTracking.getPeer().getNickname(),
                timeTracking.getDate(), timeTracking.getTime(), timeTracking.getState());
    }

    public void importFromCsv(String path) {
        try {
            copyManager.copyIn("COPY timetracking FROM STDIN WITH CSV", new FileReader(path));
        } catch (Exception exception) {

        }
    }

    public void exportToCsv(OutputStream path) {
        try {
            copyManager.copyOut("COPY tasks TO STDOUT WITH CSV", path);
        } catch (SQLException | IOException exception) {

        }
    }
}
