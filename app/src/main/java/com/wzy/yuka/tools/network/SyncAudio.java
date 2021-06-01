package com.wzy.yuka.tools.network;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * Created by Ziyan on 2021/6/1.
 */
public abstract class SyncAudio {
    public SyncAudio() {
    }

    public abstract void send(ByteBuffer buffer);

    public abstract void close();

    public abstract boolean isClosed();

    public abstract boolean isRunning();

    public abstract void start(@NotNull String from, @NotNull String to, @NotNull String pattern);
}
