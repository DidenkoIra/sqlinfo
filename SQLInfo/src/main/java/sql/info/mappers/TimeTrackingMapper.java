package sql.info.mappers;

import org.springframework.jdbc.core.RowMapper;
import sql.info.models.Peer;
import sql.info.models.TimeTracking;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class TimeTrackingMapper implements RowMapper<TimeTracking>{
    @Override
    public TimeTracking mapRow(ResultSet resultSet, int i) throws SQLException {
        final int id = resultSet.getInt("id");
        final String peer = resultSet.getString("peer");
        final Date date = resultSet.getDate("date");
        final Time time = resultSet.getTime("time");
        final String state = resultSet.getString("state");
        return new TimeTracking(id, new Peer(peer), date, time, Integer.parseInt(state));
    }
}
