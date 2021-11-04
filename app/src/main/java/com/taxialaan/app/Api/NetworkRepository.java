package com.taxialaan.app.Api;

import com.taxialaan.app.Api.interfaces.CallBack;
import com.taxialaan.app.Api.response.Default;
import com.taxialaan.app.Api.response.Profile;
import com.taxialaan.app.Api.response.ResponseCheckPushy;
import com.taxialaan.app.Api.response.ResponseUpdateTokenPushy;
import com.taxialaan.app.Api.response.TransactionItem;
import com.taxialaan.app.Api.response.UserWalletDetail;
import com.taxialaan.app.Api.utils.APIError;
import com.taxialaan.app.Api.utils.ErrorUtils;
import com.taxialaan.app.Api.utils.RequestException;
import com.taxialaan.app.Api.utils.RetryableCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class NetworkRepository implements NetworkProvider {

    private ApiClient apiConnection = new ApiClient();

    private static NetworkRepository INSTANCE;

    public static NetworkRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (NetworkRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetworkRepository();
                }
            }
        }
        return INSTANCE;
    }

    private <T> RetryableCallback<T> makeCallBack(final CallBack<T> callBack) {
        return makeCallBack(0, 0, callBack);
    }

    private <T> RetryableCallback<T> makeCallBack(int maxRetry, int maxTimerRetry, final CallBack<T> callBack) {
        return new RetryableCallback<T>(maxRetry, maxTimerRetry) {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                super.onResponse(call, response);
                if (response.code() == 200 && response.body() != null && response.isSuccessful()) {
                    callBack.onSuccess(response.body());
                } else {
                    APIError apiError = ErrorUtils.parseError(apiConnection.getClient(), response);
                    try {
                        callBack.onFail(new RequestException(apiError.message(), response.code()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        callBack.onFail(new RequestException(response.message(), response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                super.onFailure(call, t);
                callBack.onFail(new RequestException(t.getMessage(), 0));

            }
        };
    }


    @Override
    public void chargeCode(String charge_code, CallBack<Default> call) {
        apiConnection.getService().chargeCode(charge_code).enqueue(makeCallBack(call));
    }

    @Override
    public void transferCharge(int wallet_id, int amount, CallBack<Default> call) {
        apiConnection.getService().transferCharge(wallet_id, amount).enqueue(makeCallBack(call));
    }

    @Override
    public void getTransactionList(CallBack<List<TransactionItem>> call) {
        apiConnection.getService().getTransactionList().enqueue(makeCallBack(call));
    }

    @Override
    public void getUserWalletDetail(int wallet_id, CallBack<UserWalletDetail> call) {
        apiConnection.getService().getUserWalletDetail(wallet_id).enqueue(makeCallBack(call));
    }

    @Override
    public void getProfile(CallBack<Profile> call) {
        apiConnection.getService().getProfile().enqueue(makeCallBack(call));
    }

    @Override
    public void checkPushy(CallBack<ResponseCheckPushy> callBack) {
        apiConnection.getService().checkPushy().enqueue(makeCallBack(callBack));
    }

    @Override
    public void updateTokenPushy(String token, CallBack<ResponseUpdateTokenPushy> callBack) {

        apiConnection.getService().updateTokenPushy(token).enqueue(makeCallBack(callBack));

    }
}
