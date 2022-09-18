package jp.techacademy.chiharu.akiba.autoslideshowapp


import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.database.Cursor
import android.os.Handler
import java.util.*
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100
    private var cursor: Cursor? = null

    private var mTimer: Timer? = null
    private var mTimerSec = 0.0     //タイマー用の時間のための変数
    private var mHandler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        susumu_button.setOnClickListener(this)
        modoru_button.setOnClickListener(this)
        slideshow_button.setOnClickListener(this)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            getContentsInfo()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            }
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.susumu_button) {
            if (cursor != null) {
                if (cursor!!.moveToNext()) {
                    val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor!!.getLong(fieldIndex)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)
                } else {
                    if (cursor!!.moveToFirst()) {
                        val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                        val id = cursor!!.getLong(fieldIndex)
                        val imageUri =
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                        imageView.setImageURI(imageUri)
                    }
                }
            }
        } else if (v.id == R.id.modoru_button) {
            if (cursor != null) {
                if (cursor!!.moveToPrevious()) {
                    val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor!!.getLong(fieldIndex)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageView.setImageURI(imageUri)
                } else {
                    if (cursor!!.moveToLast()) {
                        val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                        val id = cursor!!.getLong(fieldIndex)
                        val imageUri =
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                        imageView.setImageURI(imageUri)
                    }
                }
            } else if (v.id == R.id.slideshow_button) {
                if (mTimer == null) {
                    mTimer = Timer()  //タイマーの作成
                    slideshow_button.text = "停止"
                    susumu_button.isEnabled = false
                    modoru_button.isEnabled = false

                    //タイマーの始動
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mTimerSec += 2.0 //タイマー用の時間
                            mHandler.post {         //HandlerクラスのインスタンスmHandler
                                if (cursor!!.moveToNext()) {
                                    val fieldIndex =
                                        cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                                    val id = cursor!!.getLong(fieldIndex)
                                    val imageUri = ContentUris.withAppendedId(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        id
                                    )
                                    imageView.setImageURI(imageUri)

                                } else {
                                    cursor!!.moveToFirst()
                                    val fieldIndex =
                                        cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                                    val id = cursor!!.getLong(fieldIndex)
                                    val imageUri = ContentUris.withAppendedId(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        id
                                    )
                                    imageView.setImageURI(imageUri)
                                }
                            }
                        }
                    }, 2000, 2000)  //最初に始動させるまで2000ミリ秒、2秒毎にスライドさせる、ループの間隔を2000ミリ秒に設定
                } else {
                    slideshow_button.text = "再生"
                    mTimer!!.cancel()
                    susumu_button.isEnabled = true
                    modoru_button.isEnabled = true
                    if (mTimer != null) {
                        mTimer = null
                    }
                }
            }
        }
    }
}



