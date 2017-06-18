package com.example.me495.lbstest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.LocaleDisplayNames;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Circle;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import overlayutil.PoiOverlay;

public class MainActivity extends AppCompatActivity {
    public LocationClient mLocationClient;
    private TextureMapView mapView;
    private BaiduMap baiduMap;
    private LatLng ll;

    /*
    初始化定位参数，并申请权限
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09LL");
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            mLocationClient.start();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        //Log.d("coord_type", String.valueOf(SDKInitializer.getCoordType()));
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_main);
        mapView = (TextureMapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        initLocation();
        Button button1 = (Button) findViewById(R.id.my_location);
        Button button2 = (Button) findViewById(R.id.change);
        Button button3 = (Button) findViewById(R.id.search);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocation();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChangeActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent, 2);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    if(data.getStringExtra("map_type").equals("normal_map")) {
                        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    } else {
                        baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    }
                    if(data.getStringExtra("heat_map").equals("true")) {
                        baiduMap.setBaiduHeatMapEnabled(true);
                    } else {
                        baiduMap.setBaiduHeatMapEnabled(false);
                    }
                    if(data.getStringExtra("traffic_map").equals("true")) {
                        baiduMap.setTrafficEnabled(true);
                    } else {
                        baiduMap.setTrafficEnabled(false);
                    }
                }
                break;
            case 2:
                if(resultCode == RESULT_OK) {
                    final GeoCoder geoCoder = GeoCoder.newInstance();
                    final GeoCodeOption geoCodeOption = new GeoCodeOption();
                    geoCodeOption.address(data.getStringExtra("address")).city(data.getStringExtra("city"));
                    OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {
                        @Override
                        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                            if(geoCodeResult == null || geoCodeResult.error != GeoCodeResult.ERRORNO.NO_ERROR) {
                                Toast.makeText(MainActivity.this, "未找到您输入的地址！",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            baiduMap.clear();
                            LatLng latLng = geoCodeResult.getLocation();
                            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(latLng,15);
                            baiduMap.animateMapStatus(update);
                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.red_mark);
                            OverlayOptions options = new MarkerOptions().position(latLng).icon(bitmap);
                            baiduMap.addOverlay(options);
                        }

                        @Override
                        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

                        }
                    };
                    geoCoder.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
                    Log.d("location_code",geoCodeOption.toString());
                    geoCoder.geocode(geoCodeOption);
                }
                break;
            default:
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLocationClient != null) mLocationClient.stop();
        mapView.onDestroy();
    }

    private void requestLocation() {

        baiduMap.clear();
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll,18);
        baiduMap.animateMapStatus(update);
        DotOptions option = new DotOptions().center(ll).color(Color.rgb(0x1e,0x90,0xff)).radius(15);
        option.center(ll).color(Color.BLUE);
        baiduMap.addOverlay(option);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for(int result : grantResults) {
                        if(result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用定位功能", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    //requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location.getLocType() == BDLocation.TypeGpsLocation) {
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                CoordinateConverter converter = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.GPS);
                converter.coord(latLng);
                ll = converter.convert();
                Log.d("loc_type","GPS");
            } else {
                ll = new LatLng(location.getLatitude(), location.getLongitude());
            }
//            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll,18);
//            baiduMap.animateMapStatus(update);
//            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.blue_mark);
//            DotOptions option = new DotOptions().center(ll).color(Color.rgb(0x1e,0x90,0xff)).radius(10);
//            option.center(ll).color(Color.BLUE);
//            baiduMap.addOverlay(option);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

}
