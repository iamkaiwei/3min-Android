package com.threemin.fragment;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.threemin.adapter.CategoryAdapter;
import com.threemin.app.HomeActivity;
import com.threemin.model.CategoryModel;
import com.threemin.webservice.CategoryWebservice;
import com.threemins.R;

public class LeftFragment extends Fragment {

	ListView lvCategory;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_left, container, false);
		Log.i("tructran", "left fragment");
		lvCategory=(ListView) rootView.findViewById(R.id.list_cate);
		new InitCategory().execute();
		lvCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HomeActivity homeActivity=(HomeActivity) getActivity();
				CategoryModel categoryModel;
				if(position==0){
					categoryModel=null;
				} else {
					categoryModel=(CategoryModel) parent.getItemAtPosition(position);
				}
				homeActivity.onSwitchCate(categoryModel);
			}
		});
		return rootView;
	}

	public void setMenuVisibility(final boolean visible) {
		super.setMenuVisibility(visible);
		if (visible) {
			Log.i("tructran", "left fragment");
		}
	}
	

	private class InitCategory extends AsyncTask<Void, Void, List<CategoryModel>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<CategoryModel> doInBackground(Void... arg0) {
			try {
				return CategoryWebservice.getInstance().getAllCategory(getActivity());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<CategoryModel> result) {
			if (result != null) {
				
				CategoryAdapter adapter = new CategoryAdapter(getActivity(), result,false);
				lvCategory.setAdapter(adapter);
			}
			super.onPostExecute(result);
		}
	}

}