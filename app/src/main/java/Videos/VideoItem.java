// VideoItem.java
package Videos;

public class VideoItem {
    private final int imageResourceId;
    private final String title;
    private final int videoResourceId;

    public VideoItem(int imageResourceId, String title, int videoResourceId) {
        this.imageResourceId = imageResourceId;
        this.title = title;
        this.videoResourceId = videoResourceId;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public int getVideoResourceId() {
        return videoResourceId;
    }
}
