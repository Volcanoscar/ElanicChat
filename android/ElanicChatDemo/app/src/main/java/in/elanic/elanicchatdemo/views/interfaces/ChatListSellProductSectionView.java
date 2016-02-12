package in.elanic.elanicchatdemo.views.interfaces;

import android.support.annotation.NonNull;

/**
 * Created by Jay Rambhia on 2/12/16.
 */
public interface ChatListSellProductSectionView extends ChatListSectionView {

    void showProductLayout(boolean status);
    void setTitle(@NonNull CharSequence text);
    void setPrice(@NonNull CharSequence text);
    void setOfferPrice(@NonNull CharSequence text);
    void setImage(@NonNull String url);
    void setSpecifications(@NonNull CharSequence text);

}
