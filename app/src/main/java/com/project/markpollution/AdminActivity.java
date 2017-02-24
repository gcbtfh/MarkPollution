package com.project.markpollution;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.project.markpollution.CustomAdapter.AdminRecyclerViewAdapter;
import com.project.markpollution.Interfaces.OnItemClickListener;
import com.project.markpollution.Objects.PollutionPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private RecyclerView recyclerViewSpam;
    private String url_RetrievePoByCountSpam = "http://2dev4u.com/dev/markpollution/RetrievePollutionByCountSpam" +
            ".php";
    private List<PollutionPoint> listPo;
    private List<Integer> listCountSpam;
    private AdminRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initView();
        loadAdminPortalData();
    }

    private void initView() {
        recyclerViewSpam = (RecyclerView) findViewById(R.id.recyclerViewSpam);
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSpam.setLayoutManager(layout);
    }

    private void loadAdminPortalData() {
        JsonObjectRequest objReq = new JsonObjectRequest(Request.Method.GET, url_RetrievePoByCountSpam, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("success")){
                        JSONArray arr = response.getJSONArray("response");
                        listPo = new ArrayList<>();
                        listCountSpam = new ArrayList<>();
                        for(int i=0; i<arr.length(); i++){
                            JSONObject po = arr.getJSONObject(i);
                            listPo.add(new PollutionPoint(po.getString("id_po"), po.getString("id_cate"), po
                                    .getString("id_user"), po.getDouble("lat"), po.getDouble("lng"), po.getString
                                    ("title"), po.getString("desc"), po.getString("image"), po.getString("time")));
                            listCountSpam.add(po.getInt("count_spam"));
                        }

                        // Pass data into Adapter & set adapter for RecyclerView
                        adapter = new AdminRecyclerViewAdapter(AdminActivity.this, listPo, listCountSpam);
                        recyclerViewSpam.setAdapter(adapter);

                        // Handle onClickItem
                        adapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(PollutionPoint po) {
                                // Start DetailReport
                                Intent intent = new Intent(AdminActivity.this, DetailReportActivity.class);
                                intent.putExtra("id_po", po.getId());
                                intent.putExtra("admin", true);
                                startActivity(intent);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(objReq);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAdminPortalData();
    }
}
