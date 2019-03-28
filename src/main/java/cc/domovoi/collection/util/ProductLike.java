package cc.domovoi.collection.util;

import java.util.List;

/**
 * Base interface for all tuples.
 */
public interface ProductLike {

    /**
     * The size of this product.
     * @return for a product `A(x,,1,,, ..., x,,k,,)`, returns `k`
     */
    Integer productArity();

    /**
     * The n^th^ element of this product, 0-based.
     * @param n the index of the element to return
     * @return the element `n` elements after the first element
     */
    Object productElement(Integer n);

    /**
     * An list over all the elements of this product.
     * @return in the default implementation, an `List&lt;Object&gt;`
     */
    List<Object> productCollection();
}
