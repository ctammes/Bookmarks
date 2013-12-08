import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chris on 1-12-13.
 */
public class BookmarkFormTest {
    static Utility util;
    ArrayList<String> alVerwerkt = null;
    String tabs = "";

    @Before
    public void setUp() throws Exception {
        util = Utility.getInstance();
        alVerwerkt = new ArrayList<String>();
    }

    @Test
    public void testFolder() throws Exception {
        String html = "<DT><H3 ADD_DATE=\"1357499674\" LAST_MODIFIED=\"1379660728\" PERSONAL_TOOLBAR_FOLDER=\"true\">Bladwijzerbalk</H3>";
        Pattern pat = Pattern.compile("<DT><H\\d[^>]+>([^<]+)<.+>");
        Matcher mat = pat.matcher(html);
        int start = 0;
        while (mat.find(start)) {
            System.out.println(mat.group(1));
            start = mat.toMatchResult().end(1);
        }
    }

    @Test
    public void testUrl() throws Exception {
        String html = "<DT><A HREF=\"http://soapx005-hhrherhaalontwikkel.microbais.lan:8095/\" ADD_DATE=\"1363595657\">soapx005 - FrontPage</A>";
        Pattern pat = Pattern.compile("<DT><A HREF=\\\"([^\\\"]+)\\\"[^>]+>([^<]+)<");
        Matcher mat = pat.matcher(html);
        int start = 0;
        while (mat.find(start)) {
            for (int i = 1; i <= mat.groupCount(); i++) {
                System.out.println(mat.group(i));
            }
            start = mat.toMatchResult().end(1);
        }
    }

