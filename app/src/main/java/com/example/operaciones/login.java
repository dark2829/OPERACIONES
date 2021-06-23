package com.example.operaciones;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity {

    private EditText nombre;
    private ImageView personaje;
    private TextView score;
    private MediaPlayer sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        int num_aleatorio = (int) (Math.random() * 6);
        String imagen_text = "animal"+String.valueOf(num_aleatorio);

        nombre = (EditText)findViewById(R.id.e_texto_login);
        personaje = (ImageView)findViewById(R.id.imagen_login);
        score = (TextView)findViewById(R.id.s_text_record);


        int id;
        id = getResources().getIdentifier(imagen_text, "drawable", getPackageName());//imagen random (ruta)
        System.out.println("id"+id);
        personaje.setImageResource(id);

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "BD", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();//modo lectura de bdd

        //consulta a bdd
        Cursor consulta = BD.rawQuery(
                "SELECT * FROM puntaje WHERE score = (SELECT MAX(score) from puntaje )", null);

        if(consulta.moveToFirst()){
            String nombre_temp = consulta.getString(0);
            String score_temp = consulta.getString(1);
            this.score.setText(nombre_temp + " " + score_temp + " puntos" );
        }else{
            BD.close();
        }

        sound = MediaPlayer.create(this, R.raw.musicafondo);
        sound.start();
        sound.setLooping(true);
    }

    public void Comenzar(View view){
        String nombre = this.nombre.getText().toString();

        if(!nombre.equals("")) {
            sound.stop();
            sound.release();
            Intent seguir = new Intent(this, suma1.class);
            seguir.putExtra("jugador", nombre);
            startActivity(seguir);
            finish();
        }else{
            Toast.makeText(this, "Primero ingresa tu nombre", Toast.LENGTH_LONG).show();
            this.nombre.requestFocus();
            InputMethodManager metodo = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            metodo.showSoftInput(this.nombre, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onBackPressed(){
        Intent seguir = new Intent(this, MainActivity.class);
        startActivity(seguir);
    }

}