package database;

import database.settings.Settings;
import lombok.Data;
import resource.DBNode;
import resource.data.Row;
import resource.enums.AttributeType;
import resource.implementation.Attribute;
import resource.implementation.Entity;
import resource.implementation.InformationResource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MYSQLrepository implements Repository {

    private Settings settings;
    private Connection connection;

    public MYSQLrepository(Settings settings) {
        this.settings = settings;
    }

    private void initConnection() throws SQLException, ClassNotFoundException {
        String ip = (String) settings.getParameter("mysql_ip");
        String database = (String) settings.getParameter("mysql_database");
        String username = (String) settings.getParameter("mysql_username");
        String password = (String) settings.getParameter("mysql_password");
        //Class.forName("net.sourceforge.jtds.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + ip + "/" + database, username, password);


    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection = null;
        }
    }

    @Override
    public DBNode getSchema() {

        try {
            this.initConnection();

            DatabaseMetaData metaData = connection.getMetaData();
            InformationResource ir = new InformationResource("RAF_BP_Primer");

            String tableType[] = {"TABLE"};
            ResultSet tables = metaData.getTables(connection.getCatalog(), null, null, tableType);

            while (tables.next()) {

                String tableName = tables.getString("TABLE_NAME");
                if (tableName.contains("trace")) continue;
                Entity newTable = new Entity(tableName, ir);
                ir.addChild(newTable);

                //Koje atribute ima ova tabela?

                ResultSet columns = metaData.getColumns(connection.getCatalog(), null, tableName, null);

                while (columns.next()) {

                    // COLUMN_NAME TYPE_NAME COLUMN_SIZE ....

                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");

                    System.out.println(columnType);

                    int columnSize = Integer.parseInt(columns.getString("COLUMN_SIZE"));

//                    ResultSet pkeys = metaData.getPrimaryKeys(connection.getCatalog(), null, tableName);
//
//                    while (pkeys.next()){
//                        String pkColumnName = pkeys.getString("COLUMN_NAME");
//                    }


                    Attribute attribute = new Attribute(columnName, newTable,
                            AttributeType.valueOf(
                                    Arrays.stream(columnType.toUpperCase().split(" "))
                                            .collect(Collectors.joining("_"))),
                            columnSize);
                    newTable.addChild(attribute);

                }


            }


            //TODO Ogranicenja nad kolonama? Relacije?


            return ir;
            //String isNullable = columns.getString("IS_NULLABLE");
            // ResultSet foreignKeys = metaData.getImportedKeys(connection.getCatalog(), null, table.getName());
            // ResultSet primaryKeys = metaData.getPrimaryKeys(connection.getCatalog(), null, table.getName());

        } catch (SQLException | ClassNotFoundException e1) {
            e1.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return null;
    }


    @Override
    public List<String> getTableNames() {
        List<String> odgvor = new ArrayList<>();
        try {
            initConnection();

            DatabaseMetaData metaData = connection.getMetaData();

            String tableType[] = {"TABLE"};
            ResultSet tables = metaData.getTables(connection.getCatalog(), null, null, tableType);

            while (tables.next()) {

                String tableName = tables.getString("TABLE_NAME");

                odgvor.add(tableName);
            }
        } catch (SQLException | ClassNotFoundException e1) {
            e1.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return odgvor;
    }

    @Override
    public List<String> getFunctionNames() {
        List<String> odgvor = new ArrayList<>();
        try {
            this.initConnection();

            DatabaseMetaData metaData = connection.getMetaData();

            //  String tableType[] = {"TABLE"};
            ResultSet funkcije = metaData.getFunctions(connection.getCatalog(), null, null);

            while (funkcije.next()) {
                System.out.println(funkcije);
                //   String tableName = funkcije.getString("TABLE_NAME");

                // odgvor.add(tableName);
            }
        } catch (SQLException | ClassNotFoundException e1) {
            e1.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return odgvor;
    }

    @Override
    public List<String> getForeignKey(String odakle) {
        List<String> odgvor = new ArrayList<>();
        try {
            this.initConnection();

            DatabaseMetaData metaData = connection.getMetaData();

            //  String tableType[] = {"TABLE"};
            ResultSet funkcije = metaData.getImportedKeys(null, null, odakle);
            //ResultSetMetaData resultSetMetaData = funkcije.getMetaData();
            while (funkcije.next()) {
                //System.out.println(funkcije);
                String tableName = funkcije.getString("FKCOLUMN_NAME");
                /*
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = funkcije.getString(i);
                    System.out.print(columnValue );
                }
                System.out.println("");
                */


                odgvor.add(tableName);
            }
        } catch (SQLException | ClassNotFoundException e1) {
            e1.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return odgvor;
    }


    @Override
    public List<String> getColNames(String from) {
        List<String> odgvor = new ArrayList<>();
        try {
            this.initConnection();

            DatabaseMetaData metaData = connection.getMetaData();

            ResultSet columns = metaData.getColumns(connection.getCatalog(), null, from, null);

            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                odgvor.add(columnName);
            }

        } catch (SQLException | ClassNotFoundException x) {
            x.printStackTrace();
        }

        return odgvor;
    }

    @Override
    public List<Row> get(String from) {

        List<Row> rows = new ArrayList<>();


        try {
            this.initConnection();

            String query = "SELECT * FROM " + from;
            PreparedStatement preparedStatement = connection.prepareStatement(query); // mora da se doda sposobnost pomeranja kursora u okviru result  seta da bi mogli da se vratimo na pocetak nakon prolazenja
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            // SaveCSV s = new SaveCSV(rs, "paprika.csv");

            while (rs.next()) {

                Row row = new Row();
                row.setName(from);

                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {// zasto ide od 1???????
                    row.addField(resultSetMetaData.getColumnName(i), rs.getString(i));
                }
                rows.add(row);


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return rows;
    }

    @Override
    public List<String> getResSet(String from) {

        List<String> rows = new ArrayList<>();


        try {
            this.initConnection();

            String query = "SELECT * FROM " + from;
            PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE); // mora da se doda sposobnost pomeranja kursora u okviru result  seta da bi mogli da se vratimo na pocetak nakon prolazenja
            ResultSet r = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = r.getMetaData();

            DatabaseMetaData metaData = connection.getMetaData();

            // SaveCSV s = new SaveCSV(rs, "paprika.csv");

            ResultSet columns = metaData.getColumns(connection.getCatalog(), null, from, null);
            String red = "";
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                red += columnName + ", ";
            }

            red = red.substring(0, red.length() - 2);

            rows.add(red);

            while (r.next()) {
                String sop = "";
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) { // zasto ide od 1???????
                    sop += r.getString(i) + ", ";
                }
                sop = sop.substring(0, sop.length() - 2);
                //System.out.println(sop);
                rows.add(sop);
                // p.println(sop);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return rows;
    }

    @Override
    public List<Row> runGetQuery(String query) {

        List<Row> rows = new ArrayList<>();


        try {
            this.initConnection();

            //String query = "SELECT * FROM " + from;
            PreparedStatement preparedStatement = connection.prepareStatement(query); // mora da se doda sposobnost pomeranja kursora u okviru result  seta da bi mogli da se vratimo na pocetak nakon prolazenja
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            // SaveCSV s = new SaveCSV(rs, "paprika.csv");

            while (rs.next()) {

                Row row = new Row();
                row.setName(""); //FIXME A!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!AAAAAAAAAAA!!!!!!!!!!!

                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    row.addField(resultSetMetaData.getColumnName(i), rs.getString(i));
                }
                rows.add(row);


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return rows;
    }

    @Override
    public int runExecUpdate(String query) {
        try {
            this.initConnection();

            //String query = "SELECT * FROM " + from;
            PreparedStatement preparedStatement = connection.prepareStatement(query); // mora da se doda sposobnost pomeranja kursora u okviru result  seta da bi mogli da se vratimo na pocetak nakon prolazenja
            int rs = preparedStatement.executeUpdate();

            return rs;

            // ResultSetMetaData resultSetMetaData = rs.getMetaData();

            // SaveCSV s = new SaveCSV(rs, "paprika.csv");
/*
            while (rs.next()){

                Row row = new Row();
                row.setName(from);

                for (int i = 1; i<=resultSetMetaData.getColumnCount(); i++){// zasto ide od 1???????
                    row.addField(resultSetMetaData.getColumnName(i), rs.getString(i));
                }
                rows.add(row);


            }
            */

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }

        return -200;
    }


}
