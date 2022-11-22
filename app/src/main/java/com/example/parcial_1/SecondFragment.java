package com.example.parcial_1;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
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

import com.bumptech.glide.Glide;
import com.example.parcial_1.databinding.FragmentSecondBinding;
import com.example.parcial_1.entities.Producto;
import com.example.parcial_1.viewmodels.ProductViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class SecondFragment extends Fragment {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_GALLERY = 2;
    public static final int REQUEST_FILE = 3;
    public static final int REQUEST_IMAGE_PERM = 101;
    private FragmentSecondBinding binding;
    private ProductViewModel productViewModel;
    private Boolean isEditing = false;
    private Producto item;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private Uri image;


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
        Log.d("fragLifecycle", "onViewCreated: Segundo fragment iniciado");
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        item = new Producto();

        if (getArguments() != null) {
            isEditing = true;
            item = (Producto) getArguments().getSerializable("selectedItem");
            Log.d("item", "onViewCreated: " + item.getImageUrl());
            binding.inputNombre.setText(item.getNombre());
            binding.inputDescripcion.setText(item.getDescripcion());

            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);
            String precioStr = df.format(item.getPrecio());
            binding.inputPrecio.setText(precioStr);

            if(!Objects.equals(item.getImageUrl(), "")){
                Glide.with(view.getContext()).load(item.getImageUrl()).into(binding.vistaImagen);
            }
        }

        if(isEditing){
            binding.btnDelete.setVisibility(View.VISIBLE);
        }

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Validación");
                builder.setMessage("Desea eliminar este artículo?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                delete();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                builder.create().show();


            }
        });

        binding.vistaImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(hasPermissions()){
                    startPictureDialog();
                }else{
                    askPermissions();
                }

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

                if(image != null){
                    uploadToFirebase(image);
                }else{
                    save(item);
                }

                if(!isEditing){
                    clearInputs();
                }



            }
        });
    }

    private void uploadToFirebase(Uri imageUri) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(imageUri);

        StorageReference fileRef = mStorageRef.child("Images/" + imageFileName);
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("item", "getDownloadUrl: " + uri.toString());
                        item.setImageUrl(uri.toString());
                        save(item);
                    }
                });
                Toast.makeText(getContext(), "Uploaded succesfully", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Uploading Failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            binding.vistaImagen.setImageURI(image);
        }

        if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null){
            image = data.getData();
            binding.vistaImagen.setImageURI(image);
        }

        if(requestCode == REQUEST_FILE && resultCode == RESULT_OK && data != null){
            image = data.getData();
            binding.vistaImagen.setImageURI(image);
        }
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    @Override
    public void onDestroyView() {
        Log.d("fragLifecycle", "onDestroyView: Segundo fragment destruido");
        Log.d("item", "onViewCreated: " + item.getImageUrl());
        super.onDestroyView();
        binding = null;
    }


    private void startPictureDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose your category picture");
        builder.setItems(new CharSequence[]
                        {"Take photo", "Choose from gallery", "Choose from file", "Cancel"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                dispatchTakePictureIntent();
                                break;
                            case 1:
                                dispatchTakeFromGalleryIntent();
                                break;
                            case 2:
                                dispatchTakeFromFilesIntent();
                                break;
                            case 3:
                                dialog.cancel();
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void save(Producto newProduct){

        Log.d("item", "save: " + newProduct.getImageUrl());
        if(isEditing){
            productViewModel.update(newProduct);
            Toast.makeText(getContext(), "Producto actualizado!!", Toast.LENGTH_LONG).show();
        }else {
            productViewModel.insert(newProduct);
            Toast.makeText(getContext(), "Producto Agregado!!", Toast.LENGTH_LONG).show();
        }

    }



    private void clearInputs(){
        binding.vistaImagen.setImageResource(R.drawable.ic_baseline_image_search_24);
        binding.inputNombre.setText("");
        binding.inputDescripcion.setText("");
        binding.inputPrecio.setText("");

        binding.btnDelete.setVisibility(View.GONE);

    }

    private void delete(){
        productViewModel.delete(item);
        item = new Producto();
        clearInputs();
        Toast.makeText(getContext(),"El artículo fue eliminado", Toast.LENGTH_SHORT).show();
        isEditing = false;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Save a file: path for use with ACTION_VIEW intents

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
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
                image = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, image);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void dispatchTakeFromGalleryIntent(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, REQUEST_GALLERY);

    }

    private void dispatchTakeFromFilesIntent(){
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("image/*");
        startActivityForResult(fileIntent, REQUEST_FILE);

    }

    private boolean hasPermissions(){
        return (
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void askPermissions(){
        Log.d("perm", "askPermissions: asking");
        requestPermissions(
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_IMAGE_PERM);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            return;
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    REQUEST_PERMISSIONS_CODE_WRITE_STORAGE
//            );
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("perm", permissions[0] + " => " + grantResults[0]);
        Log.d("perm", permissions[1] + " => " + grantResults[1]);
       if(requestCode == REQUEST_IMAGE_PERM){
           if (grantResults.length > 0 &&
               grantResults[0] == PackageManager.PERMISSION_GRANTED &&
               grantResults[1] == PackageManager.PERMISSION_GRANTED
           ){
               startPictureDialog();
           }
       }
    }

//    private boolean hasWriteStoragePermission(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            return true;
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//        }
//        return true;
//    }
}