package sql.info.mappers;

import org.springframework.jdbc.core.RowMapper;
import sql.info.models.Peer;
import sql.info.models.Recommendation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RecommendationMapper implements RowMapper<Recommendation> {
    @Override
    public Recommendation mapRow(ResultSet resultSet, int i) throws SQLException {
        final int id = resultSet.getInt("id");
        final String peer = resultSet.getString("peer");
        final String recommendedPeer = resultSet.getString("recommendedpeer");
        return new Recommendation(id, new Peer(peer), new Peer(recommendedPeer));
    }
}
