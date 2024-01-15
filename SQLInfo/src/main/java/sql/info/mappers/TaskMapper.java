package sql.info.mappers;

import org.springframework.jdbc.core.RowMapper;
import sql.info.dao.TaskDAO;
import sql.info.models.Task;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskMapper implements RowMapper<Task> {
    @Override
    public Task mapRow(ResultSet resultSet, int i) throws SQLException {
        final String title = resultSet.getString("title");
        final String parentTask = resultSet.getString("parenttask");
        final int maxXP = resultSet.getInt("maxxp");
        return new Task(title, new Task(parentTask), maxXP);
    }
}
