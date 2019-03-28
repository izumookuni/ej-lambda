package cc.domovoi.collection.util;

/**
 * Base class for all tuples.
 */
public abstract class Product implements ProductLike {

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            return this.hashCode() == obj.hashCode();
        }
        else {
            return false;
        }
    }

    @Override
    public Integer productArity() {
        return productCollection().size();
    }

    @Override
    public Object productElement(Integer n) {
        return productCollection().get(n);
    }
}
