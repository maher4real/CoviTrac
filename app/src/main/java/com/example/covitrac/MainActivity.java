package com.example.covitrac;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covitrac.api.ApiUtilities;
import com.example.covitrac.api.CountryData;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView totalConfirm , totalActive , totalRecovered, totalDeath
            , totalTests, todayConfirm, todayRecovered, todayDeath,date;

    private PieChart pieChart;

    ShimmerFrameLayout shimmerConfirm,shimmerActive,shimmerRecovered,shimmerDeath,shimmerTest;

    private List<CountryData> list;

    String country = "India";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        list = new ArrayList<>();

        if(getIntent().getStringExtra("country") != null)
            country = getIntent().getStringExtra("country");


        init();
        TextView cname = findViewById(R.id.cName);
        cname.setText(country);

        cname.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CountryActivity.class)));
        ApiUtilities.getApiInterface().getCountryData()
                .enqueue(new Callback<List<CountryData>>() {
                    @Override
                    public void onResponse(Call<List<CountryData>> call, Response<List<CountryData>> response) {
                        list.addAll(response.body());

                        for(int i=0 ; i<list.size(); i++)
                            if(list.get(i).getCountry().equals(country)){
                                int confirm = Integer.parseInt(list.get(i).getCases());
                                int active = Integer.parseInt(list.get(i).getActive());
                                int recovered = Integer.parseInt(list.get(i).getRecovered());
                                int death = Integer.parseInt(list.get(i).getDeaths());

                                totalConfirm.setText(NumberFormat.getInstance().format(confirm));
                                totalActive.setText(NumberFormat.getInstance().format(active));
                                totalRecovered.setText(NumberFormat.getInstance().format(recovered));
                                totalDeath.setText(NumberFormat.getInstance().format(death));

                                todayDeath.setText(String.format("( +%s )", NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayDeaths()))));
                                todayConfirm.setText(String.format("( +%s )", NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayCases()))));
                                todayRecovered.setText(String.format("( +%s )", NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayRecovered()))));
                                totalTests.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTests())));

                                setText(list.get(i).getUpdated());

                                pieChart.addPieSlice(new PieModel("Confirm",confirm,getResources().getColor(R.color.yellow)));
                                pieChart.addPieSlice(new PieModel("Active",active,getResources().getColor(R.color.blue_pie)));
                                pieChart.addPieSlice(new PieModel("Recovered",recovered,getResources().getColor(R.color.green_pie)));
                                pieChart.addPieSlice(new PieModel("Death",death,getResources().getColor(R.color.red_pie)));

                                pieChart.startAnimation();

                                stopShimmerAnimation();

                            }
                    }

                    @Override
                    public void onFailure(Call<List<CountryData>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void stopShimmerAnimation() {
        shimmerConfirm.stopShimmer();
        shimmerActive.stopShimmer();
        shimmerRecovered.stopShimmer();
        shimmerDeath.stopShimmer();
        shimmerTest.stopShimmer();

        shimmerConfirm.hideShimmer();
        shimmerActive.hideShimmer();
        shimmerRecovered.hideShimmer();
        shimmerDeath.hideShimmer();
        shimmerTest.hideShimmer();


    }


    private void setText(String updated) {
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy");

        long milliSeconds = Long.parseLong(updated);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        date.setText("Updated at "+format.format(calendar.getTime()));

    }

    private void init() {
        totalConfirm = findViewById(R.id.totalConfirm);
        totalActive = findViewById(R.id.totalActive);
        totalRecovered = findViewById(R.id.totalRecovered);
        totalDeath = findViewById(R.id.totalDeath);
        todayConfirm = findViewById(R.id.todayConfirm);
        totalTests = findViewById(R.id.totalTest);
        todayRecovered = findViewById(R.id.todayRecovered);
        todayDeath = findViewById(R.id.todayDeath);
        pieChart = findViewById(R.id.pieChart);
        date = findViewById(R.id.date);

        shimmerConfirm = (ShimmerFrameLayout) findViewById(R.id.shimmerConfirm);
        shimmerActive = (ShimmerFrameLayout) findViewById(R.id.shimmerActive);
        shimmerRecovered = (ShimmerFrameLayout) findViewById(R.id.shimmerRecovered);
        shimmerDeath = (ShimmerFrameLayout) findViewById(R.id.shimmerDeath);
        shimmerTest = (ShimmerFrameLayout) findViewById(R.id.shimmerTest);

    }

}