    @Test
    public void testVerwerkFile() throws Exception {
        String bookmarkFile = "/home//chris//IdeaProjects/java/Bookmarks/bookmarks_01-11-13.html";
        Pattern patFolder = Pattern.compile("<DT><H\\d[^>]+>([^<]+)<.+>");
        Pattern patUrl = Pattern.compile("<DT><A HREF=\\\"([^\\\"]+)\\\"[^>]+>([^<]+)<");

        File f = new File(bookmarkFile);
        if (f.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String regel;
            boolean skip = true;
            int teller = 0;
            String tabs = "";
            while ((regel = br.readLine()) != null) {
                regel = regel.trim();
                if (skip) {
                    if (regel.equals("<H1>Bookmarks</H1>")) {
                        skip = false;
                    }
                } else {
                    if (regel.toUpperCase().startsWith("<DL>")) {
                        tabs += "\t";
                    } else if (regel.toUpperCase().startsWith("</DL>")) {
                        tabs = tabs.substring(0,tabs.length()-1);
                    } else if (regel.toUpperCase().startsWith("<DT><H")) {
                        Matcher mat = patFolder.matcher(regel);
                        int start = 0;
                        while (mat.find(start)) {
                            System.out.println(tabs + "Folder: "+ mat.group(1));
                            start = mat.toMatchResult().end(1);
                        }
                        teller++;
                    } else if (regel.toUpperCase().startsWith("<DT><A")) {
                        Matcher mat = patUrl.matcher(regel);
                        int start = 0;
                        while (mat.find(start)) {
                            System.out.println(tabs + "\tUrl: " + mat.group(2) + " - " + mat.group(1));
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
    }

    @Test
    public void testSchrijfFile() throws Exception {
        String file = "/home//chris//IdeaProjects/java/Bookmarks/bookmarks_test.html";
        util.schrijfBookmarkFile(file);

    }

    @Test
    public void testLeesBookmarks() throws Exception {
        String dbDir = "/home/chris/IdeaProjects/java/Bookmarks";
        String dbNaam = "Bookmarks_01-11-13.db";
        util.setDbBookmarks(new BookmarksDb(dbDir, dbNaam));

//        Bookmark bm = null;
//        String parent = "";
//        for (int i=1; i<20; i++) {
//            bm = util.getDbBookmarks().leesBookmark(i);
//            if (!parent.equals(bm.getParentfolder())) {
//                parent = bm.getParentfolder();
//                System.out.println(parent);
//            }
//            System.out.printf("\t%s/%s: %s\n", bm.getFolder(), bm.getTitel(), bm.getUrl());
//        }


        String parent = "";
        ResultSet rst = util.getDbBookmarks().leesBookmarkLijst();
        int i=0;
        while (rst.next()) {
            if (!parent.equals(rst.getString("parentfolder"))) {
                parent = rst.getString("parentfolder");
                System.out.println(parent);
            }
            System.out.printf("\t%s/%s: %s\n", rst.getString("folder"), rst.getString("titel"), rst.getString("url"));
            if (i++ > 20) { break; }
        }
    }

    @Test
    public void testLees1() throws Exception {
        String dbDir = "/home/chris/IdeaProjects/java/Bookmarks";
        String dbNaam = "Bookmarks_01-11-13.db";
        util.setDbBookmarks(new BookmarksDb(dbDir, dbNaam));

        String parent = "";
        String tabs = "";
        ResultSet parents = util.getDbBookmarks().leesParentIdLijst();
        int i = 0;
        while (parents.next()) {
            int parent_id = parents.getInt("parent_id");
            ResultSet bookmarks = util.getDbBookmarks().leesBookmarkLijstByParentId(parent_id);
            if (bookmarks != null) {
                // schrijf bookmarks
                while (bookmarks.next()) {
                    if (!parent.equals(bookmarks.getString("parentfolder"))) {
                        parent = bookmarks.getString("parentfolder");
                        System.out.println(tabs + parent);
                        tabs += "\t";
                    }
                    System.out.printf(tabs + "%s/%s: %s\n", bookmarks.getString("folder"), bookmarks.getString("titel"), bookmarks.getString("url"));
                }
                if (tabs.length() > 0) {
                    tabs = tabs.substring(0, tabs.length()-1);
                }
            }
            if (i++ > 20) { break; }
        }


    }

    @Test
    public void testLees2() throws Exception {
        String dbDir = "/home/chris/IdeaProjects/java/Bookmarks";
        String dbNaam = "Bookmarks_01-11-13.db";
        util.setDbBookmarks(new BookmarksDb(dbDir, dbNaam));

        String parent = "Bookmarks";
        verwerkFolder1(parent);

/*
        String tabs = "";
        System.out.println(tabs + parent);
        // Lees folders onder parent
        ResultSet folders = util.getDbBookmarks().leesFolderLijst(parent);
        int i = 0;
        if (folders != null) {
            while (folders.next()) {
                String folder = folders.getString("folder");
                System.out.println(tabs + parent);
                toonBookmarks(parent, folder);
                if (tabs.length() > 0) {
                    tabs = tabs.substring(0, tabs.length() - 1);
                }
            }
//            if (i++ > 20) { break; }
        }
*/


    }

    private void toonBookmarks(String parentfolder, String folder) {
        try {
            ResultSet bookmarks = util.getDbBookmarks().leesBookmarkLijstByParentFolder(parentfolder, folder);
            while (bookmarks.next()) {
                if (!parentfolder.equals(bookmarks.getString("parentfolder"))) {
                    parentfolder = bookmarks.getString("parentfolder");
                    System.out.println(parentfolder);
//                    System.out.println(tabs + parent);
                    //tabs += "\t";
                }
//                System.out.printf(tabs + "%s/%s: %s\n", bookmarks.getString("folder"), bookmarks.getString("titel"), bookmarks.getString("url"));
                System.out.printf("%s/%s: %s\n", bookmarks.getString("folder"), bookmarks.getString("titel"), bookmarks.getString("url"));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Recursieve functie om alle bookmarks onder een bepaalde folder te tonen
     * Globals: ArrayList<String> alVerwerkt en String tabs
     * @param parentfolder
     */
    private void verwerkFolder(String parentfolder) {
        String folder = "";

        // TODO worden alle onbekende entries in oude folders ook verwerkt?
        ResultSet folders = util.getDbBookmarks().leesFolderLijst(parentfolder);
        try {
            while (folders.next()) {
                folder = folders.getString("folder");
                if (!alVerwerkt.contains(folder)) {
                    alVerwerkt.add(folder);
                    System.out.println(tabs + "Folder: " + folder);
                    tabs += "\t";
                    ResultSet bookmarks = util.getDbBookmarks().leesBookmarkLijstByParentFolder(parentfolder, folder);
                    while (bookmarks.next()) {
//                        System.out.printf(tabs + "%s/%s: %s\n", bookmarks.getString("folder"), bookmarks.getString("titel"), bookmarks.getString("url"));
                        System.out.printf(tabs + "%s: %s\n", bookmarks.getString("titel"), bookmarks.getString("url"));
                    }
                    verwerkFolder(folder);
                }
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        if (tabs.length() > 0) {
            tabs = tabs.substring(0, tabs.length() - 1);
        }
        return;
    }


    private void verwerkFolder1(String parentfolder) {
        String folderBegin = "<DL><p>\n";
        String folderEinde = "</DL><p>\n";
        String bookmarkFolder = "<DT><H3 ADD_DATE=\"1357499674\" LAST_MODIFIED=\"1379660728\" PERSONAL_TOOLBAR_FOLDER=\"true\">%s</H3>\n";
        String bookmarkEntry = "<DT><A HREF=\"%s\" ADD_DATE=\"1357203142\" ICON=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADD0lEQVQ4jTWTTW9UVQBAz73vzps305kOnZkCpQGLSBUQAjUpwVQXNMYFITEhdu9KV267cqMbXLl040oTWZAY3FQSjeJ3RpsawbSiRYu2DmVmmO/33tz37r0uqucfnOQcYYzZjC0TnTjFOQtOAuB5AiHBGtAWdGpwOJwTCKA65rPPpy3aw1Ff5P2CTgAHuQzEKdS7Cd1Yo4RkshhQzAlCDQgYjiCbhX1WD1SYGp1JwOiUjCf5aqPFrbsNttsRkTZY61BS8OzxKi+fP8JOOyU1jqlKjvYo1co6EA6UJ7lW+4ubvzzAVxLfk/ieoDNKCZRk9mCRPxojogRmD2SxgHMgAfI+3LxT58baDrmMh+9JnINOmDBTyfPWldPk/Dw7bc1MRRHpFLFnjPSkYLdv+XhtB19JrHXoxNIajJg/Wmb58in+bBp+3w1ZmB3n6vVVVla3yAeAAekrj9vbXeqdGAlEOqUbal6am+a1xePU7g3ZfBhx6VyVDz6/y0pti9r6P+gUnAAFju1HIdpYeqOEjJS8evEYF2YP8MmdDg+7mqUL+/n0p/u8/9k6pZyi3hzQDQ2BB8ohcA7aQ80ThQLLl04wXR7no9UWrb7myvx+fv27yTs3fkYJiK3FWotzApxAGguVQpanDhZ5e+ks47kxrv/QoP4oZvH0BMM44s0Pf0TrBKwjihImS3nGcpLUOFQvSjn7WJm5I3PcbyZ8ubFLGKc8f3KCasHj9Xe/odEeEHiSJEkJ4xELZw7jSbAOlAHyOcm360Nqv3VIEsO5x0ucOTrO8ntfs7HVpBgoEp3Q6Ax5ZnaKF+ePMYwcDotEQKNnuHW7QasTcrga8NzJMlev1fhibQvfg24/pN7sc2pmkjdeWUApSO3eG8qkjiArefJQHmMdl89P8d36Divf3yPwJCYxHKoUufjC0ywtnmAsyBDGCSobIAGx3er1e36xIPY+IbUw0oZeOKIziPGVx1SlQKngEcaO1FgAMsqjYvsDYbTebETJxIN+jHOO//GVxBMeDoc2BmMcQvyXLzBdylHOZ9v/Aunni+p4XFn6AAAAAElFTkSuQmCC\">%s</A>\n";

        String folder = "";

        // TODO worden alle onbekende entries in oude folders ook verwerkt?
        ResultSet folders = util.getDbBookmarks().leesFolderLijst(parentfolder);
        try {
            while (folders.next()) {
                folder = folders.getString("folder");
                if (!alVerwerkt.contains(folder)) {
                    alVerwerkt.add(folder);
                    System.out.print(tabs + String.format(bookmarkFolder, folder) + tabs + folderBegin);
                    tabs += "\t";
                    ResultSet bookmarks = util.getDbBookmarks().leesBookmarkLijstByParentFolder(parentfolder, folder);
                    while (bookmarks.next()) {
                        System.out.print(tabs + String.format(bookmarkEntry, bookmarks.getString("titel"), bookmarks.getString("url")));
                    }
                    verwerkFolder1(folder);
                }
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        if (tabs.length() > 0) {
            tabs = tabs.substring(0, tabs.length() - 1);
        }
        System.out.print(tabs + folderEinde);
        return;
    }


}
