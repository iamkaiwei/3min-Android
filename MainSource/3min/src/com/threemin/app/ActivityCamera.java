/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.threemin.app;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImage.OnPictureSavedListener;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLookupFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.threemin.adapter.FilterAdapter;
import com.threemin.uti.CameraHelper;
import com.threemin.uti.CameraHelper.CameraInfo2;
import com.threemin.uti.GPUImageFilterTools.FilterAdjuster;
import com.threemin.view.HorizontalListView;
import com.threemins.R;

public class ActivityCamera extends Activity implements OnSeekBarChangeListener, OnClickListener {
	
	public static final int REQUEST_CROP = 33;

	private GPUImage mGPUImage;
	private CameraHelper mCameraHelper;
	private CameraLoader mCamera;
	private GPUImageFilter mFilter;
	private FilterAdjuster mFilterAdjuster;
	private GPUImageView gpuImageView;
	private static final int REQUEST_PICK_IMAGE = 1;
	View surface;
	private HorizontalListView listfilters;
	private View lnFilter;
	private View imgHide;
	private static int TIME_TRANSLATE=500;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		((SeekBar) findViewById(R.id.seekBar)).setOnSeekBarChangeListener(this);
		findViewById(R.id.button_capture).setOnClickListener(this);

		mGPUImage = new GPUImage(this);
		mGPUImage.setGLSurfaceView((GLSurfaceView) findViewById(R.id.surfaceView));
		surface = findViewById(R.id.surfaceView);

		mCameraHelper = new CameraHelper(this);
		mCamera = new CameraLoader();

		View cameraSwitchView = findViewById(R.id.img_switch_camera);
		cameraSwitchView.setOnClickListener(this);
		if (!mCameraHelper.hasFrontCamera() || !mCameraHelper.hasBackCamera()) {
			cameraSwitchView.setVisibility(View.GONE);
		}

