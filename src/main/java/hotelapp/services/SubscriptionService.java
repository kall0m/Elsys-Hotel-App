package hotelapp.services;

public final class SubscriptionService {
    // FREE SUBSCRIPTION
    public static final double FREE_SUB = 0;

    public static final int FREE_TYPES_COUNT = 5;
    public static final int FREE_ACCS_COUNT = 5;

    // FIRST SUBSCRIPTION
    public static final double FIRST_SUB = 10;

    public static final int FIRST_TYPES_COUNT = 10;
    public static final int FIRST_ACCS_COUNT = 20;

    // SECOND SUBSCRIPTION
    public static final double SECOND_SUB = 20;

    public static final int SECOND_TYPES_COUNT = 12;
    public static final int SECOND_ACCS_COUNT = 35;

    // PRIVATE //
    /*
     The caller references the constants using <tt>NotificationMessages.EMPTY_STRING</tt>,
     and so on. Thus, the caller should be prevented from constructing objects of
     this class, by declaring this private constructor.
     */
    private SubscriptionService(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }
}
