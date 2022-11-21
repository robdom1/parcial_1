package com.example.parcial_1.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.parcial_1.entities.Producto;
import com.example.parcial_1.repositories.ProductRepo;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

    private ProductRepo productRepo;
    private LiveData<List<Producto>> allProducts;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        productRepo = new ProductRepo(application);
        allProducts = productRepo.getProductos();
    }

    public void insert(Producto producto){
        productRepo.insert(producto);
    }

    public void update(Producto producto){
        productRepo.update(producto);
    }

    public void delete(Producto producto){
        productRepo.delete(producto);
    }

    public void deleteAll(){
        productRepo.deleteAll();
    }

    public LiveData<List<Producto>> getAllProducts() {
        return allProducts;
    }
}
