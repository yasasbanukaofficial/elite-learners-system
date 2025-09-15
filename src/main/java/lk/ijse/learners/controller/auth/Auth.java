package lk.ijse.learners.controller.auth;

public class Auth {
    public static boolean areRequiredFieldsFilled(Object... inputs) {
        for(Object input : inputs){
            if (input == null || input.equals("")) {
                return false;
            }
        }
        return true;
    }
}
