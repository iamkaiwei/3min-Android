package com.threemin.fragment;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidanimator.animation.Animation;
import com.androidanimator.animation.AnimationListener;
import com.androidanimator.animation.HighlightAnimation;
import com.facebook.widget.LoginButton;
import com.google.gson.Gson;
import com.threemin.adapter.ProductGridAdapter;
import com.threemin.app.DetailActivity;
import com.threemin.app.HomeActivity;
import com.threemin.app.ImageViewActivity;
import com.threemin.model.CategoryModel;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.uti.WebserviceConstant;
import com.threemin.view.QuickReturnGridView;
import com.threemin.view.QuickReturnGridView.OnItemDoubleTapLister;
import com.threemin.webservice.ProductWebservice;
import com.threemin.webservice.UploaderImageUlti;
import com.threemin.webservice.UserWebService;
import com.threemins.R;

public class HomeFragment extends BaseProductFragment {
    
    public static final String tag = "HomeFragment";
    
    public static final int MODE_LIST_VIEW = 1;
    public static final int MODE_GRID_VIEW = 2;
    private static final int REQUEST_UPLOAD = 3;

    public static int STEP_INIT = 0;
    public static int STEP_ADDMORE = 1;
    public static int STEP_REFRESH = 2;
    static int page;
    View bottomView;
    static protected CategoryModel currentCate;
    static protected List<ProductModel> productModels;
    private Context mContext;
    private LoginButton mLoginButton;

    // no items:
    private RelativeLayout rlNoItems;
    private boolean isSwitched;

    private QuickReturnGridView mGrid;
    private ProductGridAdapter mAdapter;

    private int bottomHeight;
    
    public HomeFragment() {
        super();
        this.isSwitched = false;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home_fix_bug, null);        
        bottomView = v.findViewById(R.id.bottom);
        v.findViewById(R.id.home_camera).setOnClickListener(onSellClick());
        
