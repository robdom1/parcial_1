package com.example.parcial_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parcial_1.entities.Producto;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item , parent , false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Producto productoActual = FirstFragment.productos.get(position);

        holder.nombre.setText(productoActual.getNombre());
        holder.descripcion.setText(productoActual.getDescripcion());
        holder.precio.setText(productoActual.getPrecio().toString());

        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirstFragment.productos.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), FirstFragment.productos.size());
                Toast.makeText(view.getContext(), "Producto eliminado exitosamente!!", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return FirstFragment.productos.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView nombre;
        TextView descripcion;
        TextView precio;
        ImageButton btnEliminar;
        ImageButton btnCompartir;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreArticulo);
            descripcion = itemView.findViewById(R.id.descripcionArticulo);
            precio = itemView.findViewById(R.id.precioArticulo);
            btnEliminar = itemView.findViewById(R.id.btnBorrar);
            btnCompartir = itemView.findViewById(R.id.btnCompartir);
        }

    }
}
