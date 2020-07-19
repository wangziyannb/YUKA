package com.wzy.yuka.tools.message;

import androidx.fragment.app.Fragment;

/**
 * Created by Ziyan on 2020/5/18.
 */
public class BaseFragment extends Fragment implements HandleBackInterface {
    @Override
    public boolean onBackPressed() {
        return HandleBackUtil.handleBackPress(this);
    }
}
