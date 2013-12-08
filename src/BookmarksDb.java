import nl.ctammes.common.Sqlite;

import java.sql.ResultSet;

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

    public Bookmark leesBookmark(int id) {
        String sql = "select f2.titel titel1, f1.titel titel2, b.titel titel3, b.url from bookmarks b " +
                "join bookmarkfolders f1 on b.folder_id = f1.id " +
                "join bookmarkfolders f2 on f1.parent_id = f2.id " +
                "where b.id = '" + id + "';";
        ResultSet rst = execute(sql);

        Bookmark result = null;
        try {
            while (rst.next()) {
                result = new Bookmark(rst.getString("titel1"), rst.getString("titel2"), rst.getString("titel3"), rst.getString("url"));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;

    }

    public ResultSet leesBookmarkLijst() {
        String sql = "select distinct f2.titel parentfolder, f1.titel folder, b.titel titel, b.url from bookmarks b " +
                "join bookmarkfolders f1 on b.folder_id = f1.id " +
                "join bookmarkfolders f2 on f1.parent_id = f2.id " +
                "order by f2.id;";
        ResultSet rst = execute(sql);

//        Bookmark result = null;
        ResultSet result = null;
        try {
            result = rst;
//            while (rst.next()) {
//                result = new Bookmark(rst.getString("parentfolder"), rst.getString("folder"), rst.getString("titel3"), rst.getString("url"));
//            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;
    }

    /**
     * Stap 1: lijst van alle parent_id's
     * @return
     */
    public ResultSet leesParentIdLijst() {
        String sql = "select distinct f2.id parent_id\n" +
                "    from bookmarkfolders f1\n" +
                "    join bookmarkfolders f2 on f1.parent_id = f2.id\n" +
                "    order by f2.id;";
        ResultSet rst = execute(sql);

        ResultSet result = null;
        try {
            result = rst;
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;
    }

    /**
     * Stap 2: lees gegevens voor de opgegeven parent_id - kan leeg zijn
     * @param parent_id
     * @return
     */
    public ResultSet leesBookmarkLijstByParentId(int parent_id) {
        String sql = "select distinct f2.titel parentfolder, f1.titel folder, b.titel titel, b.url from bookmarks b " +
                "join bookmarkfolders f1 on b.folder_id = f1.id " +
                "join bookmarkfolders f2 on f1.parent_id = f2.id " +
                "where f2.id = " + parent_id ;
        ResultSet rst = execute(sql);

        ResultSet result = null;
        try {
            result = rst;
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;
    }

    /**
     * Lees bookmarks achter de parentfolder
     * @param parentfolder
     * @return
     */
    public ResultSet leesBookmarkLijstByParentFolder(String parentfolder, String folder) {
        String sql = "select distinct f2.titel parentfolder, f1.titel folder, b.titel titel, b.url from bookmarks b " +
                "join bookmarkfolders f1 on b.folder_id = f1.id " +
                "join bookmarkfolders f2 on f1.parent_id = f2.id " +
                "where f2.titel = '" + parentfolder + "' and f1.titel = '" + folder + "'" ;
        ResultSet rst = execute(sql);

        ResultSet result = null;
        try {
            result = rst;
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;
    }

    /**
     * Lees folders achter de parentfolder
     * @param parentfolder
     * @return
     */
    public ResultSet leesFolderLijst(String parentfolder) {
        String sql = "select distinct f1.titel folder from bookmarks b\n" +
                "    join bookmarkfolders f1 on b.folder_id = f1.id\n" +
                "    join bookmarkfolders f2 on f1.parent_id = f2.id\n" +
                "    where f2.titel = '" + parentfolder + "'\n" +
                "    order by f2.titel;";
        ResultSet rst = execute(sql);

        ResultSet result = null;
        try {
            result = rst;
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;
    }


}
