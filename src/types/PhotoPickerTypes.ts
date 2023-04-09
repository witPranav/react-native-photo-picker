const MediaTypes = {
  ImageAndVideo: "ImageAndVideo",
  ImageOnly: "ImageOnly",
  VideoOnly: "VideoOnly",
} as const;
export type MediaTypes = typeof MediaTypes[keyof typeof MediaTypes];

export interface OptionsInterface {
  mediaType?: MediaTypes;
  mimeType?: string;
  multipleMedia?: boolean;
}

export interface PhotoPickerInterface {
  launchPhotoPicker: (
    options: OptionsInterface,
    cb: (data: any) => void
  ) => void;
}

export interface StatusConstantsInterface {
  STATUS_CANCELLED: string;
  STATUS_ERROR: string;
  STATUS_SUCCESS: string;
}
