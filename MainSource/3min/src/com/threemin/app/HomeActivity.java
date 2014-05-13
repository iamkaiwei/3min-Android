package com.threemin.app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.threemin.adapter.CategoryAdapter;
import com.threemin.model.CategoryModel;
import com.threemin.uti.CommonUti;
import com.threemin.webservice.AuthorizeWebservice;
import com.threemin.webservice.CategoryWebservice;
import com.threemins.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;

public class HomeActivity extends Activity{

	Context mContext;
	ListView lvCategory;
	
	String fb_tokken="CAACq3f9R9LsBAF6tNZBLDg3eGnXQPrwQLnSVjZCVIt4AZBnxvKhebLWBk65ZAGarUElG5ZC4vZBmP0ECYILkBhNEqzkzxX4BOZBY0DHEi4rN3GcuGqfx6EqWjAeE1ZAZApYUhe4aEIYqvauu393iTJ9k5sIC5Gyc4atubV9xCNxNthB1PhaUPEwZAZAZCycHweGqM9ZCe6ivJk3LfjaU9DZBmRFjO1kvYN05O2TrvqwrheoYp7lgZDZD";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mContext=this;
		lvCategory=(ListView) findViewById(R.id.home_left_drawer);
		new InitCategory().execute();
		
		 try {
		        PackageInfo info = getPackageManager().getPackageInfo(
		                getPackageName(), 
		                PackageManager.GET_SIGNATURES);
		        for (Signature signature : info.signatures) {
		            MessageDigest md = MessageDigest.getInstance("SHA");
		            md.update(signature.toByteArray());
		            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
		            }
		    } catch (NameNotFoundException e) {

		    } catch (NoSuchAlgorithmException e) {

		    }
	}
	
	private class InitCategory extends AsyncTask<Void, Void, List<CategoryModel>>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected List<CategoryModel> doInBackground(Void... arg0) {
			try {
				new AuthorizeWebservice().login(fb_tokken, CommonUti.getDeviceId(mContext), mContext);
				return CategoryWebservice.getInstance().getAllCategory(mContext);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(List<CategoryModel> result) {
			if(result!=null){
				CategoryAdapter adapter=new CategoryAdapter(mContext, result);
				lvCategory.setAdapter(adapter);
			}
			super.onPostExecute(result);
		}
	}
}
