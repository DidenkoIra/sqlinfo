package sql.info.dao;

import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import sql.info.mappers.RecommendationMapper;
import sql.info.models.Recommendation;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

@Component
public class RecommendationDAO {
    private final JdbcTemplate jdbcTemplate;
    private final CopyManager copyManager;

    @Autowired
    public RecommendationDAO(JdbcTemplate jdbcTemplate, CopyManager copyManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.copyManager = copyManager;
    }

    public List<Recommendation> index() {
        return jdbcTemplate.query("SELECT * FROM recommendations", new RecommendationMapper());
    }

    public Recommendation show(int id) {
        return jdbcTemplate.query("SELECT * FROM recommendations WHERE id=?",
                        new Object[]{id}, new RecommendationMapper())
                .stream().findAny().orElse(null);
    }

    public void update(int id, Recommendation newRecommendation) {
        jdbcTemplate.update("UPDATE recommendations SET peer=?, recommendedpeer=? WHERE id=?",
                newRecommendation.getPeer().getNickname(),
                newRecommendation.getRecommendedPeer().getNickname(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM recommendations where id=?", id);
    }

    public void save(Recommendation recommendation) {
        jdbcTemplate.update("INSERT INTO recommendations (peer, recommendedpeer) VALUES(?, ?)",
                recommendation.getPeer().getNickname(), recommendation.getRecommendedPeer().getNickname());
    }

    public void importFromCsv(String path) throws SQLException, IOException {
        copyManager.copyIn("COPY recommendations FROM STDIN WITH CSV", new FileReader(path));
    }

    public void exportToCsv(OutputStream path) throws SQLException, IOException {
        copyManager.copyOut("COPY recommendations TO STDOUT WITH CSV", path);
    }
}
