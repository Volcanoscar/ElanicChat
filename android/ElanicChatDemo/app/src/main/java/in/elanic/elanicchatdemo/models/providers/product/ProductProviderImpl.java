package in.elanic.elanicchatdemo.models.providers.product;

import java.util.List;

import in.elanic.elanicchatdemo.models.db.Product;
import in.elanic.elanicchatdemo.models.db.ProductDao;

/**
 * Created by Jay Rambhia on 05/01/16.
 */
public class ProductProviderImpl implements ProductProvider {

    private ProductDao mDao;

    public ProductProviderImpl(ProductDao mDao) {
        this.mDao = mDao;
    }

    @Override
    public Product getProduct(String productId) {
        return mDao.load(productId);
    }

    @Override
    public int addOrUpdateProducts(List<Product> products) {
        int count = 0;
        for (Product product : products) {
            count = count + (mDao.insertOrReplace(product) != 0 ? 1 : 0);
        }
        return count;
    }

    @Override
    public boolean doesProductExist(String productId) {
        return (mDao.queryBuilder().where(ProductDao.Properties.Product_id.eq(productId)).count() != 0);
    }

    @Override
    public boolean addOrUpdateProduct(Product product) {
        return mDao.insertOrReplace(product) != 0;
    }
}
