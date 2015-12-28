package in.elanic.elanicchatdemo.presenters;

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

    }

    @Override
    public String getLatestMessage() {
        Message message = mMessageProvider.getLatestMessage();
        return message != null ? message.getContent() : null;
    }

    @Override
    public void sendMessage(String content) {
        Message message = mMessageProvider.createNewMessage(content, mSender, mReceiver);
        mChatView.updateLatestMessage(message.getContent());
    }
}
