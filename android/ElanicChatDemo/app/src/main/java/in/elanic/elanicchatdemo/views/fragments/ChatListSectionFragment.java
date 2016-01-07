package in.elanic.elanicchatdemo.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.elanic.elanicchatdemo.ELChatApp;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.components.ApplicationComponent;
import in.elanic.elanicchatdemo.models.ChatItem;
import in.elanic.elanicchatdemo.presenters.ChatListSectionPresenter;
import in.elanic.elanicchatdemo.views.adapters.ChatListAdapter;
import in.elanic.elanicchatdemo.views.interfaces.ChatListSectionView;

/**
 * Created by Jay Rambhia on 06/01/16.
 */
public abstract class ChatListSectionFragment extends Fragment implements ChatListSectionView {

    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    @Bind(R.id.progress_bar) ProgressBar mProgressBar;
    @Bind(R.id.error_view) TextView mErrorView;

    @Inject
    ChatListSectionPresenter mPresenter;

    private ChatListAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent(ELChatApp.get(getContext()).component());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list_layout, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new ChatListAdapter(getActivity());
        mAdapter.setHasStableIds(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setCallback(new ChatListAdapter.Callback() {
            @Override
            public void onItemClicked(int position) {
                mPresenter.openChat(position);
            }
        });

        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);

        mPresenter.attachView(getArguments());

        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
        mPresenter.detachView();
    }

    @Override
    public void showError(CharSequence text) {
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mErrorView.setText(text);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void setData(List<ChatItem> data) {
        if (mAdapter != null) {
            mAdapter.setItems(data);
            mAdapter.notifyDataSetChanged();

            mRecyclerView.setVisibility(View.VISIBLE);
            mErrorView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);

            return;
        }

        showError("Unable to show data");
    }

    @Override
    public void showProgressBar(boolean show) {
        if (show) {
            mRecyclerView.setVisibility(View.GONE);
            mErrorView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void loadChats() {
        if (mPresenter != null) {
            mPresenter.loadData();
        }
    }

    public boolean openChatIfExists(String productId) {
        return mPresenter != null && mPresenter.openIfChatExists(productId);
    }

    protected abstract void setupComponent(ApplicationComponent applicationComponent);
}
