package cc.domovoi.lambda.function;

/**
 * This class implements errors which are thrown whenever an
 * object doesn't match any pattern of a pattern matching
 * expression.
 */
public class MatchError extends RuntimeException {

    /**
     * Error
     */
    private Object obj;

    /**
     * Constructor
     *
     * @param obj Error
     */
    public MatchError(Object obj) {
        this.obj = obj;
    }

    /**
     * Error message of this MatchError
     *
     * @return Error message
     */
    @Override
    public String getMessage() {
        String ofClass = "of class " + obj.getClass().getName();
        if (obj == null) {
            return "null";
        } else {
            try {
                return obj.toString() + " (" + ofClass + ")";
            } catch (Throwable e) {
                return "an instance " + ofClass;
            }
        }
    }
}
