import {
  OptionsInterface,
  StatusConstantsInterface,
} from "./src/types/PhotoPickerTypes.ts";
import { NativeModules } from "react-native";

const { PhotoPicker } = NativeModules;

const StatusConstants: StatusConstantsInterface = PhotoPicker.getConstants();

export function launchPhotoPicker(
  options: OptionsInterface,
  callback?: (data: any) => void
) {
  return PhotoPicker.launchPhotoPicker(options, callback);
}

export { PhotoPicker, StatusConstants };

export * from "./src/types/PhotoPickerTypes.ts";
