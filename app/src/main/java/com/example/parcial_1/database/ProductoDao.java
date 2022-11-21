package com.example.parcial_1.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.parcial_1.entities.Producto;

import java.util.List;

@Dao
public interface ProductoDao {

    @Insert
    void insert(Producto producto);

    @Update
    void update(Producto producto);

    @Delete
    void delete(Producto producto);

    @Query("DELETE FROM PRODUCTS")
    void deleteAll();

    @Query("SELECT * FROM PRODUCTS ORDER BY PRODUCT_ID ASC")
    LiveData<List<Producto>> getAllProducts();
}
