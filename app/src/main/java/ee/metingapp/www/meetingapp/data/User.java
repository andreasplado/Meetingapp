package ee.metingapp.www.meetingapp.data;

/**
 * Created by Andreas on 16.12.2015.
 */
public class User {

    private static String id;
    private static String name;
    private static String age;
    private static String gender;
    private static String interestedIn;
    private static User dataObject = null;

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
}
