package com.taxdoublehotmail.jarvis.utils;

import com.squareup.otto.Bus;

public final class BusProvider {
    public static final String TAG = BusProvider.class.getSimpleName();

    private static final Bus sInstance = new Bus();

    private BusProvider() {
        throw new AssertionError(String.format("%s: Cannot be initialized.", TAG));
    }

    public static Bus getInstance() {
        return sInstance;
    }
}
