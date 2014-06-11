package com.threemin.webservice;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import android.text.TextUtils;

import com.threemin.model.ImageModel;
import com.threemin.model.ProductModel;

public class UploaderImageUlti {
	private DefaultHttpClient mHttpClient;
	private final int TIMEOUT = 60000;

	public UploaderImageUlti() {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		mHttpClient = new DefaultHttpClient(params);
	}

	public HttpResponse uploadUserPhoto(String url, ProductModel model, String tokken) {

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
			builder.addTextBody("user_id", model.getOwner().getId() + "");
			builder.addTextBody("name", model.getName());
			builder.addTextBody("price", model.getPrice());
			builder.addTextBody("category_id", model.getCategory().getId() + "");
			if (model.getVenueName() != null) {
				builder.addTextBody("venue_id", model.getVenueId() + "");
				builder.addTextBody("venue_name", model.getVenueName());
				builder.addTextBody("venue_long", model.getVenueLong() + "");
				builder.addTextBody("venue_lat", model.getVenueLat() + "");
			}

			httppost.setEntity(builder.build());
			return mHttpClient.execute(httppost);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
