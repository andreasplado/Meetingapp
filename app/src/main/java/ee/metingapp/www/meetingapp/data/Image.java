package ee.metingapp.www.meetingapp.data;

/**
 * Created by Andreas on 16.12.2015.
 */
public class Image {

    private static String filePath;
    private static boolean isImage;
    private static Image dataObject = null;

    public static Image getInstance() {
        if (dataObject == null)
            dataObject = new Image();
        return dataObject;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        Image.filePath = filePath;
    }

    public static boolean isImage() {
        return isImage;
    }

    public static void setIsImage(boolean isImage) {
        Image.isImage = isImage;
    }
}
