package database;

import lombok.AllArgsConstructor;
import lombok.Data;
import resource.DBNode;
import resource.data.Row;

import java.util.List;

@Data
@AllArgsConstructor
public class DatabaseImplementation implements Database {

    private Repository repository;


    @Override
    public DBNode loadResource() {
        return repository.getSchema();
    }

    @Override
    public List<Row> readDataFromTable(String tableName) {
        return repository.get(tableName);
    }

    @Override
    public List<String> loadForeignKeys(String tableName) {
        return repository.getForeignKey(tableName);
    }

    @Override
    public List<String> loadColNames(String from) {
        return repository.getColNames(from);
    }

    @Override
    public List<String> loadResSet(String from) {
        return repository.getResSet(from);
    }

    @Override
    public List<String> loadFunctioNames() {
        return repository.getFunctionNames();
    }

    @Override
    public List<Row> loadGetQuery(String from) {
        return repository.runGetQuery(from);
    }

    @Override
    public int loadExecUpdate(String from) {
        return repository.runExecUpdate(from);
    }

    @Override
    public List<String> loadTableNames() {
        return repository.getTableNames();
    }
}
