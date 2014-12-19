package com.threemin.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.CategoryModel;
import com.threemin.model.ImageModel;
import com.threemins.R;

public class CategoryAdapter extends BaseAdapter {
    
    public static final String tag = "CategoryAdapter";
    
    static class ViewHolder {
        public ImageView ivCategoryIcon;
        public TextView tvCategoryName;
        public View vDivider;
    }

	Context mContext;
	List<CategoryModel> data;
	int selectedCate;
	boolean hideSelectedItem = false;

    public CategoryAdapter(Context context, List<CategoryModel> data, boolean hideSelectedItem) {
		mContext = context;
		selectedCate = 0;
		this.data = data;
		this.hideSelectedItem = hideSelectedItem;
	}


	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < data.size()) {
			return data.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
	    
	    if (convertView == null) {
	        LayoutInflater inflater = LayoutInflater.from(mContext);
	        convertView = inflater.inflate(R.layout.inflater_category, parent, false);
	        
	        ViewHolder vh = new ViewHolder();
	        vh.ivCategoryIcon =    (ImageView) convertView.findViewById(R.id.inflater_cate_image);
	        vh.tvCategoryName =    (TextView) convertView.findViewById(R.id.inflater_cate_tv_name);
	        vh.vDivider =          convertView.findViewById(R.id.inflater_cate_list_divider);
	        
	        convertView.setTag(vh);
        }
	    
	    CategoryModel model = data.get(position);
	    ViewHolder holder = (ViewHolder) convertView.getTag();
	    Log.i(tag, new Gson().toJson(model).toString());

        Log.i("CateID", "" + position + ": " + model.getId() + "Name: " + model.getName());

        
        if (model.getId() != 0) {
            Log.d("postion", "" + position);
            ImageModel imgModel = model.getImage();
            if (imgModel != null) {
                UrlImageViewHelper.setUrlDrawable(holder.ivCategoryIcon, model.getImage().getUrl(), R.drawable.stuff_img);
            } else {
                holder.ivCategoryIcon.setImageResource(R.drawable.stuff_img);
            }
        } else {
            holder.ivCategoryIcon.setImageResource(R.drawable.ic_everything);
        }

        holder.tvCategoryName.setText(data.get(position).getName());
        
        //because we use view holder, after set height = 1 to hide selected item,
        //we have to reset height to original size in other ones
        if (selectedCate == position && hideSelectedItem) {
            LayoutParams params = convertView.getLayoutParams();
            params.height = 1;
            convertView.setLayoutParams(params);
            holder.vDivider.setVisibility(View.GONE);
        } else {
            LayoutParams params = convertView.getLayoutParams();
            params.height = mContext.getResources().getDimensionPixelSize(R.dimen.inflater_category_row_height);
            convertView.setLayoutParams(params);
            holder.vDivider.setVisibility(View.VISIBLE);
        }
        return convertView;

    }

	public void swapView(int position) {
		selectedCate = position;
	}

}
