package com.threemin.webservice;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.threemin.model.ImageModel;
import com.threemin.model.ProductModel;

import android.text.TextUtils;
import android.util.Log;

public class UploaderImageUlti {
	private DefaultHttpClient mHttpClient;

	public UploaderImageUlti() {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		mHttpClient = new DefaultHttpClient(params);
	}

	public HttpResponse uploadUserPhoto(String url, ProductModel model,String tokken) {

		try {

			HttpPost httppost = new HttpPost(url);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();

			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

			/* example for adding an image part */
			for (ImageModel imageModel : model.getImages()) {
				ContentType contentType = ContentType.create("image/jpeg");
				File imageFile = new File(imageModel.getUrl());
				FileBody fileBody = new FileBody(imageFile, contentType, imageFile.getName());
				builder.addPart("images[]", fileBody);
			}
			 if (!TextUtils.isEmpty(model.getDescription())) {
			 builder.addTextBody("description", model.getDescription());
			 }
			 builder.addTextBody("access_token", tokken);
			 builder.addTextBody("user_id", model.getOwner().getId()+"");
			 builder.addTextBody("name", model.getName());
			 builder.addTextBody("price", model.getPrice());
			 builder.addTextBody("category_id", model.getCategory().getId()+"");
			//
			// if (!TextUtils.isEmpty(twitterScrecetToken)) {
			// builder.addTextBody("twitter_secret", twitterScrecetToken);
			// }
			//
			// if (!TextUtils.isEmpty(caption)) {
			// builder.addTextBody("caption", caption);
			// } else {
			// builder.addTextBody("caption", " ");
			// }

			httppost.setEntity(builder.build());

			return mHttpClient.execute(httppost);
		} catch (Exception e) {
			Log.e(UploaderImageUlti.class.getName(), e.getLocalizedMessage(), e);
			return null;
		}
	}

}
