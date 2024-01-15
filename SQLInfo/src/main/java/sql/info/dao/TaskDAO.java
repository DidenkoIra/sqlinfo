package sql.info.dao;

import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import sql.info.mappers.TaskMapper;
import sql.info.models.Task;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;


@Component
public class TaskDAO {
    private final JdbcTemplate jdbcTemplate;
    private final CopyManager copyManager;

    @Autowired
    public TaskDAO(JdbcTemplate jdbcTemplate, CopyManager copyManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.copyManager = copyManager;
    }

    public List<Task> index() {
        return jdbcTemplate.query("SELECT * FROM tasks", new TaskMapper());
    }

    public Task show(String title) {
        return jdbcTemplate.query("SELECT * FROM tasks WHERE title=?",
                        new Object[]{title}, new TaskMapper())
                .stream().findAny().orElse(null);
    }

    public void update(String title, Task newTask) {
        jdbcTemplate.update("UPDATE tasks SET title=?, parentTask=?, maxXP=? WHERE title=?", newTask.getTitle(),
                newTask.getParentTask().getTitle(), newTask.getMaxXP(), title);
    }

    public void delete(String title) {
        jdbcTemplate.update("DELETE FROM tasks where title=?", title);
    }

    public void save(Task task) {
        jdbcTemplate.update("INSERT INTO tasks (title, parentTask, maxXP) VALUES(?, ?, ?)", task.getTitle(),
                task.getParentTask().getTitle(), task.getMaxXP());
    }

    public void importFromCsv(String path) {
        try {
            copyManager.copyIn("COPY tasks FROM STDIN WITH CSV", new FileReader(path));
        } catch (Exception exception) {

        }
    }

    public void exportToCsv(OutputStream path) {
        try {
            copyManager.copyOut("COPY tasks TO STDOUT WITH CSV", path);
        } catch (SQLException | IOException exception) {

        }
    }
}
