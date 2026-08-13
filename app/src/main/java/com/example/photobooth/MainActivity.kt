package com.example.photobooth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.widget.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : Activity() {
    private lateinit var canvas: FrameLayout
    private lateinit var status: TextView
    private val slots = mutableListOf<PhotoSlotView>()
    private var background: Bitmap? = null
    private var shotIndex = 0
    private var captureUri: Uri? = null
    private val photos = arrayOfNulls<Bitmap>(3)
    private val dp get() = resources.displayMetrics.density

    override fun onCreate(state: Bundle?) { super.onCreate(state); if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) requestPermissions(arrayOf(Manifest.permission.CAMERA), 4); buildUi() }
    private fun buildUi() {
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.rgb(23,33,43)); setPadding((16*dp).toInt(), (12*dp).toInt(), (16*dp).toInt(), (12*dp).toInt()) }
        status = TextView(this).apply { text = "Set your frame, then start a 3-shot session"; textSize = 18f; setTextColor(Color.WHITE); gravity = Gravity.CENTER }
        root.addView(status, LinearLayout.LayoutParams(-1, 42.dp()))
        canvas = FrameLayout(this).apply { setBackgroundColor(Color.rgb(255,249,240)) }
        root.addView(canvas, LinearLayout.LayoutParams(-1, 0, 1f))
        val actions = LinearLayout(this).apply { gravity = Gravity.CENTER; orientation = LinearLayout.HORIZONTAL }
        listOf("Import background" to { chooseBackground() }, "Reset layout" to { resetLayout() }, "Start 3 shots" to { startSession() }, "Save image" to { saveComposite() }).forEach { (title, action) ->
            actions.addView(Button(this).apply { text = title; setOnClickListener { action() } }, LinearLayout.LayoutParams(0, 58.dp(), 1f).apply { setMargins(5.dp(),5.dp(),5.dp(),0) })
        }
        root.addView(actions); setContentView(root); canvas.post { resetLayout() }
    }
    private fun Int.dp() = (this * dp).toInt()
    private fun resetLayout() {
        canvas.removeAllViews(); background?.let { canvas.addView(ImageView(this).apply { setImageBitmap(it); scaleType = ImageView.ScaleType.FIT_XY }, FrameLayout.LayoutParams(-1,-1)) }
        slots.clear(); val w = canvas.width; val h = canvas.height
        val data = arrayOf(intArrayOf((w*.05).toInt(),(h*.08).toInt(),(w*.56).toInt(),(h*.84).toInt()), intArrayOf((w*.64).toInt(),(h*.08).toInt(),(w*.30).toInt(),(h*.39).toInt()), intArrayOf((w*.64).toInt(),(h*.53).toInt(),(w*.30).toInt(),(h*.39).toInt()))
        data.forEachIndexed { i, p -> slots += PhotoSlotView(this, i).apply { photos[i]?.let { setImageBitmap(it) }; contentDescription = "Photo ${i+1}: drag to move, pinch to resize"; canvas.addView(this, FrameLayout.LayoutParams(p[2],p[3]).apply { leftMargin=p[0]; topMargin=p[1] }) } }
    }
    private fun chooseBackground() { startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type="image/*"; addCategory(Intent.CATEGORY_OPENABLE) }, 20) }
    private fun startSession() { shotIndex=0; status.text="Photo 1 of 3 — camera opens now"; captureNext() }
    private fun captureNext() {
        val file = File(cacheDir, "booth_${System.currentTimeMillis()}.jpg"); captureUri = Uri.fromFile(file)
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, captureUri), 10)
    }
    override fun onActivityResult(request: Int, result: Int, data: Intent?) { super.onActivityResult(request,result,data)
        if (result != RESULT_OK) { status.text="Session cancelled"; return }
        when(request) { 20 -> { background = data?.data?.let { uriToBitmap(it) }; resetLayout(); status.text="Background ready. Drag or pinch the photo areas to position them." }
            10 -> { captureUri?.let { photos[shotIndex]=uriToBitmap(it) }; shotIndex++; resetLayout(); if(shotIndex<3) { status.text="Photo ${shotIndex+1} of 3 — camera opens now"; canvas.postDelayed({captureNext()},700) } else status.text="All 3 shots are ready. Adjust the frame or save your image." }
        }
    }
    private fun uriToBitmap(uri: Uri): Bitmap? = contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
    private fun saveComposite() {
        if (photos.any { it == null }) { status.text="Take all 3 photos first."; return }
        val output=Bitmap.createBitmap(1800,1200,Bitmap.Config.ARGB_8888); val c=Canvas(output); c.drawColor(Color.WHITE)
        background?.let { c.drawBitmap(it,null,Rect(0,0,1800,1200),null) }
        slots.forEachIndexed { i,v -> val lp=v.layoutParams as FrameLayout.LayoutParams; val r=Rect((lp.leftMargin.toFloat()/canvas.width*1800).toInt(),(lp.topMargin.toFloat()/canvas.height*1200).toInt(),((lp.leftMargin+lp.width).toFloat()/canvas.width*1800).toInt(),((lp.topMargin+lp.height).toFloat()/canvas.height*1200).toInt()); c.drawBitmap(photos[i]!!,null,r,Paint(Paint.ANTI_ALIAS_FLAG)) }
        val file=File(getExternalFilesDir(null),"PhotoBooth_${System.currentTimeMillis()}.jpg"); FileOutputStream(file).use { output.compress(Bitmap.CompressFormat.JPEG,95,it) }; status.text="Saved: ${file.name}"
    }
}
