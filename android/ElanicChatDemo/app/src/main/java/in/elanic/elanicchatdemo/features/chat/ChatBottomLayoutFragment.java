package in.elanic.elanicchatdemo.features.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.features.shared.widgets.NumbersLayout;

/**
 * Created by Jay Rambhia on 2/15/16.
 */
public class ChatBottomLayoutFragment extends Fragment {

    @Bind(R.id.close_button) FloatingActionButton closeButton;
    @Bind(R.id.offer_input_view) TextView inputView;
    @Bind(R.id.numbers_layout) NumbersLayout numbersLayout;

    private Callback callback;

    public static ChatBottomLayoutFragment newInstance(Bundle args) {
        ChatBottomLayoutFragment fragment = new ChatBottomLayoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ChatBottomLayoutFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_bottom_layout, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onCloseRequested();
                }
            }
        });

        numbersLayout.setCallback(new NumbersLayout.Callback() {
            @Override
            public void onNumberChanged(String number) {
                inputView.setText("Rs. " + number);
                if (callback != null) {
                    callback.onPriceChanged(number);
                }
            }

            @Override
            public void onDoneClicked() {

                if (callback != null) {
                    callback.onSendOfferRequested(numbersLayout.getInput());
                }
            }
        });

        inputView.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        numbersLayout.setCallback(null);
        ButterKnife.unbind(this);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onSendOfferRequested(CharSequence price);
        void onPriceChanged(CharSequence price);
        void onCloseRequested();
    }
}
