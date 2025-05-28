#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>

typedef NS_ENUM(NSInteger, MicrophoneEffectiveAccessStatus) {
    MicrophoneEffectiveAccessStatusAccessible,
    MicrophoneEffectiveAccessStatusBlocked,
    MicrophoneEffectiveAccessStatusError
};

MicrophoneEffectiveAccessStatus testEffectiveMicrophoneAccess() {
    @autoreleasepool {
        // Create a capture session
        AVCaptureSession *captureSession = [[AVCaptureSession alloc] init];

        // Get the default audio input (microphone)
        AVCaptureDevice *microphone = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeAudio];
        if (!microphone) {
            return MicrophoneEffectiveAccessStatusError;
        }

        NSError *error = nil;
        AVCaptureDeviceInput *micInput = [AVCaptureDeviceInput deviceInputWithDevice:microphone error:&error];
        if (error) {
            return MicrophoneEffectiveAccessStatusBlocked;
        }

        // Add the microphone input to the session
        if ([captureSession canAddInput:micInput]) {
            [captureSession addInput:micInput];
        } else {
            return MicrophoneEffectiveAccessStatusBlocked;
        }

        // Attempt to start the capture session
        @try {
            [captureSession startRunning];
        } @catch (NSException *exception) {
            return MicrophoneEffectiveAccessStatusBlocked;
        }

        // Stop the session immediately (we just wanted to test permissions)
        [captureSession stopRunning];

        return MicrophoneEffectiveAccessStatusAccessible;
    }
}

bool requestMicrophonePermission(void) {
    __block bool accessGranted = false;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);

    [AVCaptureDevice requestAccessForMediaType:AVMediaTypeAudio completionHandler:^(BOOL granted) {
        accessGranted = true;
        dispatch_semaphore_signal(semaphore);
    }];

    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    return accessGranted;
}

int getMicrophonePermissionStatus(void) {
    AVAuthorizationStatus authStatus =
            [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeAudio];

    switch (authStatus) {
        case AVAuthorizationStatusAuthorized:
            printf("AVAuthorizationStatusAuthorized\n");
            return 1;

        case AVAuthorizationStatusDenied:
            printf("AVAuthorizationStatusDenied\n");
            return 2;

        case AVAuthorizationStatusRestricted:
            printf("AVAuthorizationStatusRestricted\n");
            return 3;

        case AVAuthorizationStatusNotDetermined:
            printf("AVAuthorizationStatusNotDetermined\n");
            return 0;

        default:
            return 0;
    }
}

