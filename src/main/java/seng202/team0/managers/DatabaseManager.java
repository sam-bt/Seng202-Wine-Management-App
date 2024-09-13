package seng202.team0.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team0.database.Wine;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.Map;


/**
 * Mediates access to the database
 */
public class DatabaseManager implements AutoCloseable {

    /**
     * Database connection
     * <p>
     * This is ensured to be always valid
     * </p>
     */
    private Connection connection;

    /**
     * Connects to a db file for management. The path to the file is specified by dbpath
     *
     * @throws SQLException if failed to initialize
     */
    public DatabaseManager() throws SQLException {

        // Construct a file path for the database
        File dir = new File("sqlDatabase");
        if (!dir.exists()) {
            boolean created = dir.mkdirs();

            if (!created) {
                System.err.println("Error creating database directory");
            }

        }

        // Connect to database
        // This is the path to the db file
        //String dbPath = "jdbc:sqlite:sqlDatabase" + File.separator + "SQLDatabase.db";
        //this.connection = DriverManager.getConnection(dbPath);
        this.connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        createWinesTable();

    }

    /**
     * @throws SQLException on sql error
     */
    private void createWinesTable() throws SQLException {
        if (tableExists("WINE")) {
            return;
        }
        String create = "create table WINE (" +
                // There are a lot of duplicates
                "ID AUTO_INCREMENT PRIMARY KEY," +
                "TITLE varchar(64) NOT NULL," +
                "VARIETY varchar(32)," +
                "COUNTRY varchar(32)," +
                "WINERY varchar(64)," +
                "DESCRIPTION text," +
                "SCORE_PERCENT int," +
                "ABV float," +
                "PRICE float);";
        try (Statement statement = connection.createStatement()) {
            statement.execute(create);
        }
        assert (tableExists("WINE"));
    }

