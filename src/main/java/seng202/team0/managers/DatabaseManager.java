package seng202.team0.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team0.database.Wine;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.Map;
import seng202.team0.util.Filters;


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
        String create = "create table if not exists WINE (" +
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
            set.close();

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
    public ObservableList<Wine> getWinesInRange(int begin, int end, Filters filters) {
        ObservableList<Wine> wines = FXCollections.observableArrayList();
        String query = "select TITLE, VARIETY, COUNTRY, WINERY, DESCRIPTION, SCORE_PERCENT, ABV, PRICE "
            + "from WINE "
            + "where TITLE like ? "
            + "and COUNTRY like ? "
            + "and WINERY like ? "
            + "and SCORE_PERCENT between ? and ? "
            + "and ABV between ? and ? "
            + "and PRICE between ? and ? "
            + "order by ROWID "
            + "limit ? "
            + "offset ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int paramIndex = 1;
            statement.setString(paramIndex++, filters.getTitle().isEmpty() ? "%" : "%" + filters.getTitle() + "%");
            statement.setString(paramIndex++, filters.getCountry().isEmpty() ? "%" : "%" + filters.getCountry());
            statement.setString(paramIndex++, filters.getWinery().isEmpty() ? "%" : "%" + filters.getWinery() + "%");
            statement.setDouble(paramIndex++, filters.getMinScore());
            statement.setDouble(paramIndex++, filters.getMaxScore());
            statement.setDouble(paramIndex++, filters.getMinAbv());
            statement.setDouble(paramIndex++, filters.getMaxAbv());
            statement.setDouble(paramIndex++, filters.getMinPrice());
            statement.setDouble(paramIndex++, filters.getMaxPrice());
            statement.setInt(paramIndex++, end - begin);
            statement.setInt(paramIndex, begin);

            // Add filtered wines to list
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
            set.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return wines;
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
}
