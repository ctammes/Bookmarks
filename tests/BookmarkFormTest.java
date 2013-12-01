import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chris on 1-12-13.
 */
public class BookmarkFormTest {
    @Before
    public void setUp() throws Exception {

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
}
