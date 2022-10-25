package com.example.parcial_1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parcial_1.entities.Producto;

import java.text.DecimalFormat;
import java.util.List;

import javax.sql.StatementEvent;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private Context mContext;

    public ItemAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item , parent , false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Producto productoActual = MainActivity.productos.get(position);

        holder.nombre.setText(productoActual.getNombre());
        holder.descripcion.setText(productoActual.getDescripcion());

        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);

        String precioStr = df.format(productoActual.getPrecio());
        holder.precio.setText(precioStr);

        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.productos.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), MainActivity.productos.size());
                Toast.makeText(view.getContext(), "Producto eliminado exitosamente!!", Toast.LENGTH_LONG).show();
            }
        });

        holder.btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                String productoStr = productoActual.toString();
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, productoStr);
                mContext.startActivity(Intent.createChooser(intent, "Compartir"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return MainActivity.productos.size();
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
