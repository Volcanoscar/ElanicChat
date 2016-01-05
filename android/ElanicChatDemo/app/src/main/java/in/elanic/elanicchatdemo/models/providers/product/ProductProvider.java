package in.elanic.elanicchatdemo.models.providers.product;

import java.util.List;

import in.elanic.elanicchatdemo.models.db.Product;

/**
 * Created by Jay Rambhia on 05/01/16.
 */
public interface ProductProvider {

    Product getProduct(String productId);
    int addOrUpdateProducts(List<Product> products);
    boolean doesProductExist(String productId);
    boolean addOrUpdateProduct(Product product);
}
