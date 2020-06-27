package com.wzy.yuka;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by Ziyan on 2020/5/8.
 */
public class MainViewModel extends ViewModel {
    private MutableLiveData<String> user_n;
    private MutableLiveData<String> pwd;
    private MutableLiveData<Boolean> back;
    public LiveData<String> getuser_n() {
        if (user_n == null) {
            user_n = new MutableLiveData<>();
        }
        return user_n;
    }

    public LiveData<String> getpwd() {
        if (pwd == null) {
            pwd = new MutableLiveData<>();
        }
        return pwd;
    }

    public LiveData<Boolean> getback() {
        if (back == null) {
            back = new MutableLiveData<>();
        }
        return back;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        user_n = null;
        pwd = null;
    }

}
