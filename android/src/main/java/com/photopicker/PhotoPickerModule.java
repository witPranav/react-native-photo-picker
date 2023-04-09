package com.photopicker;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class PhotoPickerModule extends ReactContextBaseJavaModule {

    private static final int SINGLE_PHOTO_PICKER_REQUEST_CODE = 3;
    private static final int MULTIPLE_PHOTO_PICKER_REQUEST_CODE = 4;

    private Callback callback;

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            try {
                if (requestCode == SINGLE_PHOTO_PICKER_REQUEST_CODE || requestCode == MULTIPLE_PHOTO_PICKER_REQUEST_CODE) {
                    if (resultCode == RESULT_OK) {
                        WritableArray resultUris = Arguments.createArray();
                        if (intent != null) {
                            if (intent.getDataString() != null) {
                                resultUris.pushString(intent.getDataString());
                                sendMessageToJS(PhotoPickerConstants.SUCCESS, resultUris);
                            } else {
                                ClipData clipData = intent.getClipData();
                                if (clipData != null) {
                                    int count = clipData.getItemCount();
                                    for (int i = 0; i < count; i++) {
                                        ClipData.Item item = clipData.getItemAt(i);
                                        String uri = item.getUri().toString();
                                        resultUris.pushString(uri);
                                    }
                                    sendMessageToJS(PhotoPickerConstants.SUCCESS, resultUris);
                                }
                            }
                        }
                    } else if (resultCode == RESULT_CANCELED) {
                        sendErrorToJS(PhotoPickerConstants.CANCELLED, PhotoPickerConstants.CANCEL_MESSAGE);
                    }
                }
            } catch (Exception e) {
                sendErrorToJS(PhotoPickerConstants.ERROR, e.toString());
            }
        }
    };

    PhotoPickerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @NonNull
    @Override
    public String getName() {
        return PhotoPickerConstants.PACKAGE_NAME;
    }

    private void sendMessageToJS(String status, WritableArray data) {
        WritableMap params = Arguments.createMap();
        params.putString(PhotoPickerConstants.STATUS, status);
        params.putArray(PhotoPickerConstants.URIS, data);

        callback.invoke(params);
        callback = null;
    }

    private void sendErrorToJS(String status, String data) {
        WritableMap params = Arguments.createMap();
        params.putString(PhotoPickerConstants.STATUS, status);
        params.putString(PhotoPickerConstants.ERROR, data);

        callback.invoke(params);
        callback = null;
    }

    @ReactMethod
    public void launchPhotoPicker(ReadableMap params, Callback cb) {
        try {
            Activity currentActivity = getCurrentActivity();
            callback = cb;

            boolean multipleMedia = false;
            String mimeType = null;
            String mediaType = null;

            if (params.hasKey("multipleMedia")) {
                multipleMedia = params.getBoolean("multipleMedia");
            }
            if (params.hasKey("mimeType")) {
                mimeType = params.getString("mimeType");
            }
            if (params.hasKey("mediaType")) {
                mediaType = params.getString("mediaType");
            }

            int requestCode;
            Intent intent;

            PickVisualMediaRequest.Builder builder = new PickVisualMediaRequest.Builder();
            PickVisualMediaRequest request = new PickVisualMediaRequest();

            if (mimeType != null) {
                request = builder.setMediaType(new ActivityResultContracts.PickVisualMedia.SingleMimeType(mimeType)).build();
            } else {
                if (mediaType != null) {
                    switch (mediaType) {
                        case PhotoPickerConstants.IMAGE_AND_VIDEO:
                            request = builder.setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE).build();
                            break;
                        case PhotoPickerConstants.IMAGE_ONLY:
                            request = builder.setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build();
                            break;
                        case PhotoPickerConstants.VIDEO_ONLY:
                            request = builder.setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE).build();
                            break;
                    }
                } else {
                    request = builder.setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE).build();
                }
            }

            if (multipleMedia) {
                requestCode = MULTIPLE_PHOTO_PICKER_REQUEST_CODE;
                intent = new ActivityResultContracts.PickMultipleVisualMedia().createIntent(getReactApplicationContext(), request);
            } else {
                requestCode = SINGLE_PHOTO_PICKER_REQUEST_CODE;
                intent = new ActivityResultContracts.PickVisualMedia().createIntent(getReactApplicationContext(), request);
            }
            currentActivity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            sendErrorToJS(PhotoPickerConstants.ERROR, e.toString());
        }
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(PhotoPickerConstants.RN_SUCCESS, PhotoPickerConstants.SUCCESS);
        constants.put(PhotoPickerConstants.RN_ERROR, PhotoPickerConstants.ERROR);
        constants.put(PhotoPickerConstants.RN_CANCELLED, PhotoPickerConstants.CANCELLED);
        return constants;
    }
}