        mLoginButton = getLoginButton();
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_gridview);
        int color = R.color.red_background;
        int color2 = R.color.common_grey;
        swipeLayout.setColorScheme(color, color2, color, color2);
        rlNoItems = (RelativeLayout) v.findViewById(R.id.fragment_product_gridview_layout_no_items);
        mGrid = (QuickReturnGridView) v.findViewById(R.id.gv_product);
        
        mContext=getActivity();
        if (mAdapter == null) {
            // TODO
            mAdapter = new ProductGridAdapter(productModels, mContext, mLoginButton, CommonUti.calcWidthItem(mContext));
        }
        mGrid.setAdapter(mAdapter);

        initListner();
        if (isSwitched) {
            changeIfNoItem();
        }
        return v;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        bottomView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (bottomHeight == 0) {
                    bottomHeight = bottomView.getHeight();
                    Log.d("bottom", "height=" + bottomHeight);
                }
            }
        });
    }


    //method of BaseProductFragment
    @Override
    public void updateUI() {
        if (mAdapter == null) {
            // TODO
            mAdapter = new ProductGridAdapter(productModels, 
                                                mContext, 
                                                mLoginButton,
                                                CommonUti.calcWidthItem(mContext));
        }
        mAdapter.updateData(productModels);
        changeIfNoItem();
    }

    //method of BaseProductFragment
    @Override
    public void changeIfNoItem() {
        if (rlNoItems == null || mGrid == null) {
            return;
        }
//        if (mAdapter.getListProducts() == null || mAdapter.getListProducts().size() == 0) {
        if (productModels == null || productModels.size() == 0) {
            rlNoItems.setVisibility(View.VISIBLE);
            mGrid.setVisibility(View.GONE);
        } else {
            rlNoItems.setVisibility(View.GONE);
            mGrid.setVisibility(View.VISIBLE);
        }
    }
    
    public class GetProductTaks extends AsyncTask<Integer, Void, List<ProductModel>> {
        int currentStep;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (swipeLayout != null) {
                swipeLayout.setRefreshing(true);
            }
        }

        @Override
        protected List<ProductModel> doInBackground(Integer... params) {
            currentStep = params[0];
            if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
                page = 1;
                thelasttotalCount = 0;
            }
            if (currentStep == STEP_ADDMORE) {
                page++;
            }
            String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
            try {
                return new ProductWebservice().getProduct(tokken, currentCate, page);
            } catch (Exception e) {
                Log.i(tag, "get product ex: " + e.toString());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ProductModel> result) {
            super.onPostExecute(result);
            if (swipeLayout != null) {
                swipeLayout.setRefreshing(false);
            }
            if (result != null && result.size() > 0) {
                if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
                    productModels = result;
                    setProductModels(result);
                } else if (currentStep == STEP_ADDMORE) {
                    productModels.addAll(result);
                    setProductModels(productModels);
                } else {
                }

            } else if (currentStep == STEP_INIT) {
                setProductModels(result);
                productModels = result;
            } 
            
            changeIfNoItem();
        }
    }

    public void addNewProducts(ProductModel result, CategoryModel categoryModel) {
        if (currentCate == null || currentCate.getId() == categoryModel.getId()) {
            productModels.add(0, result);
            updateUI();
        }
    }

    private OnClickListener onSellClick() {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), ImageViewActivity.class), REQUEST_UPLOAD);
            }
        };
    }
    
    public void onSwichCategory(CategoryModel categoryModel) {
        currentCate = categoryModel;
        new GetProductTaks().execute(STEP_INIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        if (requestCode == REQUEST_UPLOAD) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i("HomeFragment", data.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA));
                ProductModel productModel = new Gson().fromJson(
                        data.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA), ProductModel.class);
                new UploadProduct((HomeActivity) getActivity()).execute(productModel);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class UploadProduct extends AsyncTask<ProductModel, Void, ProductModel> {
        CategoryModel categoryModel;

        public UploadProduct(HomeActivity activity) {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getRefreshLayout().setRefreshing(true);
        }

        @Override
        protected void onPostExecute(ProductModel result) {
            super.onPostExecute(result);
            if (getActivity() != null)  {
                getRefreshLayout().setRefreshing(false);
                if (result != null) {
                    addNewProducts(result, categoryModel);
                } else {
                    Toast.makeText(getActivity(), R.string.error_upload, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected ProductModel doInBackground(ProductModel... params) {
            ProductModel model = params[0];
            String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
            categoryModel = model.getCategory();
            HttpResponse reponese = new UploaderImageUlti().uploadUserPhoto(WebserviceConstant.CREATE_PRODUCT, model,
                    tokken);
            if (reponese != null) {
                HttpEntity r_entity = reponese.getEntity();
                String responseString;
                try {
                    responseString = EntityUtils.toString(r_entity);
                    Log.d("UPLOAD", responseString);
                    JSONObject resultObject = new JSONObject(responseString);

                    ProductModel list = new Gson().fromJson(resultObject.optJSONObject("product").toString(),
                            ProductModel.class);
                    return list;
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                }
            }
            return null;
        }
    }
    
    public LoginButton getLoginButton() {
        return ((HomeActivity)getActivity()).getLoginButton();
    }
    
    int mLastFirstVisibleItem;
    boolean mIsScrollingUp;
    private void initListner() {
        swipeLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                new GetProductTaks().execute(HomeFragment.STEP_REFRESH);
            }
        });

        mGrid.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == 0)
                    Log.i("a", "scrolling stopped...");

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount - 1;
                if (loadMore && totalItemCount > 1 && thelasttotalCount != totalItemCount) {
                    thelasttotalCount = totalItemCount;
                    new GetProductTaks().execute(HomeFragment.STEP_ADDMORE);
                }

                if (view.getId() == mGrid.getId()) {
                    final int currentFirstVisibleItem = mGrid.getFirstVisiblePosition();
                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        // Log.i("a", "scrolling down...");
                        if (mIsScrollingUp) {
                            mIsScrollingUp = false;
                           ObjectAnimator animator= ObjectAnimator.ofFloat(bottomView, "TranslationY", bottomHeight);
                           animator.setDuration(300);
                           animator.setInterpolator(new OvershootInterpolator());
                           animator.start();
//                            bottomView.setTranslationY(bottomHeight);
                        }
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        // Log.i("a", "scrolling up...");
                        if (!mIsScrollingUp) {
                            mIsScrollingUp = true;
//                            bottomView.setTranslationY(0);
                            ObjectAnimator animator= ObjectAnimator.ofFloat(bottomView, "TranslationY", 0);
                            animator.setDuration(300);
                            animator.setInterpolator(new OvershootInterpolator());
                            animator.start();
                        }
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
                // handleQuickReturn();
            }
        });

        mGrid.setOnItemDoubleClickListener(new OnItemDoubleTapLister() {

            @Override
            public void OnSingleTap(AdapterView parent, View view, int position, long id) {
                ProductModel model = (ProductModel) mGrid.getItemAtPosition(position);
                if (model != null) {
                    String data = new Gson().toJson(model);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
                    getActivity().startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_no_animation);
                    CommonUti.addAnimationWhenStartActivity(getActivity());
                }
            }

            @Override
            public void OnDoubleTap(AdapterView parent, View view, int position, long id) {
                ProductModel model = (ProductModel) mGrid.getItemAtPosition(position);
                if (model != null) {
                    requestLike(model, view);
                }
            }
        });
    }
    
    private void requestLike(final ProductModel productModel, View view) {
        final String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();

        productModel.setLiked(!productModel.isLiked());
        if (productModel.isLiked()) {
            productModel.setLike(productModel.getLike() + 1);
        } else {
            productModel.setLike(productModel.getLike() - 1);
        }

        TextView tv_like = (TextView) view.findViewById(R.id.inflater_body_product_grid_tv_like);
        if (productModel.getLike() > 0) {
            tv_like.setText("" + productModel.getLike());
        } else {
            tv_like.setText("");
        }

        ImageView img_like = (ImageView) view.findViewById(R.id.inflater_body_product_grid_img_like);
        if (productModel.isLiked()) {
            img_like.setSelected(true);
        } else {
            img_like.setSelected(false);
        }

        // mAdapter.notifyDataSetChanged();
        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                boolean result = new UserWebService().productLike(productModel.getId(), tokken, productModel.isLiked());
                Log.d("result", "result=" + result);
            }
        });
        LinearLayout animationView = (LinearLayout) view.findViewById(R.id.animation_linear_layout);
//        it crash here if we use the given view by the onitemclick interface
//        new HighlightAnimation(view)
//        .setDuration(200)
//        .setListener(new AnimationListener() {
//            
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                // TODO Auto-generated method stub
//                t.start();
//            }
//        }).animate();
        
        new HighlightAnimation(animationView)
        .setDuration(200)
        .setListener(new AnimationListener() {
            
            @Override
            public void onAnimationEnd(Animation animation) {
                t.start();
            }
        }).animate();
    }

}
