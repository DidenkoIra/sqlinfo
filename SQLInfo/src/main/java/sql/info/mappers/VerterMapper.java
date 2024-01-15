package sql.info.mappers;

import org.springframework.jdbc.core.RowMapper;
import sql.info.models.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class VerterMapper implements RowMapper<Verter> {
    @Override
    public Verter mapRow(ResultSet resultSet, int i) throws SQLException {
        final int id = resultSet.getInt("id");
        final int check = resultSet.getInt("check_");
        final String state = resultSet.getString("state");
        final Time time = resultSet.getTime("time");
        return new Verter(id, new Check(check), CheckState.valueOf(state), time);
    }
}
