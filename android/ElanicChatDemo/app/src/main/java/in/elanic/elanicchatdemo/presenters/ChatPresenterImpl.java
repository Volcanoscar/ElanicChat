package in.elanic.elanicchatdemo.presenters;

import java.util.ArrayList;
import java.util.List;

import in.elanic.elanicchatdemo.models.db.DaoSession;
import in.elanic.elanicchatdemo.models.db.Message;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.providers.message.MessageProvider;
import in.elanic.elanicchatdemo.models.providers.message.MessageProviderImpl;
import in.elanic.elanicchatdemo.models.providers.user.UserProvider;
import in.elanic.elanicchatdemo.models.providers.user.UserProviderImpl;
import in.elanic.elanicchatdemo.views.interfaces.ChatView;

/**
 * Created by Jay Rambhia on 28/12/15.
 */
public class ChatPresenterImpl implements ChatPresenter {

    private ChatView mChatView;
    private DaoSession mDaoSession;

    private UserProvider mUserProvider;
    private MessageProvider mMessageProvider;

    private User mSender;
    private User mReceiver;

    private List<Message> mMessages;

    public ChatPresenterImpl(ChatView mChatView, DaoSession mDaoSession) {
        this.mChatView = mChatView;
        this.mDaoSession = mDaoSession;

        mUserProvider = new UserProviderImpl(this.mDaoSession.getUserDao());
        mMessageProvider = new MessageProviderImpl(this.mDaoSession.getMessageDao());
    }

    @Override
    public void attachView() {
        mSender = mUserProvider.createSender();
        mReceiver = mUserProvider.createReceiver();
    }

    @Override
    public void detachView() {
        mMessages.clear();
    }

    @Override
    public void loadData() {
        mMessages = mMessageProvider.getAllMessages();
        mChatView.setData(mMessages);
    }


    @Override
    public void sendMessage(String content) {
        Message message = mMessageProvider.createNewMessage(content, mSender, mReceiver);
        if (mMessages == null) {
            mMessages = new ArrayList<>();
        }

        mMessages.add(0, message);
        mChatView.setData(mMessages);
    }
}
