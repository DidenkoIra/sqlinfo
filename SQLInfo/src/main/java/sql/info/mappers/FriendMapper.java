package sql.info.mappers;

import org.springframework.jdbc.core.RowMapper;
import sql.info.models.Friend;
import sql.info.models.Peer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendMapper implements RowMapper<Friend> {
    @Override
    public Friend mapRow(ResultSet resultSet, int i) throws SQLException {
        final int id = resultSet.getInt("id");
        final String peer1 = resultSet.getString("peer1");
        final String peer2 = resultSet.getString("peer2");
        return new Friend(id, new Peer(peer1), new Peer(peer2));
    }
}
