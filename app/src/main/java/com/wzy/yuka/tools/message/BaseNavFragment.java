package com.wzy.yuka.tools.message;

import androidx.navigation.fragment.NavHostFragment;

/**
 * Created by Ziyan on 2020/5/18.
 */
public class BaseNavFragment extends NavHostFragment implements HandleBackInterface {


    @Override
    public boolean onBackPressed() {
        return HandleBackUtil.handleBackPress(this);
    }
}
