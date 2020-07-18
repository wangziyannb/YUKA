package com.wzy.yuka.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.wzy.yuka.R;

import java.util.List;

/**
 * Created by Ziyan on 2020/7/10.
 */
public class SyncCompatibleAppsAdapter extends RecyclerView.Adapter<SyncCompatibleAppsAdapter.VH> {
    private List<SyncCompatibleApp> syncCompatibleApps;

    public SyncCompatibleAppsAdapter(List<SyncCompatibleApp> syncCompatibleApps) {
        this.syncCompatibleApps = syncCompatibleApps;
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sync_compatible_views, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        SyncCompatibleApp syncCompatibleApp = syncCompatibleApps.get(position);
        holder.itemView.setTag(position);
        holder.icon.setImageDrawable(syncCompatibleApp.getIcon());
        holder.name.setText(syncCompatibleApp.getName());
    }

    @Override
    public int getItemCount() {
        return syncCompatibleApps.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon;
        AppCompatTextView name;

        VH(View view) {
            super(view);
            icon = view.findViewById(R.id.sync_app_logo);
            name = view.findViewById(R.id.sync_app_name);
        }
    }

}
