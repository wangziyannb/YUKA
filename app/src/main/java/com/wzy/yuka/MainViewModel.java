package com.wzy.yuka;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by Ziyan on 2020/5/8.
 */
public class MainViewModel extends ViewModel {
    private MutableLiveData<String> id;
    private MutableLiveData<String> pwd;

    public LiveData<String> getid() {
        if (id == null) {
            id = new MutableLiveData<>();
        }
        return id;
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
        id = null;
        pwd = null;
    }

}
