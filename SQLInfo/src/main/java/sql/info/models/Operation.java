package sql.info.models;

import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Method;
import java.util.*;

public class Operation {
    private String name;
    private String text;
    private List<String> columnsName;
    private List<List<String>> result;
    private Map<String, String> parameters;
    private List<String> parametersList;

    private String methodName;

    public Operation() {
        parameters = new HashMap<>();
        parametersList = new ArrayList<>();
    }

    public Operation(String name) {
        this.name = name;
        parameters = new HashMap<>();
        parametersList = new ArrayList<>();
    }

    public Operation(String name, String methodName) {
        this.name = name;
        this.methodName = methodName;
        parameters = new HashMap<>();
        parametersList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColumnsName() {
        return columnsName;
    }

    public void setColumnsName(List<String> columnsName) {
        this.columnsName = columnsName;
    }

    public List<List<String>> getResult() {
        return result;
    }

    public void setResult(List<List<String>> result) {
        this.result = result;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public List<String> getParametersList() {
        return parametersList;
    }

    public void setParametersList(List<String> parametersList) {
        this.parametersList = parametersList;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getParametersValuesList() {
        List<String> values = new ArrayList<>();
        for (String key : parametersList) {
            values.add(parameters.get(key));
        }
        return values;
    }

    public void parseParameters() {
        StringTokenizer stringTokenizer = new StringTokenizer(text,"$");
        if (stringTokenizer.hasMoreTokens()) stringTokenizer.nextToken();
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            Scanner scanner = new Scanner(token);
            String key = scanner.next();
            if (!parameters.containsKey(key)) {
                parameters.put(key, "");
            }
            if (!parametersList.contains(key)) {
                parametersList.add(key);
            }
        }
    }

    public String getPreparedText() {
        String preparedText = new String(text);
        for (String key :parametersList) {
            preparedText = preparedText.replace("$"+key,"?");
        }
        return preparedText;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", parameters=" + parameters +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}