		listfilters = (HorizontalListView) findViewById(R.id.list_filter);
		listfilters.setAdapter(new FilterAdapter());
		getActionBar().setIcon(R.drawable.btn_cancel);
		getActionBar().setHomeButtonEnabled(true);
		gpuImageView = (GPUImageView) findViewById(R.id.gpuimage);
		lnFilter = findViewById(R.id.lnfilter);
		imgHide=findViewById(R.id.img_hide);
		imgHide.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				hideView(TIME_TRANSLATE);
			}
		});
		listfilters.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				resetAll();
				view.setSelected(true);
				switch (position) {
				case 0:
					switchFilterTo(new GPUImagePixelationFilter());
					break;
				case 1:
					switchFilterTo(new GPUImageContrastFilter(2.0f));
					break;
				case 2:
					switchFilterTo(new GPUImageSepiaFilter());
					break;
				case 3:
					GPUImageLookupFilter amatorka = new GPUImageLookupFilter();
					amatorka.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.lookup_amatorka));
					switchFilterTo(amatorka);
					break;
				case 4:
					switchFilterTo(new GPUImageGrayscaleFilter());
					break;
				case 5:
					PointF centerPoint = new PointF();
					centerPoint.x = 0.5f;
					centerPoint.y = 0.5f;
					switchFilterTo(new GPUImageVignetteFilter(centerPoint, new float[] { 0.0f, 0.0f, 0.0f }, 0.3f,
							0.75f));
					break;

				default:
					break;
				}
			}
		});

		findViewById(R.id.chooseGallery).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE);
			}
		});
		hideView(0);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_camera, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==R.id.action_switch_camera){
			 mCamera.switchCamera();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void resetAll() {
		for (int i = 0; i < 5; i++) {
			listfilters.getChildAt(i).setSelected(false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
			gpuImageView.setVisibility(View.VISIBLE);
			surface.setVisibility(View.INVISIBLE);
			handleImage(data.getData());
		} else if (requestCode == REQUEST_CROP && resultCode == RESULT_OK) {
			String path = data.getStringExtra("imagePath");
			finishCameraActivity(path);
		}
	}

	private void handleImage(final Uri selectedImage) {
		gpuImageView.setImage(selectedImage);
		resetAll();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("onresume", "onResume");
		mCamera.onResume();
		Log.d("onresume", "onResume complete");
	}

	@Override
	protected void onPause() {
		mCamera.onPause();
		super.onPause();
	}
	
	

	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		// case R.id.button_choose_filter:
		// GPUImageFilterTools.showDialog(this,
		// new OnGpuImageFilterChosenListener() {
		//
		// @Override
		// public void onGpuImageFilterChosenListener(
		// final GPUImageFilter filter) {
		// switchFilterTo(filter);
		// }
		// });
		// break;

		case R.id.button_capture:
			if (surface.getVisibility() == View.VISIBLE) {
				if (mCamera.mCameraInstance.getParameters().getFocusMode()
						.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
					takePicture();
				} else {
					mCamera.mCameraInstance.autoFocus(new Camera.AutoFocusCallback() {

						@Override
						public void onAutoFocus(final boolean success, final Camera camera) {
							takePicture();
						}
					});
				}
			} else {
				gpuImageView.saveToPictures("threemin", System.currentTimeMillis() + ".jpg",
						new GPUImageView.OnPictureSavedListener() {

							@Override
							public void onPictureSaved(Uri uri) {
//								Intent intent = new Intent();
//								intent.putExtra("imageUri", uri.toString());
//								setResult(RESULT_OK, intent);
//								finish();
								finishImage(uri);
							}
						});

			}
			break;

		case R.id.img_switch_camera:
			// mCamera.switchCamera();
			showView(TIME_TRANSLATE);
			break;
		}
	}

	private void hideView(int time) {
		TranslateAnimation ta = new TranslateAnimation(0, 0, 0, lnFilter.getHeight());
		ta.setDuration(time);
		ta.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				lnFilter.setVisibility(View.GONE);
			}
		});
		lnFilter.startAnimation(ta);
	}

	private void showView(int time) {
		TranslateAnimation ta = new TranslateAnimation(0, 0, lnFilter.getHeight(), 0);
		ta.setDuration(TIME_TRANSLATE);
		ta.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				lnFilter.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}
		});
		lnFilter.startAnimation(ta);
	}

	private Camera.Size getOptimalSize(List<Camera.Size> sizes) {
		Size optimalSize = sizes.get(0);
		for (Size size : sizes) {
			Log.i("camera", "Available resolution: " + size.width + " " + size.height);
			if (size.width > 480 && size.width < 800) {
				optimalSize = size;
				break;
			}
		}
		Log.d("camera", "Using size: " + optimalSize.width + "w " + optimalSize.height + "h");
		return optimalSize;
	}

	private void takePicture() {
		// TODO get a size that is about the size of the screen
		Camera.Parameters params = mCamera.mCameraInstance.getParameters();
		Size picturesize = getOptimalSize(params.getSupportedPictureSizes());
		params.setPictureSize(picturesize.width, picturesize.height);
		mCamera.mCameraInstance.setParameters(params);
		for (Camera.Size size2 : mCamera.mCameraInstance.getParameters().getSupportedPictureSizes()) {
			Log.i("ASDF", "Supported: " + size2.width + "x" + size2.height);
		}
		mCamera.mCameraInstance.takePicture(null, null, new Camera.PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, final Camera camera) {

				final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
				if (pictureFile == null) {
					Log.d("ASDF", "Error creating media file, check storage permissions");
					return;
				}

				BitmapFactory.Options opt;

				opt = new BitmapFactory.Options();
				opt.inTempStorage = new byte[16 * 1024];
				Parameters parameters = camera.getParameters();
				Size size = parameters.getPictureSize();

				int height11 = size.height;
				int width11 = size.width;
				float mb = (width11 * height11) / 1024000;

				if (mb > 4f) {
					opt.inSampleSize = 4;
				} else if (mb > 3f) {
					opt.inSampleSize = 2;
				}

				Bitmap bMap = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
				Bitmap bMapRotate;
				Matrix matrix = new Matrix();
				matrix.postRotate(90);
				bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);

				data = null;
				// mGPUImage.setImage(bitmap);
				final GLSurfaceView view = (GLSurfaceView) findViewById(R.id.surfaceView);
				view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
				mGPUImage.saveToPictures(bMapRotate, "threemins", System.currentTimeMillis() + ".jpg",
						new OnPictureSavedListener() {

							@Override
							public void onPictureSaved(final Uri uri) {
//								Intent intent = new Intent();
//								intent.putExtra("imageUri", uri.toString());
//								setResult(RESULT_OK, intent);
//								finish();
								finishImage(uri);
							}
						});
			}
		});
	}

	public static final int MEDIA_TYPE_IMAGE = 1;

	private static File getOutputMediaFile(final int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"threemins");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	private void switchFilterTo(final GPUImageFilter filter) {
		if (mFilter == null || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
			mFilter = filter;
			if (surface.getVisibility() == View.VISIBLE) {
				mGPUImage.setFilter(mFilter);
			} else {
				gpuImageView.setFilter(mFilter);
			}
			mFilterAdjuster = new FilterAdjuster(mFilter);
		}
	}

	@Override
	public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
		if (mFilterAdjuster != null) {
			mFilterAdjuster.adjust(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(final SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(final SeekBar seekBar) {
	}

	private class CameraLoader {
		private int mCurrentCameraId = 0;
		private Camera mCameraInstance;

		public void onResume() {
			setUpCamera(mCurrentCameraId);
		}

		public void onPause() {
			releaseCamera();
		}

		public void switchCamera() {
			releaseCamera();
			mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
			setUpCamera(mCurrentCameraId);
		}

		private void setUpCamera(final int id) {
			mCameraInstance = getCameraInstance(id);
			Parameters parameters = mCameraInstance.getParameters();
			// TODO adjust by getting supportedPreviewSizes and then choosing
			// the best one for screen size (best fill screen)
			parameters.setPreviewSize(720, 480);

			if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			}
			mCameraInstance.setParameters(parameters);

			int orientation = mCameraHelper.getCameraDisplayOrientation(ActivityCamera.this, mCurrentCameraId);
			CameraInfo2 cameraInfo = new CameraInfo2();
			mCameraHelper.getCameraInfo(mCurrentCameraId, cameraInfo);
			boolean flipHorizontal = cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT ? true : false;
			mCameraInstance.setDisplayOrientation(90);
			mGPUImage.setUpCamera(mCameraInstance, orientation, flipHorizontal, false);
		}

		/** A safe way to get an instance of the Camera object. */
		private Camera getCameraInstance(final int id) {
			Camera c = null;
			try {
				c = mCameraHelper.openCamera(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return c;
		}

		private void releaseCamera() {
			mCameraInstance.setPreviewCallback(null);
			mCameraInstance.release();
			mCameraInstance = null;
		}
	}
	
	public void finishImage(Uri picUri) {
//		Intent intent = new Intent();
//		intent.putExtra("imageUri", picUri.toString());
//		setResult(RESULT_OK, intent);
//		finish();
		
//		performCrop(picUri);
		
		Intent intent = new Intent(this, CropImageActivity.class);
		intent.putExtra("imageUri", picUri.toString());
		startActivityForResult(intent, REQUEST_CROP);
	}
	
	public void finishCameraActivity(String path) {
		Intent intent = new Intent();
		intent.putExtra("imagePath", path);
		setResult(RESULT_OK, intent);
		finish();
	}

}
