package hotelapp.services;

public final class NotificationMessages {
    // USER MESSAGES
    public static final String USER_ALREADY_EXISTS = "A user with the same email address already exists!";

    public static final String USER_DOESNT_HAVE_FORGOT_PASSWORD_CONFIRMATION_TOKEN = "Please first go to \"Forgot password\" section.";

    public static final String MAXIMAL_ACCS_COUNT_LIMIT_REACHED = "Maximal worker accounts count limit reached. Please update subscription.";

    public static final String MAXIMAL_TYPES_COUNT_LIMIT_REACHED = "Maximal types count limit reached. Please update subscription.";

    public static final String CREATED_WORKER = "Created 1 worker.";

    public static final String CREATED_WORKERS(int count) {
        return "Created " + count + " workers.";
    }

    public static final String USER_CONFIRMATION_EMAIL_SENT(String email) {
        return "Confirmation email has been sent to " + email + ".";
    }

    public static final String USER_DOESNT_EXISTS(String email) {
        return "User with email address \"" + email + "\" doesn't exist!";
    }

    public static final String USER_FORGOT_PASSWORD_CONFIRMATION_EMAIL_SENT(String email) {
        return "Email for password recovery has been sent to " + email;
    }

    // WORKER MESSAGES
    public static final String WORKER_DOESNT_EXIST = "The searched worker doesn't exist!";

    public static final String WORKER_SUCCESSFULLY_CREATED = "Worker successfully created!";

    public static final String WORKER_SUCCESSFULLY_DELETED = "Worker successfully deleted!";

    public static final String WORKER_TYPE_DOESNT_EQUAL_TASK_TYPE = "You don't have access to that page.";

    // TASK MESSAGES
    public static final String TASK_DOESNT_EXIST = "The searched task doesn't exist!";

    public static final String TASK_SUCCESSFULLY_CREATED = "Task successfully created!";

    public static final String TASK_SUCCESSFULLY_DELETED = "Task successfully deleted!";

    public static final String TASK_EDIT_NOT_ALLOWED = "You can't edit this task because it is already handled by a worker!";

    // TYPE MESSAGES
    public static final String TYPE_DOESNT_EXIST = "The searched type doesn't exist!";

    public static final String TYPE_SUCCESSFULLY_CREATED = "Type successfully created!";

    public static final String TYPE_SUCCESSFULLY_DELETED = "Type successfully deleted!";

    // SUCCESS MESSAGES
    public static final String USER_SUCCESSFULLY_LOGGED_OUT = "You successfully logged out!";

    public static final String USER_EMAIL_SUCCESSFULLY_CONFIRMED = "Your email address is successfully confirmed! Please log in.";

    // FORM MESSAGES
    public static final String FILL_FORM_CORRECTLY = "Please fill the form correctly!";

    public static final String FILL_ALL_FIELDS = "Please fill all fields!";

    public static final String CHANGES_SAVED = "Changes have been saved. Happy tasking!";

    public static final String PASSWORDS_DONT_MATCH = "Passwords don't match. Try again!";

    public static final String PASSWORD_TOO_WEAK = "Password is too weak!";

    public static final String WRONG_EMAIL_OR_PASSWORD = "Invalid email or password! Please try again.";

    public static final String PAYPAL_ERROR = "PayPal Error. Please try again.";

    // URL MESSAGES
    public static final String ACCESS_DENIED = "You don't have power here!";

    public static final String WRONG_CONFIRMATION_LINK = "Oops! Wrong email confirmation link."; //not very likely

    // PRIVATE //
    /*
     The caller references the constants using <tt>NotificationMessages.EMPTY_STRING</tt>,
     and so on. Thus, the caller should be prevented from constructing objects of
     this class, by declaring this private constructor.
     */
    private NotificationMessages(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }
}