#include <jni.h>
#include <algorithm>

/**
 * Inverts the colors of a bitmap.
 *
 * @param env The JNI environment.
 * @param byteBuffer The direct ByteBuffer containing the bitmap data.
 * @param width The width of the bitmap.
 * @param height The height of the bitmap.
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_example_drawingactivity_DrawingScreen_InvertColors_1Bitmap(JNIEnv *env, jobject /* this */,
                                                                    jobject byteBuffer, jint width,
                                                                    jint height) {
    void *bufferAddress = env->GetDirectBufferAddress(byteBuffer);
    if (bufferAddress == nullptr) {
        // ByteBuffer is null or doesn't point to a valid memory area
        return;
    }

    uint8_t *src = (uint8_t *) bufferAddress;

    for (int y = 0; y < height; ++y) {
        for (int x = 0; x < width; ++x) {
            int pos = (y * width + x) * 4;
            uint8_t originalRed = src[pos];
            uint8_t originalGreen = src[pos + 1];
            uint8_t originalBlue = src[pos + 2];

            src[pos] = 255 - originalRed;
            src[pos + 1] = 255 - originalGreen;
            src[pos + 2] = 255 - originalBlue;
        }
    }
}

/**
 * Adds random noise to the colors of a bitmap.
 *
 * @param env The JNI environment.
 * @param byteBuffer The direct ByteBuffer containing the bitmap data.
 * @param width The width of the bitmap.
 * @param height The height of the bitmap.
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_example_drawingactivity_DrawingScreen_AddNoise_1Bitmap(JNIEnv *env, jobject /* this */,
                                                                jobject byteBuffer, jint width,
                                                                jint height) {
    void *bufferAddress = env->GetDirectBufferAddress(byteBuffer);
    if (bufferAddress == nullptr) {
        // ByteBuffer is null or doesn't point to a valid memory area
        return;
    }

    uint8_t *src = (uint8_t *) bufferAddress;

    for (int y = 0; y < height; ++y) {
        for (int x = 0; x < width; ++x) {
            int pos = (y * width + x) * 4;
            uint8_t originalRed = src[pos];
            uint8_t originalGreen = src[pos + 1];
            uint8_t originalBlue = src[pos + 2];

            // Add random noise to each color channel
            src[pos] = std::min(255, originalRed + rand() % 50);
            src[pos + 1] = std::min(255, originalGreen + rand() % 50);
            src[pos + 2] = std::min(255, originalBlue + rand() % 50);
        }
    }
}