package com.boredream.bdcodehelper.function;

import com.boredream.bdcodehelper.entity.IUser;
import com.boredream.bdcodehelper.net.BaseHttpRequest;
import com.boredream.bdcodehelper.net.ErrorConstants;
import com.boredream.bdcodehelper.net.ObservableDecorator;
import com.boredream.bdcodehelper.utils.StringUtils;


import rx.Observable;
import rx.Subscriber;

public class LoginPresenter implements LoginContract.Presenter {

    private final LoginContract.View view;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
    }

    @Override
    public void login(String username, String password) {
        if (StringUtils.isEmpty(username)) {
            view.showTip("用户名不能为空");
            return;
        }

        if (StringUtils.isEmpty(password)) {
            view.showTip("密码不能为空");
            return;
        }

        view.showProgress();

        Observable<IUser> observable = BaseHttpRequest.login(username, password);
        ObservableDecorator.decorate(observable).subscribe(new Subscriber<IUser>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (!view.isActive()) {
                    return;
                }
                view.dismissProgress();

                String error = ErrorConstants.parseHttpErrorInfo(e);
                view.showTip(error);
            }

            @Override
            public void onNext(IUser user) {
                if (!view.isActive()) {
                    return;
                }
                view.dismissProgress();

                view.loginSuccess(user);
            }
        });
    }

}
