package database;

import resource.DBNode;
import resource.data.Row;

import java.util.List;

public interface Database {

    DBNode loadResource();

    List<Row> readDataFromTable(String tableName);


    List<String> loadForeignKeys(String tableName);

    List<String> loadColNames(String from);


    List<String> loadResSet(String from);

    List<String> loadFunctioNames();

    List<Row> loadGetQuery(String from);

    int loadExecUpdate(String from);

    List<String> loadTableNames();
}
