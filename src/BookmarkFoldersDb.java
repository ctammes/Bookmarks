import nl.ctammes.common.Sqlite;

import java.sql.ResultSet;

/**
 * Created by chris on 1-12-13.
 */
public class BookmarkFoldersDb extends Sqlite{

    private int id;
    private int parent_id;
    private String titel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
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
        createBookmarkFolder();
    }

    public void sluitDb() {
        super.sluitDb();
    }

    /**
     * Maak nieuwe database
     * @return
     */
    public boolean createBookmarkFolder() {
        String sql = "CREATE TABLE IF NOT EXISTS bookmarkfolders (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "    parent_id INTEGER NULL DEFAULT NULL," +
                "    titel TEXT NOT NULL" +
                ");";
        return executeNoResult(sql);
    }

    /**
     * Maak nieuwe folder
     * @param titel
     * @return
     */
    public int schrijfBookmarkFolder(int parentId, String titel) {

        String values = String.format("'%d', '%s'",
                parentId, titel);
        String sql = "insert into bookmarkfolders" +
                " (parent_id, titel)" +
                " values (" + values + ")";
        executeNoResult(sql);
        return getMaxId();

    }

    private int getMaxId() {
        String sql = "select max(id) max_id from  bookmarkfolders;";
        ResultSet rst = execute(sql);

        int result = -1;
        try {
            while (rst.next()) {
                result = rst.getInt("max_id");
            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;

    }

    /**
     * Geef id van folder
     * @param titel
     * @return
     */
    public int getBookmarkFolderByTitel(String titel) {
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

    /**
     * Geef de parent_id
     * @param id
     * @return
     */
    public int getParentId(int id) {
        String sql = "select parent_id from  bookmarkfolders" +
                " where id = " + id + ";";
        ResultSet rst = execute(sql);

        int result = -1;
        try {
            while (rst.next()) {
                result = rst.getInt("parent_id");
            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;


    }

}
