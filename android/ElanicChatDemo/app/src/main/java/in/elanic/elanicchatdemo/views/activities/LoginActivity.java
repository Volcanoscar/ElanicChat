package in.elanic.elanicchatdemo.views.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.elanic.elanicchatdemo.ELChatApp;
import in.elanic.elanicchatdemo.R;
import in.elanic.elanicchatdemo.components.ApplicationComponent;
import in.elanic.elanicchatdemo.components.DaggerLoginViewComponent;
import in.elanic.elanicchatdemo.models.db.User;
import in.elanic.elanicchatdemo.models.providers.PreferenceProvider;
import in.elanic.elanicchatdemo.modules.LoginProviderModule;
import in.elanic.elanicchatdemo.modules.LoginViewModule;
import in.elanic.elanicchatdemo.presenters.LoginPresenter;
import in.elanic.elanicchatdemo.views.interfaces.LoginView;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private static final String TAG = "LoginActivity";

    @Bind(R.id.root) View mRoot;
    @Bind(R.id.edittext) EditText mLoginIdEditText;

    private ProgressDialog mProgressDialog;

    @Inject
    LoginPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupComponent(ELChatApp.get(this).component());

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mPresenter.attachView();

        PreferenceProvider preferenceProvider = new PreferenceProvider(this);
        final String userId = preferenceProvider.getLoginUserId();

        Log.i(TAG, "user id: " + userId);

        if (userId != null && !userId.isEmpty()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    navigateOnLogin(userId, false);
                }
            }, 500);

            mRoot.setVisibility(View.GONE);
        }
    }

    private void setupComponent(ApplicationComponent applicationComponent) {

        DaggerLoginViewComponent.builder()
                .applicationComponent(applicationComponent)
                .loginProviderModule(new LoginProviderModule())
                .loginViewModule(new LoginViewModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @OnClick(R.id.button)
    public void onLoginClicked() {
        String userId = mLoginIdEditText.getText().toString().trim();
        if (userId.isEmpty()) {
            showSnackbar("Please enter login id");
            return;
        }

        mPresenter.login(userId);
    }

    @Override
    public void showSnackbar(CharSequence text) {
        Snackbar.make(mRoot, text, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressDialog(boolean show) {
        if (show) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

            mProgressDialog = ProgressDialog.show(this, "Login", "Please Wait", true, false);
        } else {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    @Override
    public void saveLoginData(User user) {
        PreferenceProvider provider = new PreferenceProvider(this);
        provider.setLoginUserId(user.getUser_id());
    }

    @Override
    public void navigateOnLogin(String userId, boolean newLogin) {
        Intent intent = ChatListActivity.getActivityIntent(this, userId, newLogin);
        startActivity(intent);
        finish();
    }
}
