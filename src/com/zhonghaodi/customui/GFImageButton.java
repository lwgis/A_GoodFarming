package com.zhonghaodi.customui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

import com.zhonghaodi.goodfarming.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GFImageButton extends LinearLayout {
	private TextView titleTv = null;
	private ImageView contentIv = null;
	private ImageView deleteIv = null;
	private String path;
	private GFHandler handler;
	private boolean isHasImage = false;
	private Bitmap bitmap;
	private ImageChangedListener imageChangedListener;

	public boolean isHasImage() {
		return isHasImage;
	}

	public ImageChangedListener getImageChangedListener() {
		return imageChangedListener;
	}

	public void setImageChangedListener(
			ImageChangedListener imageChangedListener) {
		this.imageChangedListener = imageChangedListener;
	}

	public void setHasImage(boolean isHasImage) {
		this.isHasImage = isHasImage;
	}

	public GFImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHasImage(false);
		handler = new GFHandler(this);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.gfimagebutton, this);
		titleTv = (TextView) findViewById(R.id.title_text);
		contentIv = (ImageView) findViewById(R.id.content_image);
		deleteIv = (ImageView) findViewById(R.id.delete_image);
		deleteIv.setVisibility(View.INVISIBLE);
		deleteIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				contentIv.setImageResource(R.drawable.add_image);
				deleteIv.setVisibility(View.INVISIBLE);
				titleTv.setVisibility(View.VISIBLE);
				setHasImage(false);
				if (GFImageButton.this.imageChangedListener != null) {
					GFImageButton.this.imageChangedListener.imageChanged(
							GFImageButton.this, GFImageButton.this.isHasImage);
				}

			}
		});
	}

	public void setTitle(String title) {
		titleTv.setText(title);
	}

	public void setImageFilePath(String path) {
		this.path = path;
		new Thread(new ImageThread(), "ImageThread").start();
		deleteIv.setVisibility(View.VISIBLE);
	}

	class ImageThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.obj = GFImageButton.this.getImage(path);
			GFImageButton.this.handler.sendMessage(msg);
		}

	}

	private Bitmap getImage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 720f;// 这里设置高度为800f
		float ww = 960f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return  compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	
	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 90;
		while (baos.toByteArray().length / 1024 > 500&&options>10) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}


	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	static class GFHandler extends Handler {
		WeakReference<GFImageButton> reference;

		public GFHandler(GFImageButton imageButton) {
			reference = new WeakReference<GFImageButton>(imageButton);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			GFImageButton imageButton = reference.get();
			if (msg.obj != null) {
				Bitmap bm = (Bitmap) msg.obj;
				imageButton.bitmap = bm;
				imageButton.contentIv.setImageBitmap(bm);
				imageButton.contentIv
						.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageButton.titleTv.setVisibility(View.INVISIBLE);
				imageButton.setHasImage(true);
				if (imageButton.imageChangedListener != null) {
					imageButton.imageChangedListener.imageChanged(imageButton,
							imageButton.isHasImage);
				}
			}
		}

	}

	public interface ImageChangedListener {
		void imageChanged(View v, boolean isHasImage);
	}
}
