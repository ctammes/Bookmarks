import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chris on 1-12-13.
 */
public class BookmarkFormTest {
    static Utility util;

    @Before
    public void setUp() throws Exception {
        util = Utility.getInstance();
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

}
