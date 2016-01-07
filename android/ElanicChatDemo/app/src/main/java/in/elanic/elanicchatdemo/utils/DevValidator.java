package in.elanic.elanicchatdemo.utils;

/**
 * Created by Jay Rambhia on 07/01/16.
 */
public class DevValidator {

    public static boolean checkString(String string, String fieldName) {
        if (string == null) {
            throw new NullPointerException(fieldName + " is null");
        }

        if (string.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is empty");
        }

        return true;
    }

}
