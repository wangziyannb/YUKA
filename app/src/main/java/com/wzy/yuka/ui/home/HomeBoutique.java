package com.wzy.yuka.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.io.ResultInput;
import com.wzy.yuka.tools.io.ResultSort;
import com.wzy.yuka.tools.message.BaseFragment;
import com.wzy.yuka.ui.view.Screenshot;
import com.wzy.yuka.ui.view.ScreenshotAdapter;
import com.wzy.yuka.ui.view.SpacesItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HomeBoutique extends BaseFragment implements ScreenshotAdapter.onItemClickListener {
    private List<Screenshot> screenshots = new ArrayList<>();
    private ScreenshotAdapter adapter;
    private boolean isDetail = false;

    @Override
    public boolean onBackPressed() {
        if (isDetail) {
            isDetail = false;
            int i = adapter.getItemCount();
            adapter.notifyItemRangeRemoved(0, i);
            screenshots.clear();
            initScreenshots(null);
            adapter.notifyItemRangeInserted(0, screenshots.size());
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        initScreenshots(null);
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_boutique, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.boutique_rec);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        HashMap<String, Integer> spacesVelue = new HashMap<>();
        spacesVelue.put(SpacesItemDecoration.TOP_SPACE, 0);
        spacesVelue.put(SpacesItemDecoration.BOTTOM_SPACE, 20);
        spacesVelue.put(SpacesItemDecoration.LEFT_SPACE, 0);
        spacesVelue.put(SpacesItemDecoration.RIGHT_SPACE, 0);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacesVelue));

        adapter = new ScreenshotAdapter(screenshots);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        return root;
    }

    private void initScreenshots(String date) {
        try {
            if (date == null) {
                File path = Objects.requireNonNull(getContext()).getExternalFilesDir("screenshot");
                File[] dirs = ResultSort.orderByName(Objects.requireNonNull(path));
                for (File dir : dirs) {
                    ArrayList<String> arrayList = ResultInput.ReadTxtFile(dir.getAbsolutePath() + "/imgList.txt");
                    if (arrayList.size() == 0) {
                        return;
                    }
                    String[] params = ResultInput.DecodeString(arrayList.get(0));
                    Screenshot screenshot = new Screenshot(params[0], dir.getName());
                    screenshots.add(screenshot);
                }
            } else {
                isDetail = true;
                String path = Objects.requireNonNull(getContext()).getExternalFilesDir("screenshot").getAbsolutePath() + "/" + date;
                Log.d("TAG", "initScreenshots: " + path);
                ArrayList<String> arrayList = ResultInput.ReadTxtFile(path + "/imgList.txt");
                for (int i = 0; i < arrayList.size(); i++) {
                    String[] params = ResultInput.DecodeString(arrayList.get(i));
                    Screenshot screenshot = new Screenshot(params[0], params[1]);
                    screenshots.add(screenshot);
                }
            }
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        TextView textView = v.findViewById(R.id.screenshot_date);
        int i = adapter.getItemCount();
        adapter.notifyItemRangeRemoved(0, i);
        screenshots.clear();
        initScreenshots(textView.getText() + "");
        adapter.notifyItemRangeInserted(0, screenshots.size());
    }
}
