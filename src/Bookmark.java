/**
 * Created by chris on 1-12-13.
 */
public class Bookmark {

    private int id;
    private String parentfolder;
    private String folder;
    private String titel;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParentfolder() {
        return parentfolder;
    }

    public void setParentfolder(String parentfolder) {
        this.parentfolder = parentfolder;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
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

    public Bookmark() {
    }

    public Bookmark(String parentfolder, String folder, String titel, String url) {
        this.parentfolder = parentfolder;
        this.folder = folder;
        this.titel = titel;
        this.url = url;
    }

}
