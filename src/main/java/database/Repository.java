package database;

import resource.DBNode;
import resource.data.Row;

import java.util.List;

public interface Repository {


    DBNode getSchema();

    List<String> getTableNames();

    List<String> getFunctionNames();

    List<String> getForeignKey(String odakle);

    List<String> getColNames(String from);

    List<Row> get(String from);

    List<String> getResSet(String from);

    List<Row> runGetQuery(String query);

    int runExecUpdate(String query);
}
