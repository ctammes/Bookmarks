import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Verwerk de bookmarks in het bestand
     * @param filenaam
     */
    protected void verwerkBookmarkFile(String filenaam, JLabel folder, JLabel titel) {
        // Pattern voor folders
        Pattern patFolder = Pattern.compile("<DT><H\\d[^>]+>([^<]+)<.+>");
        // Pattern voor de url + naam
        Pattern patUrl = Pattern.compile("<DT><A HREF=\\\"([^\\\"]+)\\\"[^>]+>([^<]+)<");
        // Huidige folder
        int currentFolderId = 0;

        try {
            File f = new File(filenaam);
            if (f.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String regel;
                boolean skip = true;
                int teller = 0;
                String tabs = "";
                while ((regel = br.readLine()) != null) {
                    regel = regel.trim();

                    // Eerste regels overslaan
                    if (skip) {
                        if (regel.equals("<H1>Bookmarks</H1>")) {
                            currentFolderId = dbBookmarkFolders.schrijfBookmarkFolder(currentFolderId, "Bookmarks");
                            folder.setText("Bookmarks");
                            skip = false;
                        }
                    } else {
                        if (regel.toUpperCase().startsWith("<DL>")) {
                            // Hier geen code
                        } else if (regel.toUpperCase().startsWith("</DL>")) {
                            // Haal parent_id
                            currentFolderId = dbBookmarkFolders.getParentId(currentFolderId);
                        } else if (regel.toUpperCase().startsWith("<DT><H")) {
                            Matcher mat = patFolder.matcher(regel);
                            int start = 0;
                            while (mat.find(start)) {
//                                System.out.println(tabs + "Folder: "+ mat.group(1));
                                currentFolderId = dbBookmarkFolders.schrijfBookmarkFolder(currentFolderId, mat.group(1));
                                folder.setText(mat.group(1));
                                start = mat.toMatchResult().end(1);
                            }
                            teller++;
                        } else if (regel.toUpperCase().startsWith("<DT><A")) {
                            Matcher mat = patUrl.matcher(regel);
                            int start = 0;
                            while (mat.find(start)) {
//                                System.out.println(tabs + "\tUrl: " + mat.group(2) + " - " + mat.group(1));
                                dbBookmarks.schrijfBookmark(currentFolderId, mat.group(2), mat.group(1));
                                titel.setText(mat.group(1));
                                start = mat.toMatchResult().end(1);
                            }
                            teller++;
                        }
                    }
                    if (teller > 100) {
                        break;
                    }
                }
            }

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
