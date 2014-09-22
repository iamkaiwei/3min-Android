package com.threemin.app;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.threemin.adapter.CategoryAdapter;
import com.threemin.model.CategoryModel;
import com.threemin.uti.CommonConstant;
import com.threemin.webservice.CategoryWebservice;
import com.threemins.R;

public class CategoryActivity extends ListActivity {
	
	Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext=this;
		new InitCategory().execute();
		initActionBar();
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick (AdapterView<?> parent, View view, int position, long id){
				CategoryModel model=(CategoryModel) getListView().getItemAtPosition(position);
				String data=new Gson().toJson(model);
				Intent intent = new Intent();
				intent.putExtra(CommonConstant.INTENT_CATEGORY_DATA, data);
				setResult(RESULT_OK, intent);
				finish();
				
			}
		});
	};
	
	public void initActionBar() {
		getActionBar().setIcon(R.drawable.btn_cancel);
		getActionBar().setHomeButtonEnabled(true);
//		bar.setCustomView(R.layout.actionbar_activity_imageview_custom_view);
//		bar.setDisplayShowCustomEnabled(true);
	}
	


	private class InitCategory extends AsyncTask<Void, Void, List<CategoryModel>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<CategoryModel> doInBackground(Void... arg0) {
			try {
				return CategoryWebservice.getInstance().getTaggableCategory(mContext);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<CategoryModel> result) {
			if (result != null) {
				Log.i("tructran", "Set adapter");
//				mAdapter = new ArrayAdapter<CategoryModel>(mContext, android.R.layout.simple_spinner_item, mListCategory);
//				mAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
//				mSpnCategory.setAdapter(mAdapter);
//				mSpnCategory.setOnItemSelectedListener(onItemSpinnerSelected);
//				CategoryAdapter adapter=new CategoryAdapter(CategoryActivity.this, result, false, null);
				boolean hideSelectedItem = false;
              CategoryAdapter adapter=new CategoryAdapter(CategoryActivity.this, result, hideSelectedItem);
				getListView().setAdapter(adapter);
			}
			super.onPostExecute(result);
		}
	}
}
