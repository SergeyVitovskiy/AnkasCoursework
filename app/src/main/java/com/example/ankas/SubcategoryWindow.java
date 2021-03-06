package com.example.ankas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ankas.Adapter.SubcategoryAdapter;
import com.example.ankas.Class.Subcategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SubcategoryWindow extends AppCompatActivity {

    ExpandableHeightGridView gridSubcategory;

    static int idSelectCategory;
    static String titleSelectCategory;
    ArrayList<Subcategory> subcategoryArrayList;
    SubcategoryAdapter subcategoryAdapter;
    RequestQueue requestQueue;

    TextView textLoading;
    Timer timer;
    int tick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subcategory_window);

        subcategoryArrayList = new ArrayList<>(); // Создаем лист для подкатегорий
        subcategoryAdapter = new SubcategoryAdapter(this, R.layout.item_category, subcategoryArrayList); // Создаем адаптер
        requestQueue = Volley.newRequestQueue(this);
        //Список категорий
        jsonParseSubcategory(idSelectCategory); //
        gridSubcategory = (ExpandableHeightGridView) findViewById(R.id.gridSubcategory); // Обьявление GridView
        gridSubcategory.setExpanded(true); // Расширение GridView
        gridSubcategory.setAdapter(subcategoryAdapter); // Присваиваем адаптер

        TextView textNameWindow = (TextView) findViewById(R.id.textNameWindow);
        textNameWindow.setText(titleSelectCategory);
        menuNavigation();// Меню навигации
        gridOnClick(); // Обработка нажатий

        timerLoading(); // Таймер
    }

    // Меню навигации
    private void menuNavigation() {
        LinearLayout layoutShop, layoutMenuBrash, layoutBasket,layoutHuman;
        layoutShop = (LinearLayout) findViewById(R.id.layoutShop); // Категории
        layoutBasket = (LinearLayout) findViewById(R.id.layoutBasket); // Корзина
        layoutHuman = (LinearLayout) findViewById(R.id.layoutHuman); // Пользователь
        layoutMenuBrash = (LinearLayout) findViewById(R.id.layoutMenuBrash); // Меню

        layoutShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubcategoryWindow.this, MainActivity.class);
                startActivity(intent);
            }
        });
        layoutBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubcategoryWindow.this, BasketWindow.class);
                startActivity(intent);
            }
        });
        layoutHuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubcategoryWindow.this, UserAuthorizationWindow.class);
                startActivity(intent);
            }
        });
        layoutMenuBrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubcategoryWindow.this, MenuBrashWindow.class);
                startActivity(intent);
            }
        });
    }
    // Нажатие на подкатегорию товара
    private void gridOnClick() {
        gridSubcategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ProductWindow.idSelectSubcategory = SubcategoryAdapter.subcategoryArrayList.get(position).getId();
                ProductWindow.titleSelectSubcategory = SubcategoryAdapter.subcategoryArrayList.get(position).getTitle();
                Intent intent = new Intent(SubcategoryWindow.this, ProductWindow.class);
                startActivity(intent);
            }
        });
    }

    private void jsonParseSubcategory(int position) {
        String url = "http://anndroidankas.h1n.ru/php/subcategory.php?category="+position;

        subcategoryArrayList.clear(); // Отчищаем лист с категориями
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(getApplicationContext(), "" + response, Toast.LENGTH_SHORT).show();
                            JSONArray jsonArray = response.getJSONArray("SUBCATEGORY");
                            for (int i = 0; i < jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                int id = object.getInt("id");
                                int category_id = object.getInt("category_id");
                                String title = object.getString("title");
                                String image_url = object.getString("image_url"); // Получаем URL картики
                                subcategoryArrayList.add(new Subcategory(id, category_id, title, image_url)); // Добавляем категорию
                            }
                            subcategoryAdapter.notifyDataSetChanged(); // Отправка в адаптер для добавление категорий товара
                            timer.cancel();
                            textLoading.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                timer.cancel();
                textLoading.setVisibility(View.VISIBLE);
                textLoading.setText("Нет подключения к Интернету!");
            }
        });
        requestQueue.add(request);
    }

    private void timerLoading(){
        tick = 0;
        timer = new Timer();
        timer.schedule(new SubcategoryWindow.UpdateTimeTask(), 0, 300);
        textLoading = (TextView) findViewById(R.id.textLoading);
    }
    //Timer
    private class UpdateTimeTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (tick) {
                        case 0:
                            textLoading.setText("Загрузка");
                            break;
                        case 1:
                            textLoading.setText("Загрузка.");
                            break;
                        case 2:
                            textLoading.setText("Загрузка..");
                            break;
                        case 3:
                            textLoading.setText("Загрузка...");
                            tick = -1;
                            break;
                    }
                    tick++;
                }
            });
        }
    }
}