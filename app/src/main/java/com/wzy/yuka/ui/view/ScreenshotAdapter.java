package com.wzy.yuka.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wzy.yuka.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Ziyan on 2020/5/17.
 */
public class ScreenshotAdapter extends RecyclerView.Adapter<ScreenshotAdapter.VH> implements View.OnClickListener {
    private WeakReference<Context> mContext;
    private List<Screenshot> screenshots;
    private onItemClickListener onItemClickListener;

    public ScreenshotAdapter(List<Screenshot> screenshots) {
        this.screenshots = screenshots;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_screenshot, parent, false);
        mContext = new WeakReference<>(parent.getContext());
        VH vh = new VH(view);
        view.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Screenshot screenshot = screenshots.get(position);
        holder.itemView.setTag(position);
        holder.date.setText(screenshot.getDate());
        Bitmap bitmap;
        try {
            String path = screenshot.getPicSrc();
            bitmap = BitmapFactory.decodeFile(path);
        } catch (Exception e) {
//            bitmap= BitmapFactory.decodeResource(mContext.get().getResources(),R.drawable.avater);
            bitmap = null;
        }
        holder.pic.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return screenshots.size();
    }

    public interface onItemClickListener {
        void onItemClick(View v, int position);
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView date;

        VH(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.screenshot_image);
            date = itemView.findViewById(R.id.screenshot_date);
        }
    }
}
