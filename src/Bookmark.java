/**
 * Created by chris on 1-12-13.
 */
public class Bookmark {

    private int id;
    private String folder;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bookmark() {
    }

    public Bookmark(String folder, String url) {
        this.folder = folder;
        this.url = url;
    }

}
