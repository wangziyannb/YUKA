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

    @Override
    protected void onCleared() {
        super.onCleared();
        user_n = null;
        pwd = null;
    }

}
