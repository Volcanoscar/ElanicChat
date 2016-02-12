package in.elanic.elanicchatdemo.utils;

import android.support.annotation.NonNull;

import in.elanic.elanicchatdemo.models.db.Product;

/**
 * Created by Jay Rambhia on 2/12/16.
 */
public class ProductUtils {

    public static String getProductSpecification(@NonNull Product product) {
        StringBuilder sb = new StringBuilder();
        if (product.getSize() != null && !product.getSize().isEmpty()) {
            sb.append("Size: ");
            sb.append(product.getSize());
        }

        if (product.getBrand() != null && !product.getBrand().isEmpty()) {
            sb.append(" | Brand: ");
            sb.append(product.getBrand());
        }

        return sb.toString();
    }
}
