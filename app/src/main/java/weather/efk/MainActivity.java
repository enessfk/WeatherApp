package weather.efk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    TextView city,temp,main,humidity,wind,realFeel,time;
    ImageView weatherImage;
    private FusedLocationProviderClient client;
    static int indexfor=5;
    static String lat;
    static String lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = findViewById(R.id.id_city);
        temp = findViewById(R.id.id_degree);
        main = findViewById(R.id.id_main);
        humidity = findViewById(R.id.id_humidity);
        wind = findViewById(R.id.id_wind);
        realFeel = findViewById(R.id.id_realfeel);
        weatherImage = findViewById(R.id.id_weatherImage);
        client = LocationServices.getFusedLocationProviderClient(this);
        time=findViewById(R.id.id_time);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, ACCESS_FINE_LOCATION)){

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{ACCESS_FINE_LOCATION}, 1);
            }
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    double latitude=Math.round(location.getLatitude() * 100.0)/100.0;
                    lat= String.valueOf(latitude);

                    double longitude=Math.round(location.getLongitude() * 100.0)/100.0;
                    lon= String.valueOf(longitude);

                    WeatherByLatLon(lat,lon);
                }else{
                    WeatherByCityName("London");
                }

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location!=null){

                                    double latitude=Math.round(location.getLatitude() * 100.0)/100.0;
                                    lat= String.valueOf(latitude);

                                    double longitude=Math.round(location.getLongitude() * 100.0)/100.0;
                                    lon= String.valueOf(longitude);

                                    WeatherByLatLon(lat,lon);
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void WeatherByCityName(String city){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/forecast?q="+city+"&appid="+BuildConfig.ApiKey+"&units=metric")
                .get().build();
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Response response=client.newCall(request).execute();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String data=response.body().string();
                    try {

                        JSONObject json=new JSONObject(data);
                        JSONObject city=json.getJSONObject("city");
                        JSONObject coord=city.getJSONObject("coord");
                        String lat =coord.getString("lat");
                        String lon=coord.getString("lon");

                        WeatherByLatLon(lat,lon);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    private void WeatherByLatLon(String lat,String lon){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+lon+"&appid="+BuildConfig.ApiKey+"&units=metric")
                .get().build();
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Response response=client.newCall(request).execute();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String data=response.body().string();
                    try {

                        JSONObject json=new JSONObject(data);
                        TextView[] forecast = new TextView[5];
                        TextView[] forecastTemp=new TextView[5];
                        ImageView[] forecastIcons=new ImageView[5];
                        IdAssign(forecast,forecastTemp,forecastIcons);

                        indexfor=5;
                        for (int i=0;i<forecast.length;i++){
                            forecastCal(forecast[i],forecastTemp[i],forecastIcons[i],indexfor,json);
                        }

                        JSONArray list=json.getJSONArray("list");
                        JSONObject objects = list.getJSONObject(0);
                        JSONArray array=objects.getJSONArray("weather");
                        JSONObject object=array.getJSONObject(0);

                        String description=object.getString("description");
                        String icons=object.getString("icon");

                        Date currentDate=new Date();
                        String dateString=currentDate.toString();
                        String[] dateSplit=dateString.split(" ");
                        String date=dateSplit[0]+", "+dateSplit[1] +" "+dateSplit[2];

                        JSONObject Main=objects.getJSONObject("main");
                        double temparature=Main.getDouble("temp");
                        String Temp=Math.round(temparature)+"°C";
                        double Humidity=Main.getDouble("humidity");
                        String hum=Math.round(Humidity)+"%";
                        double FeelsLike=Main.getDouble("feels_like");
                        String feelsValue=Math.round(FeelsLike)+"°";

                        JSONObject Wind=objects.getJSONObject("wind");
                        String windValue=Wind.getString("speed")+" "+"km/h";

                        JSONObject CityObject=json.getJSONObject("city");
                        String City=CityObject.getString("name");

                        setDataText(city,City);
                        setDataText(temp,Temp);
                        setDataText(main,description);
                        setDataImage(weatherImage,icons);
                        setDataText(time,date);
                        setDataText(humidity,hum);
                        setDataText(realFeel,feelsValue);
                        setDataText(wind,windValue);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setDataText(final TextView text, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }
    private void setDataImage(final ImageView ImageView, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (value){
                    case "01d": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w01d)); break;
                    case "01n": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w01d)); break;
                    case "02d": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w02d)); break;
                    case "02n": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w02d)); break;
                    case "03d": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w03d)); break;
                    case "03n": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w03d)); break;
                    case "04d": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w04d)); break;
                    case "04n": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w04d)); break;
                    case "09d": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w09d)); break;
                    case "09n": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w09d)); break;
                    case "10d": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w10d)); break;
                    case "10n": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w10d)); break;
                    case "11d": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w11d)); break;
                    case "11n": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w11d)); break;
                    case "13d": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w13d)); break;
                    case "13n": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w13d)); break;
                    default:ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w03d));

                }
            }
        });
    }


    private void forecastCal(TextView forecast,TextView forecastTemp,ImageView forecastIcons,int index,JSONObject json) throws JSONException {
        JSONArray list=json.getJSONArray("list");
        for (int i=index; i<list.length(); i++) {
            JSONObject object = list.getJSONObject(i);

            String dt=object.getString("dt_txt"); // dt_text.format=2020-06-26 12:00:00
            String[] a=dt.split(" ");
            if ((i==list.length()-1) && !a[1].equals("12:00:00")){
                String[] dateSplit=a[0].split("-");
                Calendar calendar=new GregorianCalendar(Integer.parseInt(dateSplit[0]),Integer.parseInt(dateSplit[1])-1,Integer.parseInt(dateSplit[2]));
                Date forecastDate=calendar.getTime();
                String dateString=forecastDate.toString();
                String[] forecastDateSplit=dateString.split(" ");
                String date=forecastDateSplit[0]+", "+forecastDateSplit[1] +" "+forecastDateSplit[2];
                setDataText(forecast, date);

                JSONObject Main=object.getJSONObject("main");
                double temparature=Main.getDouble("temp");
                String Temp=Math.round(temparature)+"°";
                setDataText(forecastTemp,Temp);

                JSONArray array=object.getJSONArray("weather");
                JSONObject object1=array.getJSONObject(0);
                String icons=object1.getString("icon");
                setDataImage(forecastIcons,icons);

                return;
            }
            else if (a[1].equals("12:00:00")){

                String[] dateSplit=a[0].split("-");
                Calendar calendar=new GregorianCalendar(Integer.parseInt(dateSplit[0]),Integer.parseInt(dateSplit[1])-1,Integer.parseInt(dateSplit[2]));
                Date forecastDate=calendar.getTime();
                String dateString=forecastDate.toString();
                String[] forecastDateSplit=dateString.split(" ");
                String date=forecastDateSplit[0]+", "+forecastDateSplit[1] +" "+forecastDateSplit[2];
                setDataText(forecast, date);


                JSONObject Main=object.getJSONObject("main");
                double temparature=Main.getDouble("temp");
                String Temp=Math.round(temparature)+"°";
                setDataText(forecastTemp,Temp);

                JSONArray array=object.getJSONArray("weather");
                JSONObject object1=array.getJSONObject(0);
                String icons=object1.getString("icon");
                setDataImage(forecastIcons,icons);


                indexfor=i+1;
                return;
            }
        }
    }

    private void IdAssign(TextView[] forecast,TextView[] forecastTemp,ImageView[] forecastIcons){
        forecast[0]=findViewById(R.id.id_forecastDay1);
        forecast[1]=findViewById(R.id.id_forecastDay2);
        forecast[2]=findViewById(R.id.id_forecastDay3);
        forecast[3]=findViewById(R.id.id_forecastDay4);
        forecast[4]=findViewById(R.id.id_forecastDay5);
        forecastTemp[0]=findViewById(R.id.id_forecastTemp1);
        forecastTemp[1]=findViewById(R.id.id_forecastTemp2);
        forecastTemp[2]=findViewById(R.id.id_forecastTemp3);
        forecastTemp[3]=findViewById(R.id.id_forecastTemp4);
        forecastTemp[4]=findViewById(R.id.id_forecastTemp5);
        forecastIcons[0]=findViewById(R.id.id_forecastIcon1);
        forecastIcons[1]=findViewById(R.id.id_forecastIcon2);
        forecastIcons[2]=findViewById(R.id.id_forecastIcon3);
        forecastIcons[3]=findViewById(R.id.id_forecastIcon4);
        forecastIcons[4]=findViewById(R.id.id_forecastIcon5);

    }

    public void showPopup(View v){
        PopupMenu popup=new PopupMenu(this,v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.id_currentLocation:
                WeatherByLatLon(lat,lon);
                return true;
            case R.id.id_otherCity:
                Intent intent=new Intent(MainActivity.this,CitySearch.class);
                startActivityForResult(intent,1);
            default:
                return false;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String citySearched=data.getStringExtra("result");
                WeatherByCityName(citySearched);
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }
}