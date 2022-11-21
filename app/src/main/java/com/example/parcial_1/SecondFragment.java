package com.example.parcial_1;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.parcial_1.databinding.FragmentSecondBinding;
import com.example.parcial_1.entities.Producto;
import com.example.parcial_1.viewmodels.ProductViewModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SecondFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_PERM = 101;
    private FragmentSecondBinding binding;
    private ProductViewModel productViewModel;
    private Boolean isEditing = false;
    private Producto item = new Producto();;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private Bitmap image;
    String currentPhotoPath;

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

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        if (getArguments() != null) {
            isEditing = true;
            item = (Producto) getArguments().getSerializable("selectedItem");
            binding.inputNombre.setText(item.getNombre());
            binding.inputDescripcion.setText(item.getDescripcion());

            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);
            String precioStr = df.format(item.getPrecio());
            binding.inputPrecio.setText(precioStr);
        }

        if(isEditing){
            binding.btnDelete.setVisibility(View.VISIBLE);
        }


//        binding.btnRegresar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
//            }
//        });

        binding.vistaImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Choose your category picture");
                builder.setItems(new CharSequence[]
                                {"Take photo", "Choose from gallery", "Choose from file", "Cancel"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 0:
                                        askPermissions();
                                        break;
                                    case 1:
                                        Toast.makeText(v.getContext(), "clicked 2", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 2:
                                        Toast.makeText(v.getContext(), "clicked 3", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 3:
                                        Toast.makeText(v.getContext(), "clicked 4", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                builder.create().show();

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

                String nombreArticulo = binding.inputNombre.getText().toString();
                if(nombreArticulo.equals("")){
                    binding.inputNombre.setError("Este campo no puede estar vacío");
                    return;
                }
                item.setNombre(nombreArticulo);


                String descricionArticulo = binding.inputDescripcion.getText().toString();
                if(descricionArticulo.equals("")){
                    binding.inputDescripcion.setError("Este campo no puede estar vacío");
                    return;
                }
                item.setDescripcion(descricionArticulo);


                String precioArticulo = binding.inputPrecio.getText().toString();
                if(precioArticulo.equals("")){
                    binding.inputPrecio.setError("Este campo no puede estar vacío");
                    return;
                }
                Double precio = Double.parseDouble(precioArticulo);
                item.setPrecio(precio);

                save(view, item);

//                MainActivity.productos.add(nuevoProducto);


            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null){
            File f = new File(currentPhotoPath);
            binding.vistaImagen.setImageURI(Uri.fromFile(f));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void save(View view, Producto newProduct){

        if(isEditing){
            productViewModel.update(newProduct);
            Toast.makeText(view.getContext(), "Producto actualizado!!", Toast.LENGTH_LONG).show();
        }else {
            productViewModel.insert(newProduct);
            Toast.makeText(view.getContext(), "Producto Agregado!!", Toast.LENGTH_LONG).show();
        }

        isEditing = false;

    }

    public void clearInputs(){
        binding.inputNombre.setText("");
        binding.inputDescripcion.setText("");
        binding.inputPrecio.setText("");

        binding.inputNombre.requestFocus();

        binding.btnDelete.setVisibility(View.GONE);

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        System.out.println(getActivity().getPackageManager());
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void askPermissions(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_IMAGE_PERM);
        }else{
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       if(requestCode == REQUEST_IMAGE_PERM){
           if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               dispatchTakePictureIntent();
           }
       }
    }
}