package sql.info.dao;

import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import sql.info.mappers.TransferredPointsMapper;
import sql.info.models.TransferredPoints;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;


@Component
public class TransferredPointsDAO {
    private final JdbcTemplate jdbcTemplate;
    private final CopyManager copyManager;

    @Autowired
    public TransferredPointsDAO(JdbcTemplate jdbcTemplate, CopyManager copyManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.copyManager = copyManager;
    }

    public List<TransferredPoints> index() {
        return jdbcTemplate.query("SELECT * FROM transferredpoints", new TransferredPointsMapper());
    }

    public TransferredPoints show(int id) {
        return jdbcTemplate.query("SELECT * FROM transferredpoints WHERE id=?",
                        new Object[]{id}, new TransferredPointsMapper())
                .stream().findAny().orElse(null);
    }

    public void update(int id, TransferredPoints newTransferredPoints) {
        jdbcTemplate.update("UPDATE transferredpoints SET checkingPeer=?, checkedPeer=?, pointsAmount=? WHERE id=?", newTransferredPoints.getCheckingPeer().getNickname(),
                newTransferredPoints.getCheckedPeer().getNickname(), newTransferredPoints.getPointsAmount(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM tasks where id=?", id);
    }

    public void save(TransferredPoints transferredPoints) {
        jdbcTemplate.update("INSERT INTO transferredpoints (checkingPeer, checkedPeer, pointsAmount) VALUES(?, ?, ?)", transferredPoints.getCheckingPeer().getNickname(),
                transferredPoints.getCheckedPeer().getNickname(), transferredPoints.getPointsAmount());
    }

    public void importFromCsv(String path) {
        try {
            copyManager.copyIn("COPY transferredpoints FROM STDIN WITH CSV", new FileReader(path));
        } catch (Exception exception) {

        }
    }

    public void exportToCsv(OutputStream path) {
        try {
            copyManager.copyOut("COPY transferredpoints TO STDOUT WITH CSV", path);
        } catch (SQLException | IOException exception) {

        }
    }
}