    /**
     * Gets a subset of the wines in the database
     * <p>
     * The order of elements should remain stable until a write operation occurs.
     * </p>
     *
     * @param begin beginning element
     * @param end   end element (begin + size)
     * @return subset list of wines
     */
    public ObservableList<Wine> getWinesInRange(int begin, int end) {

        ObservableList<Wine> wines = FXCollections.observableArrayList();
        String query = "select TITLE, VARIETY, COUNTRY, WINERY, DESCRIPTION, SCORE_PERCENT, ABV, PRICE from WINE order by ROWID limit ? offset ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, end - begin);
            statement.setInt(2, begin);

            ResultSet set = statement.executeQuery();
            while (set.next()) {

                Wine wine = new Wine(
                        set.getString("TITLE"),
                        set.getString("VARIETY"),
                        set.getString("COUNTRY"),
                        set.getString("WINERY"),
                        set.getString("DESCRIPTION"),
                        set.getInt("SCORE_PERCENT"),
                        set.getFloat("ABV"),
                        set.getFloat("PRICE")
                );
                wines.add(wine);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return wines;
    }

    /**
     * Gets a subset of the wines in the database with a Map for filtering
     * <p>
     * The order of elements should remain stable until a write operation occurs.
     * </p>
     *
     * @param begin   beginning element
     * @param end     end element (begin + size)
     * @param filters Map of filter values
     * @return subset list of wines
     */
    public ObservableList<Wine> getWinesInRange(int begin, int end, Map<String, Object> filters) {

        ObservableList<Wine> wines = FXCollections.observableArrayList();
        String query = generateFilteredSelectStatementString(filters);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int paramIndex = 1;

            // Order Matters here! Must match statement!
            if (filters.containsKey("title")) {
                statement.setString(paramIndex++, "%" + filters.get("title") + "%"); // Percent is wildcard → makes this a substring search
            }

            if (filters.containsKey("country")) {
                statement.setString(paramIndex++, "%" + filters.get("country") + "%");
            }

            if (filters.containsKey("winery")) {
                statement.setString(paramIndex++, "%" + filters.get("winery") + "%");
            }

            if (filters.containsKey("score")) {
                double[] scoreRange = (double[]) filters.get("score");
                statement.setDouble(paramIndex++, scoreRange[0]);
                statement.setDouble(paramIndex++, scoreRange[1]);
            }

            if (filters.containsKey("abv")) {
                double[] abvRange = (double[]) filters.get("abv");
                statement.setDouble(paramIndex++, abvRange[0]);
                statement.setDouble(paramIndex++, abvRange[1]);
            }

            if (filters.containsKey("price")) {
                double[] priceRange = (double[]) filters.get("price");
                statement.setDouble(paramIndex++, priceRange[0]);
                statement.setDouble(paramIndex++, priceRange[1]);
            }

            statement.setInt(paramIndex++, end - begin);
            statement.setInt(paramIndex, begin);

            // Add wines to list
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                Wine wine = new Wine(
                        set.getString("TITLE"),
                        set.getString("VARIETY"),
                        set.getString("COUNTRY"),
                        set.getString("WINERY"),
                        set.getString("DESCRIPTION"),
                        set.getInt("SCORE_PERCENT"),
                        set.getFloat("ABV"),
                        set.getFloat("PRICE")
                );
                wines.add(wine);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return wines;
    }


    /**
     * Generates a query for a preparedStatement
     *
     * @param filters A map of filter values and names
     * @return A statement formatted for a sql preparedStatement
     */
    public String generateFilteredSelectStatementString(Map<String, Object> filters) {
        StringBuilder query = new StringBuilder();
        query.append(
                "select TITLE, VARIETY, COUNTRY, WINERY, DESCRIPTION, SCORE_PERCENT, ABV, PRICE from WINE where 1=1");

        // Go through filter map and append conditions
        if (filters.containsKey("title")) {
            query.append(" and TITLE like ?");
        }

        if (filters.containsKey("country")) {
            query.append(" and COUNTRY like ?");
        }

        if (filters.containsKey("winery")) {
            query.append(" and WINERY like ?");
        }

        if (filters.containsKey("score")) {
            query.append(" and SCORE_PERCENT between ? and ?");
        }

        if (filters.containsKey("abv")) {
            query.append(" and ABV between ? and ?");
        }

        if (filters.containsKey("price")) {
            query.append(" and PRICE between ? and ?");
        }

        query.append(" order by ROWID limit ? offset ?;");

        return query.toString();
    }

    /**
     * Gets the number of wine records
     *
     * @return total number of wine records
     */
    public int getWinesSize() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery("select count(*) from WINE;");
            set.next();
            return set.getInt(1);
        }
    }


    /**
     * Replaces all wines in the database with a new list
     *
     * @param list list of wines
     */
    public void replaceAllWines(List<Wine> list) throws SQLException {
        String delete = "delete from WINE;";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(delete);
        }

        addWines(list);
    }

    /**
     * Adds the wines in the list to the database
     *
     * @param list list of wines
     */
    public void addWines(List<Wine> list) throws SQLException {
        // null key is auto generated
        String insert = "insert into WINE values(null, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement insertStatement = connection.prepareStatement(insert)) {
            for (Wine wine : list) {
                insertStatement.setString(1, wine.getTitle());
                insertStatement.setString(2, wine.getVariety());
                insertStatement.setString(3, wine.getCountry());
                insertStatement.setString(4, wine.getWinery());
                insertStatement.setString(5, wine.getDescription());
                insertStatement.setInt(6, wine.getScorePercent());
                insertStatement.setFloat(7, wine.getAbv());
                insertStatement.setFloat(8, wine.getPrice());
                insertStatement.executeUpdate();
            }

        }
    }


    /**
     * To disconnect/close database
     */
    @Override
    public void close() {
        try {
            this.connection.close();

            // Reset connection
            connection = null;

        } catch (SQLException e) {
            System.err.println("Error closing connection");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Checks if a table exists
     *
     * @param tableName name of table
     * @return if the table exists
     */
    private boolean tableExists(String tableName) {
        try {

            // Get database meta data
            DatabaseMetaData metaData = connection.getMetaData();

            // Strip tableName whitespace
            tableName = tableName.replaceAll("\\s+", "");

            // Query for table existence
            ResultSet tables = metaData.getTables(null, null, tableName, null);

            if (tables.next()) {
                tables.close();
                return true;
            }

            tables.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

}
