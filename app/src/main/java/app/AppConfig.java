package app;

/**
 * Created by Andreas on 05.12.2015.
 */
public class AppConfig {
    // Server user login url
    public static String MAIN_URL = "http://phonewe.freeiz.com/suhtlus/";
    public static String URL_LOGIN = MAIN_URL + "login.php";
    public static String URL_REGISTER = MAIN_URL + "register.php";
    public static final String FILE_UPLOAD_URL = MAIN_URL + "fileUpload.php";
    public static final String IMAGE_URL = MAIN_URL + "uploads/";
    public static String URL_UPDATE_USER = MAIN_URL + "updateUser.php";
    public static String FILE_UPLOAD_URL_VOLLEY = MAIN_URL + "fileUploadVolley.php";
    public static String URL_LIKE_USER = MAIN_URL + "likeUser.php";
    public static String URL_UPDATE_USER_LOCATION = MAIN_URL + "updateUserLocation.php";
    public static String URL_GET_USERS_LOCATION = MAIN_URL + "getUsersByLocation.php";
    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    public static String SESSION_ID = "";

}
