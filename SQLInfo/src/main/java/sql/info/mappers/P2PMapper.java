package sql.info.mappers;

import org.springframework.jdbc.core.RowMapper;
import sql.info.models.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class P2PMapper implements RowMapper<P2P> {
    @Override
    public P2P mapRow(ResultSet resultSet, int i) throws SQLException {
        final int id = resultSet.getInt("id");
        final int check = resultSet.getInt("check_");
        final String checkingPeer = resultSet.getString("checkingpeer");
        final String state = resultSet.getString("state");
        final Time time = resultSet.getTime("time");
        return new P2P(id, new Check(check), new Peer(checkingPeer), CheckState.valueOf(state), time);
    }
}
