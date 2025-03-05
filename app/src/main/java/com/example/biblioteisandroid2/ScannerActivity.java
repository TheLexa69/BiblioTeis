package com.example.biblioteisandroid2;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.biblioteisandroid2.API.models.BookLendingForm;
import com.example.biblioteisandroid2.API.repository.BookLendingRepository;
import com.example.biblioteisandroid2.API.repository.BookRepository;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScannerActivity extends AppCompatActivity {

    private BookLendingRepository bookLendingRepository;
    private int userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        bookLendingRepository = new BookLendingRepository();

        // Recoger el userId del Intent
        userId = getIntent().getIntExtra(InfoLibro.USER_ID_EXTRA, -1);
        if (userId == -1) {
            Toast.makeText(this, "No se ha proporcionado un ID de usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Escanea un código QR");
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                setResult(RESULT_CANCELED);
                Toast.makeText(this, "Se ha producido un error al escanear", Toast.LENGTH_SHORT).show();
            } else {
                String scannedData = result.getContents();
                Intent intent = new Intent();
                intent.putExtra("BOOK_ID", getIntent().getIntExtra("BOOK_ID", 0)); // Mantener el BOOK_ID original
                intent.putExtra("SCANNED_DATA", scannedData); // Agregar el dato escaneado si es necesario
                setResult(RESULT_OK, intent);

                int bookId = getIntent().getIntExtra("BOOK_ID", 0);

                BookLendingForm lendingForm = new BookLendingForm(bookId, userId);
                Log.d("ScannerActivity", "Lending book with ID: " + bookId + " for user ID: " + userId);
                Log.d("ScannerActivity", "lendingForm: " + lendingForm);
                bookLendingRepository.lendBook(userId, bookId, new BookRepository.ApiCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean success) {
                        if (success) {
                            Toast.makeText(ScannerActivity.this, "Libro prestado con éxito", Toast.LENGTH_SHORT).show();
                            Log.d("ScannerActivity", "Response success: " + success);
                            setResult(RESULT_OK);
                        } else {
                            Toast.makeText(ScannerActivity.this, "Error al prestar el libro", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_CANCELED);
                        }
                        Intent intent = new Intent(ScannerActivity.this, InfoLibro.class);
                        intent.putExtra(InfoLibro.BOOK_ID_EXTRA, bookId);
                        intent.putExtra(InfoLibro.USER_ID_EXTRA, userId);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("ScannerActivity", "Error al prestar libro", t);
                        Toast.makeText(ScannerActivity.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
                        t.printStackTrace(); // Esto imprimirá la traza completa del error
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
            }
            finish();
        }
    }

}
