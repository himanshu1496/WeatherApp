package com.himanshubaghel.dev.mausam.activity;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.himanshubaghel.dev.mausam.DividerItemDecoration;
import com.himanshubaghel.dev.mausam.R;
import com.himanshubaghel.dev.mausam.adapter.ForecastListAdapter;
import com.himanshubaghel.dev.mausam.interfaces.RetrofitInterface;
import com.himanshubaghel.dev.mausam.model.Forecast;
import com.himanshubaghel.dev.mausam.model.Item;
import com.himanshubaghel.dev.mausam.model.WeatherModel;
import com.himanshubaghel.dev.mausam.utils.Constants;
import com.himanshubaghel.dev.mausam.utils.Methods;
import com.himanshubaghel.dev.mausam.utils.MyLocation;
import com.himanshubaghel.dev.mausam.utils.PreferenceUtils;
import com.himanshubaghel.dev.mausam.utils.WrappingLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class WeatherActivity extends AppCompatActivity {

    final static long WEATHER_UPDATE_INTERVAL = 15 * 60 * 1000;  //15 minute in milli seconds
    Toolbar toolbar;
    TextView tvToolBarTitle;
    TextView tvLastUpdatedTime;
    RecyclerView forecastListView;
    ForecastListAdapter adapter;
    List<Forecast> forecastList;
    CardView cvForecastView;
    TextView tvWeatherCondition;
    TextView tvMinTemp;
    TextView tvMaxTemp;
    SimpleDraweeView ivWeatherIcon;
    TextView tvCurrentTemp;
    RelativeLayout rlCurrentCondition;
    CardView cvWindAndPressure;
    TextView tvWindSpeed;
    TextView tvPressure;
    SwipeRefreshLayout swipeRefreshLayout;
    RetrofitInterface service;
    Call<WeatherModel> retrofitCall;
    MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
        @Override
        public void gotLocation(Location location) {
            if (location != null) {
                fetchWeatherData(location);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvToolBarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        tvLastUpdatedTime = (TextView) findViewById(R.id.tvToolbarLastUpdatedTime);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        forecastListView = (RecyclerView) findViewById(R.id.forecastListView);
        tvWeatherCondition = (TextView) findViewById(R.id.tvWeatherCondition);
        tvCurrentTemp = (TextView) findViewById(R.id.tvCurrentTemp);
        tvMaxTemp = (TextView) findViewById(R.id.tvMaxTemp);
        tvMinTemp = (TextView) findViewById(R.id.tvMinTemp);
        ivWeatherIcon = (SimpleDraweeView) findViewById(R.id.ivWeatherIcon);
        cvForecastView = (CardView) findViewById(R.id.cvForecastDetail);
        rlCurrentCondition = (RelativeLayout) findViewById(R.id.rlCurrentCondition);
        cvWindAndPressure = (CardView) findViewById(R.id.cvWindAndPressure);
        tvWindSpeed = (TextView) findViewById(R.id.tvWindSpeed);
        tvPressure = (TextView) findViewById(R.id.tvPressure);

        forecastListView.addItemDecoration(
                new DividerItemDecoration(WeatherActivity.this, R.drawable.divider));
        forecastListView.setNestedScrollingEnabled(false);
        forecastListView.setHasFixedSize(false);
        forecastListView.setLayoutManager(new WrappingLinearLayoutManager(this));
        forecastList = new ArrayList<>();
        adapter = new ForecastListAdapter(forecastList, WeatherActivity.this);
        forecastListView.setAdapter(adapter);

        //setting top margin dynamically based on device height
        int height = getWindowManager().getDefaultDisplay().getHeight();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlCurrentCondition.getLayoutParams();
        layoutParams.setMargins(0, height / 2, 0, 10);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateLocation();
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(R.color.cold_blue,
                android.R.color.holo_blue_light);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RetrofitInterface.class);

        //Checking to call update location function for first time on loading activity, only if it's been old enough data
        if (System.currentTimeMillis() - PreferenceUtils.getLastUpdatedTimeInMillis() > WEATHER_UPDATE_INTERVAL) {
            updateLocation();
        }

        //Updating the UI if has previously loaded data
        if (PreferenceUtils.getWeatherModel() != null) {
            updateUI(PreferenceUtils.getWeatherModel());
        }
    }

    void updateUI(WeatherModel weatherModel){
        //toolbar.setTitle(weatherModel.getQuery().getResults().getChannel().getTitle());
        tvToolBarTitle.setText(weatherModel.getQuery().getResults().getChannel().getTitle());
        tvLastUpdatedTime.setVisibility(View.VISIBLE);
        String updatedTime = "Last updated at " + Methods.formateCalenderToDateTimeString(PreferenceUtils.getLastUpdatedTimeInMillis());
        tvLastUpdatedTime.setText(updatedTime);
        try{
            Item item = weatherModel.getQuery().getResults().getChannel().getItem();
            Forecast forecast = item.getForecast().get(0);
            tvMinTemp.setText(forecast.getLow()+(char) 0x00B0);
            tvMaxTemp.setText(forecast.getHigh()+(char) 0x00B0);
            tvWeatherCondition.setText(item.getCondition().getText());
            tvCurrentTemp.setText(item.getCondition().getTemp() + (char) 0x00B0);
            Uri imageUri = Uri.parse(Constants.weatherIconBaseUrl + item.getCondition().getCode()+".gif");
            ivWeatherIcon.setImageURI(imageUri);
            rlCurrentCondition.setVisibility(View.VISIBLE);
            //Updating Forecast View
            forecastList.clear();
            forecastList.addAll(item.getForecast());
            Log.i("Size", forecastList.size() + "");
            adapter.notifyDataSetChanged();
            cvForecastView.setVisibility(View.VISIBLE);

            //Updating Wind And Pressure
            Double speed = Double.valueOf(weatherModel.getQuery().getResults().getChannel().getWind().getSpeed());
            tvWindSpeed.setText("" + Math.round(speed));
            Double pressure = Double.valueOf(weatherModel.getQuery().getResults().getChannel().getAtmosphere().getPressure());
            tvPressure.setText(""+Math.round(pressure));
            cvWindAndPressure.setVisibility(View.VISIBLE);
        } catch (Exception e){
            e.printStackTrace();
            rlCurrentCondition.setVisibility(View.GONE);
            cvForecastView.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), getString(R.string.something_wrong_message),Toast.LENGTH_LONG).show();
        }
    }

    void updateLocation(){
        if (Methods.isNetworkAvailable(getApplicationContext())){
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(this, locationResult);

            Log.i("Update Location called", myLocation.isGps_enabled() + "");
            if (!myLocation.isGps_enabled()) {
                swipeRefreshLayout.setRefreshing(false);
                Methods.buildAlertMessageNoGps(this);
            }
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(getApplicationContext(), getString(R.string.no_network_message),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
    }

    public void fetchWeatherData(Location location) {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        //fetching Weather data based on fetched location
        Log.i("Location:", location.getLatitude() + "," + location.getLongitude());
        if (retrofitCall != null) {
            retrofitCall.cancel();
        }
        retrofitCall = service.getWeather(Methods.PrepareQueryFromLocation(location));
        retrofitCall.enqueue(new Callback<WeatherModel>() {

            @Override
            public void onResponse(Response<WeatherModel> response, Retrofit retrofit) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    if (response.isSuccess()) {
                        WeatherModel weatherModel = response.body();
                        //Updating the UI with latest weather data
                        updateUI(weatherModel);
                        PreferenceUtils.setWeatherModel(weatherModel);
                        PreferenceUtils.setLastUpdatedTime(System.currentTimeMillis());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.something_wrong_message), Toast.LENGTH_LONG).show();
                Log.i("Failed:", t.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
