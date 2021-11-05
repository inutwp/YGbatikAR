package com.inudev.ygbatikar.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.aquery.AQuery;
import com.bumptech.glide.Glide;
import com.inudev.ygbatikar.model.Shop;
import com.inudev.ygbatikar.model.ShopModel;
import com.inudev.ygbatikar.utils.ApiBuilder;
import com.inudev.ygbatikar.utils.ApiService;
import com.inudev.ygbatikar.R;
import com.inudev.ygbatikar.adapter.CariPertokoanAdapter;
import com.inudev.ygbatikar.holder.CariPertokoanHolder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Callback;
import retrofit2.Response;

public class CariPertokoanActivity extends AppCompatActivity {

    private static final String TAG = "CariPertokoanActivity";
    public NetworkInfo mNetworkInfo;
    ImageView checkConnectionImage;
    TextView filterJamBuka, filterAlamat, filterNama;
    private RecyclerView recyclerViewCariPertokoan;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SearchView mSearchView;
    private ConnectivityManager mConnectivityManager;
    private AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_pertokoan);
        aq = new AQuery(this);

        initCheckInternetCon();
        initSearchView();
        initRecycleView();
        initSwipeRefresh();
        initResponseAPI();

    }

    private void initCheckInternetCon() {
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert mConnectivityManager != null;
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isAvailable() && mNetworkInfo.isConnected()) {
            action(true);
        } else {
            action(false);
        }
    }

    private void action(boolean isConnected) {
        if (isConnected) {
            aq.id(R.id.avload).show();
            aq.id(R.id.loading_text_cari_pertokoan).show();
        } else {
            aq.id(R.id.avload).show();
            aq.id(R.id.loading_text_cari_pertokoan).show();
            aq.id(R.id.refresh_check_connection).text("Refresh").click(view -> {
                Intent refreshIntent = new Intent(CariPertokoanActivity.this, CariPertokoanActivity.class);
                startActivity(refreshIntent);
                finish();
            });
            aq.id(R.id.TV_check).text("Sepertinya koneksi anda terputus");
            aq.id(R.id.TV_check2).text("Pastikan anta terhubung dengan koneksi internet dan coba lagi");
            checkConnectionImage = findViewById(R.id.image_checkInternet);
            Glide.with(this)
                    .load(R.drawable.internetconnection)
                    .into(checkConnectionImage);
            mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    private void initSwipeRefresh() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this::initResponseAPI);
    }

    private void initRecycleView() {
        recyclerViewCariPertokoan = findViewById(R.id.recycle_view_cari_pertokoan);
    }

    private void initSearchView() {
        mSearchView = findViewById(R.id.SV_cari_pertokoan);
        mSearchView.setIconifiedByDefault(true);
    }

    private void initResponseAPI() {
        try {
            ApiService service = ApiBuilder.getClient().create(ApiService.class);
            retrofit2.Call<ShopModel> ShopModelCall = service.getPertokoanData();
            ShopModelCall.enqueue(new Callback<ShopModel>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<ShopModel> call, @NonNull Response<ShopModel> response) {
                    CariPertokoanActivity.this.runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            List<Shop> mShop = Objects.requireNonNull(response.body()).getShops();
                            aq.id(R.id.avload).hide();
                            aq.id(R.id.loading_text_cari_pertokoan).hide();
                            final CariPertokoanAdapter cariPertokoanAdapter = new CariPertokoanAdapter(mShop) {
                                @Override
                                protected void bindHolder(CariPertokoanHolder holder, final Shop mShop) {
                                    holder.bind(mShop);
                                    holder.itemView.setOnClickListener(view -> {
                                        Intent intent_view = new Intent(view.getContext(), SingleArviewActivity.class);
                                        intent_view.putExtra("Shop", mShop);
                                        startActivity(intent_view);
                                    });
                                }
                            };

                            CariPertokoanActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerViewCariPertokoan.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    recyclerViewCariPertokoan.setAdapter(cariPertokoanAdapter);
                                    mSwipeRefreshLayout.setRefreshing(false);
                                    initFilterCari();

                                    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                        @Override
                                        public boolean onQueryTextSubmit(String s) {
                                            cariPertokoanAdapter.getFilter().filter(s);
                                            return true;
                                        }

                                        @Override
                                        public boolean onQueryTextChange(String s) {
                                            cariPertokoanAdapter.getFilter().filter(s);
                                            return true;
                                        }
                                    });
                                }

                                private void initFilterCari() {
                                    aq.id(R.id.filterJambuka).click(view -> {
                                        boolean isSelectedAfterClick = !view.isSelected();
                                        view.setSelected(isSelectedAfterClick);
                                        if (isSelectedAfterClick) {
                                            filterJamBuka = findViewById(R.id.filterJambuka);
                                            filterJamBuka.setTextColor(ContextCompat.getColor(view.getContext(), R.color.Primary_Light_Material_Navigation));
                                            filterAlamat = findViewById(R.id.filterAlamat);
                                            filterAlamat.setTextColor(Color.WHITE);
                                            filterNama = findViewById(R.id.filterNama);
                                            filterNama.setTextColor(Color.WHITE);
                                            mShop.remove(ShopModelCall);
                                            cariPertokoanAdapter.notifyDataSetChanged();
                                            Collections.sort(mShop, (filter1a, filter1b) -> filter1a.getShopOpenTime().compareTo(filter1b.getShopCloseTime()));
                                        }
                                    });

                                    aq.id(R.id.filterAlamat).click(view -> {
                                        boolean isSelectedAfterClickTwo = !view.isSelected();
                                        view.setSelected(isSelectedAfterClickTwo);
                                        if (isSelectedAfterClickTwo) {
                                            filterAlamat = findViewById(R.id.filterAlamat);
                                            filterAlamat.setTextColor(ContextCompat.getColor(view.getContext(), R.color.Primary_Light_Material_Navigation));
                                            filterJamBuka = findViewById(R.id.filterJambuka);
                                            filterJamBuka.setTextColor(Color.WHITE);
                                            filterNama = findViewById(R.id.filterNama);
                                            filterNama.setTextColor(Color.WHITE);
                                            mShop.remove(ShopModelCall);
                                            cariPertokoanAdapter.notifyDataSetChanged();
                                            Collections.sort(mShop, (filter2a, filter2b) -> filter2a.getShopAddress().compareTo(filter2b.getShopAddress()));
                                        }
                                    });

                                    aq.id(R.id.filterNama).click(view -> {
                                        boolean isSelectedAfterClickThree = !view.isSelected();
                                        view.setSelected(isSelectedAfterClickThree);
                                        if (isSelectedAfterClickThree) {
                                            filterNama = findViewById(R.id.filterNama);
                                            filterNama.setTextColor(ContextCompat.getColor(view.getContext(), R.color.Primary_Light_Material_Navigation));
                                            filterJamBuka = findViewById(R.id.filterJambuka);
                                            filterJamBuka.setTextColor(Color.WHITE);
                                            filterAlamat = findViewById(R.id.filterAlamat);
                                            filterAlamat.setTextColor(Color.WHITE);
                                            mShop.remove(ShopModelCall);
                                            cariPertokoanAdapter.notifyDataSetChanged();
                                            Collections.sort(mShop, (filter3a, filter3b) -> filter3a.getShopName().compareTo(filter3b.getShopName()));
                                        }
                                    });
                                }
                            });
                        } else {
                            CariPertokoanActivity.this.runOnUiThread(() -> {
                                try {
                                    aq.id(R.id.avload).hide();
                                    aq.id(R.id.loading_text_cari_pertokoan).hide();
                                } catch (IllegalStateException ise) {
                                    ise.printStackTrace();
                                }
                            });
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<ShopModel> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: ", t);
                    try {
                        CariPertokoanActivity.this.runOnUiThread(() -> {
                            try {
                                aq.id(R.id.avload).hide();
                                aq.id(R.id.loading_text_cari_pertokoan).hide();
                            } catch (IllegalStateException ise) {
                                ise.printStackTrace();
                            }
                        });
                    } catch (NullPointerException n) {
                        Log.e(TAG, "onFailure: ", n);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "initResponseAPI: " + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnectivityManager.getActiveNetworkInfo();
    }

    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(this);
    }
}
