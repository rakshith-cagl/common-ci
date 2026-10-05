package com.iexceed.common;

import androidx.annotation.NonNull;

/**
 * Created by siddaiahswamy.patil on 06-Feb-17.
 */
public abstract class OnPermissionsResultHandler {

    public abstract void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
