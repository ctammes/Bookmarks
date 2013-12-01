import nl.ctammes.common.Sqlite;

/**
 * Created by chris on 1-12-13.
 */
public class BookmarksDb extends Sqlite {

    private int id;
    private int folder_id;
    private String titel;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFolder_id() {
        return folder_id;
    }

    public void setFolder_id(int folder_id) {
        this.folder_id = folder_id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BookmarksDb(String dir, String db) {
        super(dir, db);
        openDb();
        createBookmark();
    }

    public void sluitDb() {
        super.sluitDb();
    }

    /**
     * Maak nieuwe database
     * @return
     */
    public boolean createBookmark() {
        String sql = "CREATE TABLE IF NOT EXISTS bookmarks (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "    folder_id INTEGER NOT NULL, " +
                "    titel TEXT NOT NULL, " +
                "    url TEXT NOT NULL" +
                ");";
        return executeNoResult(sql);
    }

    public boolean schrijfBookmark(int folderId, String titel, String url) {

        titel = titel.replaceAll("'", "''");
        url = url.replaceAll("'", "''");
        String values = String.format("'%d', '%s', '%s'",
                folderId, titel, url);
        String sql = "insert into bookmarks" +
                " (folder_id, titel, url)" +
                " values (" + values + ")";
        executeNoResult(sql);
        return false;

    }


}
