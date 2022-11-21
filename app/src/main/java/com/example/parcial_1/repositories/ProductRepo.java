package com.example.parcial_1.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.parcial_1.database.AppDatabase;
import com.example.parcial_1.database.ProductoDao;
import com.example.parcial_1.entities.Producto;

import java.util.List;

public class ProductRepo {
    private ProductoDao productoDao;
    private LiveData<List<Producto>> productos;

    public ProductRepo(Application application){
        AppDatabase db = AppDatabase.getInstance(application);
        productoDao = db.productoDao();
        productos = productoDao.getAllProducts();
    }

    public void insert(Producto producto){
        new InsertProductAsyncTask(productoDao).execute(producto);
    }

    public void update(Producto producto){
        new UpdateProductAsyncTask(productoDao).execute(producto);
    }

    public void delete(Producto producto){
        new DeleteProductAsyncTask(productoDao).execute(producto);
    }

    public void deleteAll(){
        new DeleteAllProductAsyncTask(productoDao).execute();
    }

    public LiveData<List<Producto>> getProductos() {
        return productos;
    }

    private static class InsertProductAsyncTask extends AsyncTask<Producto, Void, Void>{

        private ProductoDao productoDao;

        private InsertProductAsyncTask(ProductoDao productoDao){
            this.productoDao = productoDao;
        }

        @Override
        protected Void doInBackground(Producto... productos) {
            productoDao.insert(productos[0]);
            return null;
        }
    }

    private static class UpdateProductAsyncTask extends AsyncTask<Producto, Void, Void>{

        private ProductoDao productoDao;

        private UpdateProductAsyncTask(ProductoDao productoDao){
            this.productoDao = productoDao;
        }

        @Override
        protected Void doInBackground(Producto... productos) {
            productoDao.update(productos[0]);
            return null;
        }
    }

    private static class DeleteProductAsyncTask extends AsyncTask<Producto, Void, Void>{

        private ProductoDao productoDao;

        private DeleteProductAsyncTask(ProductoDao productoDao){
            this.productoDao = productoDao;
        }

        @Override
        protected Void doInBackground(Producto... productos) {
            productoDao.delete(productos[0]);
            return null;
        }
    }

    private static class DeleteAllProductAsyncTask extends AsyncTask<Void, Void, Void>{

        private ProductoDao productoDao;

        private DeleteAllProductAsyncTask(ProductoDao productoDao){
            this.productoDao = productoDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            productoDao.deleteAll();
            return null;
        }
    }

}
