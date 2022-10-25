package com.example.parcial_1;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parcial_1.databinding.FragmentFirstBinding;
import com.example.parcial_1.entities.Producto;

import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    public static List<Producto> productos;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.recyclerView;

        int spanCount = 1;

        if (container.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 2;
        }

        recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), spanCount));

        productos.add(new Producto("test", "test", 100.00));
        productos.add(new Producto("test2", "test2", 200.00));
        productos.add(new Producto("test3", "test3", 300.00));
        productos.add(new Producto("test4", "test4", 400.00));
        productos.add(new Producto("test5", "test5", 500.00));

        recyclerView.setAdapter(new ItemAdapter());

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}