package sql.info.mappers;

import org.springframework.jdbc.core.RowMapper;
import sql.info.models.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class XPMapper implements RowMapper<XP> {
    @Override
    public XP mapRow(ResultSet resultSet, int i) throws SQLException {
        final int id = resultSet.getInt("id");
        final int check = resultSet.getInt("check_");
        final int xpAmount = resultSet.getInt("xpamount");
        return new XP(id, new Check(check), xpAmount);
    }
}
