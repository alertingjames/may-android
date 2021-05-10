package bookclub.technion.maymsgphoto.user;

/**
 * Created by a on 5/20/2017.
 */

public class Session {

    private static String passcode = null;

    public static String getPasscode() {
        return passcode;
    }

    public static void setPasscode(String passcode) {
        Session.passcode = passcode;
    }
}

