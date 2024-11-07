package SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.exoplayer2.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_data.db";
    private static final int DATABASE_VERSION = 1;
    public static final String COLUMN_NAME = "nombre";
    public static final String COLUMN_PHONE = "telefono";
    private static final String TABLE_CONTACTS = "contactos";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_CONTACTS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    "isSynced INTEGER DEFAULT 0);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejo de errores para crear la tabla
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_CONTACTS + " ADD COLUMN isSynced INTEGER DEFAULT 0");
        }
    }

    public void addContact(String name, String phone) {
        if (name == null || phone == null || name.isEmpty() || phone.isEmpty()) {
            Log.e("DBHelper", "Datos inválidos para agregar contacto: " + name + ", " + phone);
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        try {
            db.insertOrThrow(TABLE_CONTACTS, null, values);
        } catch (SQLException e) {
            Log.e("DBHelper", "Error al agregar contacto", e); // Registra el error
        } finally {
            db.close(); // Cerrar base de datos
        }
    }


    public Cursor getAllContacts() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_CONTACTS, new String[]{"id", COLUMN_NAME, COLUMN_PHONE}, null, null, null, null, null);
        } catch (SQLException e) {
            Log.e("DBHelper", "Error al obtener contactos", e); // Aquí registramos la excepción
        }
        // No cerramos el cursor aquí, sino donde se utiliza
        return cursor;
    }


    public boolean updateContact(String id, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);

        int rowsAffected = 0;
        try {
            rowsAffected = db.update(TABLE_CONTACTS, values, "id = ?", new String[]{id});
        } catch (SQLException e) {
            Log.e("DBHelper", "Error al actualizar el contacto con id: " + id, e); // Registro detallado
        } finally {
            if (db != null && db.isOpen()) {
                db.close(); // Cerrar base de datos
            }
        }
        return rowsAffected > 0;
    }


    public void deleteContact(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_CONTACTS, "id = ?", new String[]{id});
        } catch (SQLException e) {
            Log.e("DBHelper", "Error al eliminar el contacto", e); // Manejo de excepciones
        } finally {
            if (db != null && db.isOpen()) {
                db.close(); // Cerrar la base de datos correctamente
            }
        }
    }

    public void markAsSynced(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isSynced", 1);
        try {
            db.update(TABLE_CONTACTS, values, "id = ?", new String[]{id});
        } catch (SQLException e) {
            Log.e("DBHelper", "Error al marcar como sincronizado", e); // Registrar el error
        } finally {
            if (db != null && db.isOpen()) {
                db.close(); // Asegurarse de cerrar la base de datos
            }
        }
    }

    public Cursor getUnsyncedContacts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CONTACTS, new String[]{"id", COLUMN_NAME, COLUMN_PHONE}, "isSynced = ?", new String[]{"0"}, null, null, null);
    }

}
