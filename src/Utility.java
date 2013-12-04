import javax.swing.*;
import java.io.*;
import java.sql.ResultSet;
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
                                folder.repaint();
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
                                titel.repaint();
                                start = mat.toMatchResult().end(1);
                            }
                            teller++;
                        }
                    }
                    if (teller > 100) {
                        break;
                    }
                }
                br.close();
            }

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Schrijf gegevens uit de database naar een nieuwe bookmark file
     * @param filenaam
     */
    protected void schrijfBookmarkFile(String filenaam) {
        schrijfBookmarkFile(filenaam, 0);
    }

    protected void schrijfBookmarkFile(String filenaam, int aantalRecords) {
        String tabs = "";
        String bookmarkHeader = "<!DOCTYPE NETSCAPE-Bookmark-file-1>\n" +
                "<!-- This is an automatically generated file.\n" +
                "     It will be read and overwritten.\n" +
                "     DO NOT EDIT! -->\n" +
                "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n" +
                "<TITLE>Bookmarks</TITLE>\n" +
                "<H1>Bookmarks</H1>\n" +
                "<DL><p>\n";
        String folderBegin = "<DL><p>\n";
        String folderEinde = "</DL><p>\n";
        String bookmarkFolder = "<DT><H3 ADD_DATE=\"1357499674\" LAST_MODIFIED=\"1379660728\" PERSONAL_TOOLBAR_FOLDER=\"true\">%s</H3>\n";
        String bookmarkEntry = "<DT><A HREF=\"%s\" ADD_DATE=\"1357203142\" ICON=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADD0lEQVQ4jTWTTW9UVQBAz73vzps305kOnZkCpQGLSBUQAjUpwVQXNMYFITEhdu9KV267cqMbXLl040oTWZAY3FQSjeJ3RpsawbSiRYu2DmVmmO/33tz37r0uqucfnOQcYYzZjC0TnTjFOQtOAuB5AiHBGtAWdGpwOJwTCKA65rPPpy3aw1Ff5P2CTgAHuQzEKdS7Cd1Yo4RkshhQzAlCDQgYjiCbhX1WD1SYGp1JwOiUjCf5aqPFrbsNttsRkTZY61BS8OzxKi+fP8JOOyU1jqlKjvYo1co6EA6UJ7lW+4ubvzzAVxLfk/ieoDNKCZRk9mCRPxojogRmD2SxgHMgAfI+3LxT58baDrmMh+9JnINOmDBTyfPWldPk/Dw7bc1MRRHpFLFnjPSkYLdv+XhtB19JrHXoxNIajJg/Wmb58in+bBp+3w1ZmB3n6vVVVla3yAeAAekrj9vbXeqdGAlEOqUbal6am+a1xePU7g3ZfBhx6VyVDz6/y0pti9r6P+gUnAAFju1HIdpYeqOEjJS8evEYF2YP8MmdDg+7mqUL+/n0p/u8/9k6pZyi3hzQDQ2BB8ohcA7aQ80ThQLLl04wXR7no9UWrb7myvx+fv27yTs3fkYJiK3FWotzApxAGguVQpanDhZ5e+ks47kxrv/QoP4oZvH0BMM44s0Pf0TrBKwjihImS3nGcpLUOFQvSjn7WJm5I3PcbyZ8ubFLGKc8f3KCasHj9Xe/odEeEHiSJEkJ4xELZw7jSbAOlAHyOcm360Nqv3VIEsO5x0ucOTrO8ntfs7HVpBgoEp3Q6Ax5ZnaKF+ePMYwcDotEQKNnuHW7QasTcrga8NzJMlev1fhibQvfg24/pN7sc2pmkjdeWUApSO3eG8qkjiArefJQHmMdl89P8d36Divf3yPwJCYxHKoUufjC0ywtnmAsyBDGCSobIAGx3er1e36xIPY+IbUw0oZeOKIziPGVx1SlQKngEcaO1FgAMsqjYvsDYbTebETJxIN+jHOO//GVxBMeDoc2BmMcQvyXLzBdylHOZ9v/Aunni+p4XFn6AAAAAElFTkSuQmCC\">%s</A>\n";

        try {
            File f = new File(filenaam);
            if (f.exists()) {
                String msg = "Bestand " + filenaam + " bestaat al. Overschrijven?";
                if (JOptionPane.showConfirmDialog(null, msg, "Bevestig", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            String regel;
            bw.write(bookmarkHeader);

            String parent = "";
            String folder = "";
            ResultSet rst = getDbBookmarks().leesBookmarkLijst();
            int i=0;
            try {
                while (rst.next()) {
                    if (!folder.equals(rst.getString("folder"))) {
                        if (folder.equals("")) {
                            bw.write("<H1>Bookmarks</H1>\n<DL><p>\n");
                            tabs += "\t";
                        } else {
                            tabs = tabs.substring(0,tabs.length()-1);
                            bw.write(tabs + folderEinde);
                        }
                        parent = rst.getString("parentfolder");
                        folder = rst.getString("folder");
                        System.out.println(parent);
                        bw.write(tabs + String.format(bookmarkFolder, rst.getString("folder")) + tabs + folderBegin);
                        tabs += "\t";
                    }
                    System.out.printf("\t%s/%s: %s\n", rst.getString("folder"), rst.getString("titel"), rst.getString("url"));
                    bw.write(tabs + String.format(bookmarkEntry, rst.getString("url"), rst.getString("titel")));
                    if (aantalRecords > 0) {
                        if (i++ > aantalRecords) { break; }
                    }
                }
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }

            bw.write(tabs + folderEinde);
            bw.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

}

