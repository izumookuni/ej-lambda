package cc.domovoi.ej.lambda.function;

public class MatchError extends RuntimeException {

    private Object obj;

    public MatchError(Object obj) {
        this.obj = obj;
    }

    @Override
    public String getMessage() {
        String ofClass = "of class " + obj.getClass().getName();
        if (obj == null) {
            return "null";
        }
        else {
            try {
                return obj.toString() + " (" + ofClass + ")";
            } catch (Throwable e) {
                return "an instance " + ofClass;
            }
        }
    }
}
