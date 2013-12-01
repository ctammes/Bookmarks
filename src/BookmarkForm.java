import nl.ctammes.common.MijnIni;
import nl.ctammes.common.MijnLog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;

/**
 * Created by chris on 1-12-13.
 */
public class BookmarkForm {
    protected JPanel mainPanel;
    private JTextField txtFilenaam;
    private JButton btnFileChooser;
    private JButton btnVerwerk;
    private JLabel lblFolder;
    private JLabel lblTitel;

    private static MijnIni ini = null;
    private static String inifile = "Bookmarks.ini";

    private static String dbDir = "/home/chris/IdeaProjects/java/Bookmarks";
    private static String dbNaam = "Bookmarks.db";
    private static String bookmarkFile = "/home//chris//IdeaProjects/java/Bookmarks/bookmarks.html";

    static Utility util;

    public BookmarkForm() {
        txtFilenaam.setText(bookmarkFile);

        btnFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(txtFilenaam.getText()).getParentFile());
                fc.setDialogTitle("Selecteer bookmark bestand");
                fc.setDialogType(JFileChooser.OPEN_DIALOG);
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    txtFilenaam.setText(fc.getSelectedFile().toString());
                    bookmarkFile= txtFilenaam.getText();
                } else {
                    util.getLog().info("No Selection ");
                }
            }
        });
        btnVerwerk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                util.verwerkBookmarkFile(txtFilenaam.getText().toString(), lblFolder, lblTitel);
            }
        });
    }

    public static void main(String[] args) {
        String logDir = ".";
        String logNaam = "Bookmarks.log";

        try {
            MijnLog mijnlog = new MijnLog(logDir, logNaam, true);
            util = Utility.getInstance();
            util.setLog(mijnlog.getLog());
            util.getLog().setLevel(Level.INFO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // inifile lezen of initieel vullen
        if (new File(inifile).exists()) {
            ini = new MijnIni(inifile);
            String dir = ini.lees("Algemeen", "dbdir");
            if (dir != null) {
                dbDir = dir;
            } else {
                if (new File(dbDir).exists()) {
                    ini.schrijf("Algemeen", "dbdir", dbDir);
                }
            }
            String naam = ini.lees("Algemeen", "dbnaam");
            if (naam != null) {
                dbNaam = naam;
            } else {
                ini.schrijf("Algemeen", "dbnaam", dbNaam);
            }
        } else {
            ini = new MijnIni(inifile);
            ini.schrijf("Algemeen", "dbdir", dbDir);
            ini.schrijf("Algemeen", "dbnaam", dbNaam);
            util.getLog().info("Inifile " + inifile + " aangemaakt en gevuld");
        }
        util.setDbBookmarkFolders(new BookmarkFoldersDb(dbDir, dbNaam));
        util.setDbBookmarks(new BookmarksDb(dbDir, dbNaam));


        JFrame frame = new JFrame("BookmarkForm");
        frame.setContentPane(new BookmarkForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100, 100);
        frame.pack();
        frame.setVisible(true);
    }
}
