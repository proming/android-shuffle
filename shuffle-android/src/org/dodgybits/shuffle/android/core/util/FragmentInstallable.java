package org.dodgybits.shuffle.android.core.util;


import android.support.v4.app.Fragment;

/**
 * Interface for {@link android.app.Activity} that can "install" fragments.
 */
public interface FragmentInstallable {
    /**
     * Called when a {@link android.app.Fragment} wants to be installed to the host activity.
     *
     * Fragments which use this MUST call this from {@link android.app.Fragment#onActivityCreated} using
     * {@link UiUtilities#installFragment}.
     *
     * This means a host {@link android.app.Activity} can safely assume a passed {@link android.app.Fragment} is already
     * created.
     */
    public void onInstallFragment(Fragment fragment);

    /**
     * Called when a {@link Fragment} wants to be uninstalled from the host activity.
     *
     * Fragments which use this MUST call this from {@link Fragment#onDestroyView} using
     * {@link UiUtilities#uninstallFragment}.
     */
    public void onUninstallFragment(Fragment fragment);
}
