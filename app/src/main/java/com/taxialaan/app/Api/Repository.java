package com.taxialaan.app.Api;

import android.content.Context;

import com.taxialaan.app.Api.interfaces.CallBack;
import com.taxialaan.app.Api.response.Default;
import com.taxialaan.app.Api.response.Profile;
import com.taxialaan.app.Api.response.ResponseCheckPushy;
import com.taxialaan.app.Api.response.ResponseUpdateTokenPushy;
import com.taxialaan.app.Api.response.TransactionItem;
import com.taxialaan.app.Api.response.UserWalletDetail;
import com.taxialaan.app.Api.utils.RequestException;
import com.taxialaan.app.G;
import com.taxialaan.app.Helper.SharedHelper;
import com.taxialaan.app.Helper.URLHelper;
import com.taxialaan.app.R;
import com.taxialaan.app.Utils.NotificationCenter;

import java.util.List;

public class Repository implements NetworkProvider {

    private static Repository INSTANCE;
    private NetworkRepository networkRepository;


    public static Repository getInstance() {
        if (INSTANCE == null) {
            synchronized (Repository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Repository();
                }
            }
        }
        return INSTANCE;
    }

    private Repository() {
        networkRepository = NetworkRepository.getInstance();
    }

    @Override
    public void chargeCode(String charge_code, CallBack<Default> call) {
        networkRepository.chargeCode(charge_code, call);
    }

    @Override
    public void transferCharge(int wallet_id, int amount, CallBack<Default> call) {
        networkRepository.transferCharge(wallet_id, amount, call);
    }

    @Override
    public void getTransactionList(CallBack<List<TransactionItem>> call) {
        networkRepository.getTransactionList(call);
    }

    @Override
    public void getUserWalletDetail(int wallet_id, CallBack<UserWalletDetail> call) {
        networkRepository.getUserWalletDetail(wallet_id, call);
    }

    @Override
    public void getProfile(final CallBack<Profile> call) {
        networkRepository.getProfile(new CallBack<Profile>() {
            @Override
            public void onSuccess(Profile profile) {
                super.onSuccess(profile);
                if (call != null) {
                    call.onSuccess(profile);
                }
                Context context = G.getInstance().getApplicationContext();
                SharedHelper.putKey(context, "id", profile.getId() + "");
                SharedHelper.putKey(context, "first_name", profile.getFirstName());
                SharedHelper.putKey(context, "last_name", profile.getLastName());
                SharedHelper.putKey(context, "email", profile.getEmail());
                if (profile.getPicture() != null && profile.getPicture().startsWith("http"))
                    SharedHelper.putKey(context, "picture", profile.getPicture());
                else
                    SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + profile.getPicture());
                SharedHelper.putKey(context, "mobile", profile.getMobile());
                SharedHelper.putKey(context, "wallet_balance", profile.getWalletBalance() + "");
                SharedHelper.putKey(context, "payment_mode", profile.getPaymentMode());
                SharedHelper.putKey(context, "balance", profile.getBalance());
                SharedHelper.putKey(context, "wallet_id", profile.getWalletId() + "");
                if (!profile.getCurrency().equalsIgnoreCase("") && profile.getCurrency() != null)
                    SharedHelper.putKey(context, "currency", profile.getCurrency());
                else
                    SharedHelper.putKey(context, "currency", "$");
                SharedHelper.putKey(context, "sos", profile.getSos());
                SharedHelper.putKey(context, "loggedIn", context.getString(R.string.True));
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateWalletAmount);
            }

            @Override
            public void onFail(RequestException e) {
                super.onFail(e);
                if (call != null) {
                    call.onFail(e);
                }

            }
        });
    }

    @Override
    public void checkPushy(CallBack<ResponseCheckPushy> callBack) {

        networkRepository.checkPushy(callBack);
    }

    @Override
    public void updateTokenPushy(String token, CallBack<ResponseUpdateTokenPushy> callBack) {

        networkRepository.updateTokenPushy(token,callBack);

    }
}
