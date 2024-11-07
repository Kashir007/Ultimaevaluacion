package com.niko.pruebaurgencias3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class NotesActivity extends AppCompatActivity {

    private EditText editTextNote;
    private Button buttonSaveNote;
    private LinearLayout notesContainer;
    private SharedPreferences sharedPreferences;

    private MqttClient mqttClient;
    private static final String MQTT_BROKER = "tcp://broker.emqx.io:1883";
    private static final String MQTT_TOPIC = "test/lab";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        // Vincular los elementos del layout
        editTextNote = findViewById(R.id.editTextNote);
        buttonSaveNote = findViewById(R.id.buttonSaveNote);
        notesContainer = findViewById(R.id.notesContainer);

        // Configurar almacenamiento local con SharedPreferences
        sharedPreferences = getSharedPreferences("NotesApp", MODE_PRIVATE);

        // Configurar MQTT
        setupMQTT();

        // Guardar nota al hacer clic en el botón
        buttonSaveNote.setOnClickListener(v -> {
            String note = editTextNote.getText().toString();

            if (!note.isEmpty()) {
                // Guardar en SharedPreferences
                sharedPreferences.edit().putString("note", note).apply();

                // Agregar la nota a la interfaz
                addNoteToView(note);

                // Enviar la nota al broker MQTT
                publishToMQTT(note);

                // Limpiar el campo de texto
                editTextNote.setText("");

                Toast.makeText(NotesActivity.this, "Nota guardada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NotesActivity.this, "La nota está vacía", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupMQTT() {
        try {
            mqttClient = new MqttClient(MQTT_BROKER, MqttClient.generateClientId(), null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            mqttClient.connect(options);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Toast.makeText(NotesActivity.this, "Conexión perdida con el broker MQTT", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    // Este proyecto no requiere recibir mensajes
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Confirmación de entrega
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al conectar con el broker MQTT", Toast.LENGTH_SHORT).show();
        }
    }

    private void publishToMQTT(String note) {
        try {
            MqttMessage message = new MqttMessage(note.getBytes());
            message.setQos(2);
            mqttClient.publish(MQTT_TOPIC, message);
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al enviar la nota al broker MQTT", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNoteToView(String note) {
        TextView textView = new TextView(this);
        textView.setText(note);
        textView.setPadding(16, 16, 16, 16);
        textView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        notesContainer.addView(textView);
    }
}
