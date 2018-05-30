package hotelapp.services;

public final class TaskStatus {

    public static final String TODO = "To do";

    public static final String DOING = "Doing";

    public static final String DONE = "Done";

    // PRIVATE //
    /*
     The caller references the constants using <tt>NotificationMessages.EMPTY_STRING</tt>,
     and so on. Thus, the caller should be prevented from constructing objects of
     this class, by declaring this private constructor.
     */
    private TaskStatus(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }
}
