package com.taxialaan.app.Api;

import com.taxialaan.app.Api.interfaces.CallBack;
import com.taxialaan.app.Api.response.Default;
import com.taxialaan.app.Api.response.Profile;
import com.taxialaan.app.Api.response.ResponseCheckPushy;
import com.taxialaan.app.Api.response.ResponseUpdateTokenPushy;
import com.taxialaan.app.Api.response.TransactionItem;
import com.taxialaan.app.Api.response.UserWalletDetail;
import com.taxialaan.app.Api.response.WalletPayment;

import java.util.List;

public interface NetworkProvider {

    void chargeCode(String charge_code, CallBack<Default> call);

    void transferCharge(int wallet_id, int amount, CallBack<Default> call);

    void getTransactionList(CallBack<List<TransactionItem>> call);

    void getUserWalletDetail(int wallet_id, CallBack<UserWalletDetail> call);

    void getProfile(CallBack<Profile> call);

    void checkPushy(CallBack<ResponseCheckPushy> callBack);

    void updateTokenPushy(String token,CallBack<ResponseUpdateTokenPushy> callBack);

}
