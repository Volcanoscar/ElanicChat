package in.elanic.elanicchatdemo.features.chat.view;

import android.support.annotation.NonNull;

import java.util.List;

import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.User;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public interface ChatView {

    void setData(List<Message> data);
    void setLoadMoreEnabled(boolean status);
    void setOtherUser(@NonNull User user);
    void updateMessageAtIndex(int position);
    void confirmOfferResponse(int position, boolean accept);
    void confirmOfferCancellation(int position);
    void showProgressDialog(boolean show);
    void showSnackbar(CharSequence message);

    void showProductLayout(boolean status);
    void setProductTitle(@NonNull CharSequence text);
    void setPrice(@NonNull CharSequence text);
    void setOfferPrice(@NonNull CharSequence text, @NonNull CharSequence subtext);
    void setImage(@NonNull String url);
    void setSpecifications(@NonNull CharSequence text);

    void setProfileImage(@NonNull String url);
    void setUsername(@NonNull CharSequence text);

    void scrollToPosition(int position);

    void setOfferEarning(CharSequence text);
    void showOfferEarningProgressbar(boolean show);
    void showOfferError(CharSequence text);
    void hideOfferBottomLayout(boolean animate);
}
