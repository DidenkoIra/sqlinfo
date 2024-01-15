package sql.info.mappers;

import org.springframework.jdbc.core.RowMapper;
import sql.info.models.Check;
import sql.info.models.Peer;
import sql.info.models.Task;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckMapper implements RowMapper<Check> {
    @Override
    public Check mapRow(ResultSet resultSet, int i) throws SQLException {
        final int id = resultSet.getInt("id");
        final String peer = resultSet.getString("peer");
        final String task = resultSet.getString("task");
        final Date date = resultSet.getDate("date");
        return new Check(id, new Peer(peer), new Task(task), date);
    }
}
