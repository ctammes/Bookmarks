/**
 * Created by chris on 1-12-13.
 */
public class BookmarkFolder {

    private int id;
    private int parent_id;
    private String titel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public BookmarkFolder(String titel) {
        this.titel = titel;
    }
}
