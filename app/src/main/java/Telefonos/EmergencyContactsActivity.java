package Telefonos;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.niko.pruebaurgencias3.R;
import java.util.ArrayList;
import java.util.List;
import SQL.DBHelper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmergencyContactsActivity extends AppCompatActivity implements EmergencyContactAdapter.OnContactActionListener {

    private RecyclerView recyclerView;
    private EmergencyContactAdapter adapter;
    private List<EmergencyContact> contactsList;
    private FloatingActionButton fabAddContact;
    private DBHelper dbHelper;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.llamadas_emergencia);
        FirebaseApp.initializeApp(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("contacts"); // Nodo raíz

        recyclerView = findViewById(R.id.recyclerViewEmergency);
        fabAddContact = findViewById(R.id.fabAddContact);
        contactsList = new ArrayList<>();
        dbHelper = new DBHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EmergencyContactAdapter(contactsList, this, this);
        recyclerView.setAdapter(adapter);

        loadDefaultContacts();
        loadContactsFromDB();

        fabAddContact.setOnClickListener(v -> showAddContactDialog(null));
    }

    private void loadDefaultContacts() {
        if (dbHelper.getAllContacts().getCount() == 0) {
            dbHelper.addContact("Carabineros", "133");
            dbHelper.addContact("Ambulancia", "131");
            dbHelper.addContact("Bomberos", "132");
            dbHelper.addContact("Policía de Investigaciones", "134");
            loadContactsFromDB();
        }
    }

    private void showAddContactDialog(EmergencyContact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(contact == null ? "Agregar Contacto de Emergencia" : "Editar Contacto de Emergencia");

        View dialogView = getLayoutInflater().inflate(R.layout.agregar_contacto, null);
        builder.setView(dialogView);

        EditText nameInput = dialogView.findViewById(R.id.editTextName);
        EditText phoneInput = dialogView.findViewById(R.id.editTextPhone);

        if (contact != null) {
            nameInput.setText(contact.getName());
            phoneInput.setText(contact.getPhoneNumber());
        }

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty()) {
                if (contact == null) {
                    // Crear un nuevo contacto y guardar en Firebase
                    String id = databaseReference.push().getKey();  // Generar ID único
                    if (id != null) { // Asegurarse de que el ID no sea nulo
                        EmergencyContact newContact = new EmergencyContact(id, name, phone);
                        // Guardar en Firebase
                        databaseReference.child(id).setValue(newContact)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(this, "Contacto guardado en Firebase", Toast.LENGTH_SHORT).show();
                                        // Agregar a SQLite
                                        dbHelper.addContact(name, phone);
                                        loadContactsFromDB(); // Recargar la lista
                                    } else {
                                        Toast.makeText(this, "Error al guardar en Firebase", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Manejo de errores específicos
                                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    }
                } else {
                    // Actualizar contacto existente en Firebase
                    databaseReference.child(contact.getId()).child("name").setValue(name)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Nombre actualizado correctamente en Firebase", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Error al actualizar el nombre en Firebase", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });

                    databaseReference.child(contact.getId()).child("phoneNumber").setValue(phone)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Teléfono actualizado correctamente en Firebase", Toast.LENGTH_SHORT).show();
                                    // Actualizar en SQLite
                                    dbHelper.updateContact(contact.getId(), name, phone);
                                    loadContactsFromDB(); // Recargar la lista de contactos
                                } else {
                                    Toast.makeText(this, "Error al actualizar el teléfono en Firebase", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }
            } else {
                Toast.makeText(this, "Por favor ingresa nombre y teléfono", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }


    @Override
    public void onEditContact(EmergencyContact contact) {
        showAddContactDialog(contact);
    }

    @Override
    public void onDeleteContact(EmergencyContact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar contacto")
                .setMessage("¿Estás seguro de que deseas eliminar este contacto?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dbHelper.deleteContact(contact.getId());
                    loadContactsFromDB();
                    Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void loadContactsFromDB() {
        contactsList.clear();
        Cursor cursor = dbHelper.getAllContacts();
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex(DBHelper.COLUMN_NAME);
            int phoneIndex = cursor.getColumnIndex(DBHelper.COLUMN_PHONE);

            if (idIndex != -1 && nameIndex != -1 && phoneIndex != -1) {
                do {
                    String id = cursor.getString(idIndex);
                    String name = cursor.getString(nameIndex);
                    String phone = cursor.getString(phoneIndex);
                    contactsList.add(new EmergencyContact(id, name, phone));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            Toast.makeText(this, "No se encontraron contactos", Toast.LENGTH_SHORT).show();
        }

        // Notificar cambios al adaptador
        adapter.updateContactsList(contactsList);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void syncContactsWithFirebase() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Sin conexión a Internet. Sincronización pendiente.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.getAllContacts();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PHONE));

                // Crear un objeto EmergencyContact
                EmergencyContact contact = new EmergencyContact(String.valueOf(id), name, phone);

                // Subir a Firebase
                databaseReference.child(String.valueOf(id)).setValue(contact)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Contacto sincronizado: " + name, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error al sincronizar " + name, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error al sincronizar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncContactsWithFirebase(); // Intento de sincronización al abrir la actividad
    }

}
