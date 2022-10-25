package com.example.parcial_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.parcial_1.databinding.FragmentSecondBinding;
import com.example.parcial_1.entities.Producto;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        binding.btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInputs();
            }
        });

        binding.btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Producto nuevoProducto = new Producto();

                String nombreArticulo = binding.inputNombre.getText().toString();
                if(nombreArticulo.equals("")){
                    binding.inputNombre.setError("Este campo no puede estar vacío");
                    return;
                }
                nuevoProducto.setNombre(nombreArticulo);


                String descricionArticulo = binding.inputDescripcion.getText().toString();
                if(descricionArticulo.equals("")){
                    binding.inputDescripcion.setError("Este campo no puede estar vacío");
                    return;
                }
                nuevoProducto.setDescripcion(descricionArticulo);


                String precioArticulo = binding.inputPrecio.getText().toString();
                if(precioArticulo.equals("")){
                    binding.inputPrecio.setError("Este campo no puede estar vacío");
                    return;
                }
                Double precio = Double.parseDouble(precioArticulo);
                nuevoProducto.setPrecio(precio);

                MainActivity.productos.add(nuevoProducto);
                clearInputs();
                Toast.makeText(view.getContext(), "Producto Agregado!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void clearInputs(){
        binding.inputNombre.setText("");
        binding.inputDescripcion.setText("");
        binding.inputPrecio.setText("");

        binding.inputNombre.requestFocus();

    }

}