package com.example.parcial_1;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parcial_1.databinding.FragmentFirstBinding;
import com.example.parcial_1.entities.Producto;
import com.example.parcial_1.viewmodels.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private ProductViewModel productViewModel;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        recyclerView = binding.recyclerView;

        int spanCount = 1;

        if (container.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 2;
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), spanCount));



        recyclerView.setAdapter(new ItemAdapter(this));

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ItemAdapter adapter = (ItemAdapter) recyclerView.getAdapter();
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        productViewModel.getAllProducts().observe(getViewLifecycleOwner(), new Observer<List<Producto>>() {
            @Override
            public void onChanged(List<Producto> productos) {
                adapter.setList(productos);
            }
        });

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