# react-native-photo-picker

## Getting started

`$ npm install react-native-photo-picker --save`

## Note

- This module works only for android at the moment.

## How it works

-- This module uses the new [PhotoPicker API](https://developer.android.com/training/data-storage/shared/photopicker) introduced in Android 11.
-- Since this API is also backported, this module works also in previous versions of Android.

## Usage

- Goto app level build.gradle in android/app/build.gradle and add the following block in the end of the file.

```groovy
configurations.implementation {
    exclude group: 'org.jetbrains.kotlin', module: 'kotlin-stdlib-jdk8'
}
```

-- If backported photo picker support is needed as well, insert the following entry to the <application> tag in your apps manifest file

```xml
<service android:name="com.google.android.gms.metadata.ModuleDependencies"
         android:enabled="false"
         android:exported="false">
    <intent-filter>
        <action android:name="com.google.android.gms.metadata.MODULE_DEPENDENCIES" />
    </intent-filter>
    <meta-data android:name="photopicker_activity:0:required" android:value="" />
</service>
```

-- Javascript

```javascript
import {launchPhotoPicker, StatusConstants} from 'react-native-photo-picker';

launchPhotoPicker(
    {
    mediaType: 'ImageAndVideo', //optional prop; possible values can be ImageAndVideo, ImageOnly, VideoOnly; defaults to ImageAndVideo
    multipleMedia: true //optional prop; defaults to false
    mimeType: "image/gif" //optional prop;
    },
    (res: any) => {
      if (res.status === StatusConstants.STATUS_CANCELLED) {
        //process cancelled res.error
      }
      if (res.status === StatusConstants.STATUS_SUCCESS) {
        // process success res.uris
      }
      if (res.status === StatusConstants.STATUS_ERROR) {
        // process error res.error
      }
    },
  );
```

-- More information on mimetypes [here](https://developer.android.com/reference/androidx/media3/common/MimeTypes).

## TODO

- Add Persist Media File [Permission](https://developer.android.com/training/data-storage/shared/photopicker#persist-media-file-access).
- ???
