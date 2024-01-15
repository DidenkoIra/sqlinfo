package sql.info.mappers;

import org.springframework.jdbc.core.RowMapper;
import sql.info.models.Peer;
import sql.info.models.TimeTracking;
import sql.info.models.TransferredPoints;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class TransferredPointsMapper implements RowMapper<TransferredPoints> {
    @Override
    public TransferredPoints mapRow(ResultSet resultSet, int i) throws SQLException {
        final int id = resultSet.getInt("id");
        final String checkingPeer = resultSet.getString("checkingpeer");
        final String checkedPeer = resultSet.getString("checkedpeer");
        final int pointsAmount = resultSet.getInt("pointsamount");
        return new TransferredPoints(id, new Peer(checkingPeer), new Peer(checkedPeer), pointsAmount);
    }
}
