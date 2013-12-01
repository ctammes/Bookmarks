import java.util.logging.Logger;

/**
 * Created by chris on 1-12-13.
 */
public class Utility {
    private static Utility ourInstance = new Utility();

    public static Utility getInstance() {
        return ourInstance;
    }

    // Database
    private BookmarkFoldersDb dbBookmarkFolders;
    private BookmarksDb dbBookmarks;

    // Initialiseer logger
    private Logger log = Logger.getLogger(BookmarkForm.class.getName());

    private Utility() {
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public Logger getLog() {
        return log;
    }

    public BookmarkFoldersDb getDbBookmarkFolders() {
        return dbBookmarkFolders;
    }

    public void setDbBookmarkFolders(BookmarkFoldersDb dbBookmarkFolders) {
        this.dbBookmarkFolders = dbBookmarkFolders;
    }

    public BookmarksDb getDbBookmarks() {
        return dbBookmarks;
    }

    public void setDbBookmarks(BookmarksDb dbBookmarks) {
        this.dbBookmarks = dbBookmarks;
    }
}
