package com.yourssu.soomsil.usaint.util

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.core.view.drawToBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File
import java.io.FileOutputStream

// TODO: 커스텀 Modifier 제작하기

// https://github.com/PatilShreyas/Capturable/commit/e22087a9068db6c2a44307bbfd007266ff90d27c
// https://github.com/PatilShreyas/Capturable/commit/9cc2bbbd120721dbeffa37be3ecce8a29d7269d1
// 해당 소스 참고하여 작성함
class CaptureController {
    private val _captureRequests = MutableSharedFlow<Bitmap.Config>(extraBufferCapacity = 1)
    val captureRequest = _captureRequests.asSharedFlow();

    fun capture(config: Bitmap.Config = Bitmap.Config.ARGB_8888) {
        _captureRequests.tryEmit(config)
    }
}

@Composable
fun rememberCaptureController(): CaptureController {
    return remember { CaptureController() }
}

@Composable
fun Capturable(
    controller: CaptureController,
    predicate: () -> Boolean, // predicate의 반환값이 true여야 onCaptured가 수행됩니다.
    onCaptured: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AndroidView(
        factory = { context ->
            ComposeView(context).apply composeView@{
                setContent {
                    val updatedOnCaptured by rememberUpdatedState(newValue = onCaptured)
                    val updatedPredicate by rememberUpdatedState(newValue = predicate)

                    LaunchedEffect(controller) {
                        controller.captureRequest.collect { config ->
                            if (updatedPredicate()) {
                                delay(100) // 렌더링 될 시간 필요
                                doOnLayout {
                                    val bitmap = this@composeView.drawToBitmap(config)
                                    updatedOnCaptured(bitmap)
                                }
                            }
                        }
                    }
                    content()
                }
            }
        },
        modifier = modifier,
    )
}

// bitmap을 png로 압축한 후 외부 저장소에 저장하는 로직
fun saveBitmapUtil(
    bitmap: Bitmap,
    context: Context,
    filename: String,
    onSuccess: () -> Unit,
    onError: () -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        saveBitmapWithScopedStorage(bitmap, context, filename, onSuccess, onError)
    } else {
        if (requestStoragePermission(context)) {
            saveBitmapToExternalStorage(bitmap, filename, onSuccess, onError)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q) // API 29 이후
private fun saveBitmapWithScopedStorage(
    bitmap: Bitmap,
    context: Context,
    filename: String,
    onSuccess: () -> Unit,
    onError: () -> Unit,
) {
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(".png")

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val resolver = context.contentResolver
    val contentUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    if (contentUri != null) {
        context.contentResolver.openOutputStream(contentUri)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            onSuccess()
        } ?: onError()
    } else {
        onError()
    }
}

private fun saveBitmapToExternalStorage(
    bitmap: Bitmap,
    filename: String,
    onSuccess: () -> Unit,
    onError: () -> Unit,
) {
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        filename
    )

    try {
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        onSuccess()
    } catch (e: Exception) {
        onError()
    }
}

private fun requestStoragePermission(context: Context): Boolean {
    val permissionGranted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.WRITE_EXTERNAL_STORAGE,
    ) == PackageManager.PERMISSION_GRANTED

    if (permissionGranted) return true

    ActivityCompat.requestPermissions(
        context as Activity,
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        101
    )
    return false
}
