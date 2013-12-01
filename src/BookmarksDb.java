import nl.ctammes.common.Sqlite;

/**
 * Created by chris on 1-12-13.
 */
public class BookmarksDb extends Sqlite {

    private int id;
    private int folder_id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BookmarksDb(String dir, String db) {
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
        String sql = "CREATE TABLE IF NOT EXISTS bookmarks (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "    url INTEGER NOT NULL, " +
                "    url TEXT NOT NULL" +
                ");";
        return executeNoResult(sql);
    }

    public boolean schrijfBookmark(Bookmark bookmark) {

        // TODO folderId aanpassen!!
        int folderId = 1;
        String url = bookmark.getUrl().replaceAll("'", "''");
        String values = String.format("'%d', '%s'",
                folderId, url);
        String sql = "insert into bookmarks" +
                " (folder_id, url)" +
                " values (" + values + ")";
        executeNoResult(sql);
        return false;

    }


}
