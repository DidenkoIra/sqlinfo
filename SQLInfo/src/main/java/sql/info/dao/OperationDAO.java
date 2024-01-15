package sql.info.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sql.info.models.Operation;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class OperationDAO {
    private final Connection connection;
    @Autowired
    public OperationDAO(Connection connection) {
        this.connection = connection;
    }

    private List<String> getColumnsNameFromMetaData (ResultSet resultSet) throws SQLException{
        ResultSetMetaData resultSetMetaData= resultSet.getMetaData();
        List<String> columnsName = new ArrayList<>();
        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
            columnsName.add(resultSetMetaData.getColumnName(i));
        }
        return columnsName;
    }

    public void executeQuery(Operation operation) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(operation.getPreparedText());
        setParametersToStatement(statement, operation);
        ResultSet resultSet  = statement.executeQuery();

        List<String> columnsName = getColumnsNameFromMetaData(resultSet);
        List<List<String>> result = new ArrayList<>();
        while (resultSet.next()) {
            List<String> record = new ArrayList<>();
            for (int i = 0; i < columnsName.size(); i++) {
                record.add(resultSet.getString(columnsName.get(i)));
            }
            result.add(record);
        }
        statement.close();

        operation.setColumnsName(columnsName);
        operation.setResult(result);
    }

    public void showTransferredPoints(Operation operation) throws SQLException {
        operation.setName("Transferred points");
        operation.setText("SELECT * FROM TransferredPoints();");
        operation.setMethodName("showTransferredPoints");
        executeQuery(operation);
    }

    public void showCheckedPeersWithXP(Operation operation) throws SQLException {
        operation.setName("Checked peers with XP");
        operation.setText("SELECT * FROM CheckedPeersWithXP();");
        operation.setMethodName("showCheckedPeersWithXP");
        executeQuery(operation);
    }

    public void showPeersInCampus(Operation operation) throws SQLException {
        operation.setText("SELECT * FROM PeersInCampus(?::date);");
        operation.getParametersList().add("date");
        executeQuery(operation);
    }

    public void showPointsChange(Operation operation) throws SQLException{
        operation.setName("Points change");
        operation.setText("SELECT * FROM PointsChange();");
        operation.setMethodName("showPointsChange");
        executeQuery(operation);
    }

    public void showPointsChangeV2(Operation operation) throws SQLException{
        operation.setName("Points change 2.0");
        operation.setText("SELECT * FROM PointsChange_v2();");
        operation.setMethodName("showPointsChangeV2");
        executeQuery(operation);
    }

    public void showMostFrequentTaskDaily(Operation operation) throws SQLException{
        operation.setName("Most frequent task daily");
        operation.setText("SELECT * FROM MostFrequentTaskDaily();");
        operation.setMethodName("showMostFrequentTaskDaily");
        executeQuery(operation);
    }

    public void showPeersByGroups(Operation operation) throws SQLException {
        operation.setText("SELECT * FROM PeersByGroups(?, ?);");
        operation.getParametersList().add("block1");
        operation.getParametersList().add("block2");
        executeQuery(operation);
    }

    public void execute(Operation operation)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.getClass().getMethod(operation.getMethodName(), operation.getClass()).invoke(this, operation);
    }

    public void showCompleteBlock(Operation operation) throws SQLException {
        operation.setText("SELECT * FROM CompleteBlock(?);");
        operation.getParametersList().add("block");
        executeQuery(operation);
    }

    public void showFindPeerForCheck(Operation operation) throws SQLException {
        operation.setName("Find peer for check");
        operation.setText("SELECT * FROM FindPeerForCheck();");
        operation.setMethodName("showFindPeerForCheck");
        executeQuery(operation);
    }

    public void showPeersWithBirthdayCheck(Operation operation) throws SQLException {
        operation.setName("Peers with birthday check");
        operation.setText("SELECT * FROM PeersWithBDayCheck();");
        operation.setMethodName("showPeersWithBirthdayCheck");
        executeQuery(operation);
    }

    public void showGivenAndNotGivenTasks(Operation operation) throws SQLException {
        operation.setText("SELECT * FROM GivenAndNotGivenTasks(?, ?, ?);");
        operation.getParametersList().add("task1");
        operation.getParametersList().add("task2");
        operation.getParametersList().add("task3");
        executeQuery(operation);
    }

    public void showCountOfPreviousTasks(Operation operation) throws SQLException{
        operation.setName("Count of previous tasks");
        operation.setText("SELECT * FROM CountOfPreviousTasks();");
        operation.setMethodName("showCountOfPreviousTasks");
        executeQuery(operation);
    }

    public void showFindLuckyDaysForChecks(Operation operation) throws SQLException {
        operation.setText("SELECT * FROM FindLuckyDaysForChecks(?::int);");
        operation.getParametersList().add("N");
        executeQuery(operation);
    }

    public void showGetPeerWithMaxXP(Operation operation) throws SQLException{
        operation.setName("Get peer with max XP");
        operation.setText("SELECT * FROM GetPeerWithMaxXp();");
        operation.setMethodName("showGetPeerWithMaxXP");
        executeQuery(operation);
    }
    public void showGetPeersMaxTimeSpent(Operation operation) throws SQLException {
        operation.setText("SELECT * FROM GetPeerMaxTimeSpent(?::time, ?::int);");
        operation.getParametersList().add("Time");
        operation.getParametersList().add("N");
        executeQuery(operation);
    }

    public void showGetPeersLeftCampus(Operation operation) throws SQLException {
        operation.setText("SELECT * FROM GetPeersLeftCampus(?::int, ?::int);");
        operation.getParametersList().add("N");
        operation.getParametersList().add("M");
        executeQuery(operation);
    }

    public void showPercentageOfEarlyEntries(Operation operation) throws SQLException {
        operation.setName("Percentage of early entries");
        operation.setText("SELECT * FROM PercentageOfEarlyEntries();");
        operation.setMethodName("showPercentageOfEarlyEntries");
        executeQuery(operation);
    }


    private void setParametersToStatement(PreparedStatement preparedStatement, Operation operation) throws SQLException {
        List<String> values = operation.getParametersValuesList();
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            Scanner scanner = new Scanner(value);
            if (scanner.hasNextInt()) {
                preparedStatement.setInt(i+1, scanner.nextInt());
            } else {
                try {
                    preparedStatement.setDate(i+1, Date.valueOf(value));
                } catch (Exception exceptionDate) {
                    try {
                        preparedStatement.setTime(i+1, Time.valueOf(value));
                    } catch (Exception exceptionTime) {
                        preparedStatement.setString(i+1, value);
                    }
                }
            }
        }
    }

    public String exportToCSV(Operation operation)
            throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        execute(operation);
        StringBuilder builder = new StringBuilder();
        for (List<String> row : operation.getResult()) {
            for (String cell : row) {
                builder.append(cell).append(',');
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

}
