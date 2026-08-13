# Photo Booth Android

Landscape Android tablet photo booth starter for Xiaomi Pad 6.

## Included

- Three-shot guided photo session.
- Default layout: one large photo at left, two stacked photo slots at right.
- Import a frame/background image, then drag photo slots or pinch them to resize.
- Save an 1800 × 1200 composite JPEG to the app's external-files directory.

## Open and run

Open this folder in Android Studio (JDK 17 recommended), let Gradle download dependencies, then run it on the tablet. Camera use requires Android camera permission.

## External cameras

This first version invokes Android's selected camera app. Test the target USB camera with the Xiaomi Pad 6 first. For a dedicated UVC camera, the next iteration should integrate its vendor SDK or a UVC library so photos can be captured directly inside the kiosk app.
