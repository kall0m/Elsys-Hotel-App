package hotelapp.services;

public final class EmailDrafts {
    // APPLICATION NAME
    public static final String APP_NAME = "Dolores TUES";
    public static final String APP_EMAIL = "k.madjunov@gmail.com";
    public static final String EMAIL_CLOSING = "Sincerely yours,\n" + APP_EMAIL;

    // USER REGISTRATION
    public static final String USER_REGISTRATION_SUBJECT = APP_NAME + " - Registration";
    public static final String USER_REGISTRATION_CONTENT(String appUrl, String confirmationToken) {
        return "To confirm your email address, please click the link below:\n" + appUrl + "/confirm?token=" + confirmationToken + "\n\n" + EMAIL_CLOSING;
    }

    // USER FORGOT PASSWORD
    public static final String USER_FORGOT_PASSWORD_SUBJECT = APP_NAME + " - Password Recovery";
    public static final String USER_FORGOT_PASSWORD_CONTENT(String appUrl, String forgotPasswordToken) {
        return "To recover your password, please follow the link below:\n" + appUrl + "/user/set_new_password?token=" + forgotPasswordToken + "\n\n" + EMAIL_CLOSING;
    }

    // PRIVATE //
    /*
     The caller references the constants using <tt>NotificationMessages.EMPTY_STRING</tt>,
     and so on. Thus, the caller should be prevented from constructing objects of
     this class, by declaring this private constructor.
     */
    private EmailDrafts(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }
}
