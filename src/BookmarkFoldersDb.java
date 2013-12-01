import nl.ctammes.common.Sqlite;

import java.sql.ResultSet;

/**
 * Created by chris on 1-12-13.
 */
public class BookmarkFoldersDb extends Sqlite{

    private int id;
    private String titel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public BookmarkFoldersDb(String dir, String db) {
        super(dir, db);
        openDb();
        createQuery();
    }

    public void sluitDb() {
        super.sluitDb();
    }

    /**
     * Maak nieuwe database
     * @return
     */
    public boolean createQuery() {
        String sql = "CREATE TABLE IF NOT EXISTS bookmarkfolders (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "    titel TEXT NOT NULL" +
                ");";
        return executeNoResult(sql);
    }

    /**
     * Maak nieuwe folder
     * @param titel
     * @return
     */
    public boolean schrijfBookmarkFolder(String titel) {

        String sql = "insert into bookmarkfolders" +
                " (titel)" +
                " values (" + titel + ")";
        executeNoResult(sql);
        return false;

    }

    /**
     * Geef id van folder
     * @param titel
     * @return
     */
    public int getBookmarkFolderId(String titel) {
        String sql = "select id from  bookmarkfolders" +
                " where titel = '" + titel + "';";
        ResultSet rst = execute(sql);

        int result = -1;
        try {
            while (rst.next()) {
                result = rst.getInt("id");
            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;


    }
}
