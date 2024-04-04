package com.mware.polyshoprestapi.ui.bill;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.mware.polyshoprestapi.R;
import com.mware.polyshoprestapi.adapters.ADBills;
import com.mware.polyshoprestapi.api.APIServer;
import com.mware.polyshoprestapi.api.BillServices;
import com.mware.polyshoprestapi.databinding.FragmentHomeBinding;
import com.mware.polyshoprestapi.models.Bill;
import com.mware.polyshoprestapi.models.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillFragment extends Fragment {

    RecyclerView recyclerView;
    ADBills adBills;
    BillServices services = new APIServer().RETROFIT.create(BillServices.class);
    SharedPreferences preferences;
    ArrayList<Bill> listBills = new ArrayList<>();

    public static BillFragment newInstance() {
        BillFragment fragment = new BillFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill, container, false);
        recyclerView = view.findViewById(R.id.rvBills);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = getView().findViewById(R.id.rvBills);

        preferences = getActivity().getSharedPreferences("cookie", Context.MODE_PRIVATE);

        String cookie = preferences.getString("cookie", "");
        User user = new Gson().fromJson(cookie, User.class);

        Call<ArrayList<Bill>> call = services.getBillsByUserEmail(user.getEmail());
        call.enqueue(new Callback<ArrayList<Bill>>() {
            @Override
            public void onResponse(Call<ArrayList<Bill>> call, Response<ArrayList<Bill>> response) {
                if (response.body() != null) {
                    listBills.addAll(response.body());

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

                    recyclerView.setLayoutManager(layoutManager);

                    adBills = new ADBills(listBills, getContext());

                    recyclerView.setAdapter(adBills);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Bill>> call, Throwable t) {

            }
        });

    }
}