package in.elanic.elanicchatdemo.models.providers.chat;

import android.support.annotation.NonNull;

import java.util.List;

import de.greenrobot.dao.query.Query;
import in.elanic.elanicchatdemo.models.db.ChatItem;
import in.elanic.elanicchatdemo.models.db.ChatItemDao;

/**
 * Created by Jay Rambhia on 08/01/16.
 */
public class ChatItemProviderImpl implements ChatItemProvider {

    private ChatItemDao mDao;

    public ChatItemProviderImpl(ChatItemDao dao) {
        mDao = dao;
    }

    @Override
    public ChatItem getChatItem(String id) {
        return mDao.load(id);
    }

    @Override
    public boolean doesChatItemExist(String id) {
        return (mDao.queryBuilder().where(ChatItemDao.Properties.Chat_id.eq(id)).count() != 0);
    }

    @Override
    public boolean addOrUpdateChatItem(ChatItem chatItem) {
        return mDao.insertOrReplace(chatItem) != 0;
    }

    @Override
    public int addOrUpdateChatItems(List<ChatItem> items) {
        int count = 0;
        for (ChatItem item : items) {
            count = count + ( mDao.insertOrReplace(item) != 0 ? 1 : 0);
        }

        return count;
    }

    @Override
    public List<ChatItem> getActiveSellChats(String sellerId) {
        Query<ChatItem> query = mDao.queryBuilder().where(ChatItemDao.Properties.Seller_id.eq(sellerId)).build();
        return query.list();
    }

    @Override
    public List<ChatItem> getActiveBuyChats(String buyerId) {
        Query<ChatItem> query = mDao.queryBuilder().where(ChatItemDao.Properties.Buyer_id.eq(buyerId)).build();
        return query.list();
    }

    @Override
    public List<ChatItem> getActiveSellChatsForProduct(String sellerId, String productId) {
        Query<ChatItem> query = mDao.queryBuilder().where(ChatItemDao.Properties.Product_id.eq(productId),
                ChatItemDao.Properties.Seller_id.eq(sellerId)).build();
        return query.list();
    }

    @Override
    public String getReceiverId(@NonNull ChatItem item, @NonNull String senderId) {
        String buyerId = item.getBuyer_id();
        String sellerId = item.getSeller_id();

        if (senderId.equals(buyerId)) {
            return sellerId;
        }

        if (senderId.equals(sellerId)) {
            return buyerId;
        }

        return null;
    }
}
