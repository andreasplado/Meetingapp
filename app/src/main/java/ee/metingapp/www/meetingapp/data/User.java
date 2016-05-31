package ee.metingapp.www.meetingapp.data;

/**
 * Created by Andreas on 16.12.2015.
 */
public class User {

    private static String id;
    private static String email;
    private static String name;
    private static String age;
    private static String gender;
    private static String interestedIn;
    private static User dataObject = null;
    private static String birthdate;
    private static String radius;
    private static double longitude;
    private static double latitude;



    public static User getInstance() {
        if (dataObject == null)
            dataObject = new User();
        return dataObject;
    }

    public static String getAge() {
        return age;
    }

    public static void setAge(String age) {
        User.age = age;
    }


    public static String getInterestedIn() {
        return interestedIn;
    }

    public static void setInterestedIn(String interestedIn) {
        User.interestedIn = interestedIn;
    }

    public static String getGender() {
        return gender;
    }

    public static void setGender(String gender) {
        User.gender = gender;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        User.id = id;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }

    public static String getBirthdate() {
        return birthdate;
    }

    public static void setBirthdate(String birthdate) {
        User.birthdate = birthdate;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static String getRadius() {
        return radius;
    }

    public static void setRadius(String radius) {
        User.radius = radius;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        User.longitude = longitude;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        User.latitude = latitude;
    }
}
