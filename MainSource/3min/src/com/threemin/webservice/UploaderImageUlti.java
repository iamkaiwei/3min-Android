package com.threemin.webservice;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.Telephony.TextBasedSmsColumns;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.threemin.model.ImageModel;
import com.threemin.model.ProductModel;

public class UploaderImageUlti {
    private final String tag = "UploaderImageUlti";

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
            addProductInfo(builder, tokken, model);

            httppost.setEntity(builder.build());
            return mHttpClient.execute(httppost);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("UploaderImageUlti", e.toString());
            return null;
        }
    }

    public void addProductInfo(MultipartEntityBuilder builder, String tokken, ProductModel model) {
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
    }

    public ProductModel updateUserPhoto(String url, ProductModel model, String tokken) {

        try {

            HttpPut httpPut = new HttpPut(url);
            MultipartEntityBuilder builder = createMultipartEntityBuilder(model, tokken);
            httpPut.setEntity(builder.build());

            HttpResponse response = mHttpClient.execute(httpPut);
            if (response != null) {
                HttpEntity r_entity = response.getEntity();
                String responseString;
                try {
                    responseString = EntityUtils.toString(r_entity);
                    Log.d(tag, "updateUserPhoto response: " + responseString);
                    JSONObject resultObject = new JSONObject(responseString);

                    ProductModel result = new Gson().fromJson(resultObject.optJSONObject("product").toString(),
                            ProductModel.class);
                    return result;
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d(tag, "updateUserPhoto ex: " + e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(tag, "updateUserPhoto ex: " + e.toString());
                } catch (JSONException e) {
                    Log.d(tag, "updateUserPhoto ex: " + e.toString());
                }
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(tag, e.toString());
            return null;
        }
    }

    // use to update product
    public MultipartEntityBuilder createMultipartEntityBuilder(ProductModel model, String tokken) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        /* example for adding an image part */
        int index = 0;
        for (ImageModel imageModel : model.getImages()) {
            int type = imageModel.getTypeEditProduct();
            Log.i(tag, "ID: " + imageModel.getId() + " type: " + type);
            if (type == ImageModel.TYPE_EDIT_PRODUCT_UPDATE || type == ImageModel.TYPE_EDIT_PRODUCT_DELETE) {
                builder.addTextBody("images[" + index + "][id]", "" + imageModel.getId());
            } else if (type == ImageModel.TYPE_EDIT_PRODUCT_NO_CHANGE) {
                continue;
            }

            if (type == ImageModel.TYPE_EDIT_PRODUCT_DELETE) {
                builder.addTextBody("images[" + index + "][_destroy]", "1");
            } else {
                ContentType contentType = ContentType.create("image/jpeg");
                File imageFile = new File(imageModel.getUrl());
                FileBody fileBody = new FileBody(imageFile, contentType, imageFile.getName());
                builder.addPart("images[" + index + "][content]", fileBody);
            }

            index++;
        }
        addProductInfo(builder, tokken, model);
        return builder;
    }

}
