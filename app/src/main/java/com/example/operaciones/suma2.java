package com.example.operaciones;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class suma2 extends AppCompatActivity {
    private TextView tvNombre, tvScore;
    private ImageView ivUno, ivDos, vidaImagen, operacion;
    private EditText etRespuesta;
    private MediaPlayer sound, soundBien, soundMal;

    int score, aleatorio_uno, aleatorio_dos, resultado, vidaInt = 3 ;
    String jugadorString, scoreString, vidasString;
    String numero[] = {"cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve"};
    String opera[] = {"suma", "resta", "multi", "divi"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suma2);
        Toast.makeText(this, "Nivel-2 Sumas Moderada", Toast.LENGTH_SHORT).show();

        this.tvNombre = (TextView)findViewById(R.id.t_nombre);
        this.tvScore = (TextView)findViewById(R.id.t_score);
        this.vidaImagen = (ImageView)findViewById(R.id.img_estrellas);
        this.ivUno = (ImageView)findViewById(R.id.img_num_uno);
        this.ivDos = (ImageView)findViewById(R.id.img_num_dos);
        this.etRespuesta = (EditText)findViewById(R.id.i_text_verificar);

        //obtener nombre
        jugadorString = getIntent().getStringExtra("jugador");//recuperar nombre
        tvNombre.setText("Jugador:  "+jugadorString);
        scoreString = getIntent().getStringExtra("score");//recuperar score
        tvScore.setText("Score: "+scoreString);
        score = Integer.parseInt(scoreString);
        vidasString = getIntent().getStringExtra("vida");//recuperar score
        vidaInt = Integer.parseInt(vidasString);
        switch (vidaInt){
            case 3:
                vidaImagen.setImageResource(R.drawable.vida3);
                break;
            case 2:
                vidaImagen.setImageResource(R.drawable.vida2);
                break;
            case 1:
                vidaImagen.setImageResource(R.drawable.vida1);
                break;
        }

        this.sound = MediaPlayer.create(this, R.raw.musicafondo);
        this.sound.start();
        this.sound.setLooping(true);

        this.soundBien = MediaPlayer.create(this, R.raw.great);
        this.soundMal = MediaPlayer.create(this, R.raw.none);

        ImgAleatorio();
    }

    public void Comprobar(View view){
        String res = String.valueOf(etRespuesta.getText().toString());

        if(!res.equals("")){
            int resEntero = Integer.parseInt(res);
            System.out.println("ResEntero"+resEntero);
            System.out.println("Resultado"+resultado);
            if(resultado == resEntero ){
                soundBien.start();
                score++;
                tvScore.setText("Score:  "+score);
                etRespuesta.setText("");
                BDD();
            }else{
                soundMal.start();
                vidaInt--;

                switch (vidaInt){
                    case 3:
                        vidaImagen.setImageResource(R.drawable.vida3);
                        break;
                    case 2:
                        Toast.makeText(this, "Te quedan dos estrellas", Toast.LENGTH_SHORT).show();
                        vidaImagen.setImageResource(R.drawable.vida2);
                        break;
                    case 1:
                        Toast.makeText(this, "Te queda una estrella", Toast.LENGTH_SHORT).show();
                        vidaImagen.setImageResource(R.drawable.vida1);
                        break;
                    case 0:
                        Toast.makeText(this, "Has perdido todas tus estrellas", Toast.LENGTH_SHORT).show();
                        Intent regresar = new Intent(this, login.class);
                        startActivity(regresar);
                        finish();
                        soundMal.stop();
                        soundMal.release();
                        break;
                }
            }
            ImgAleatorio();
        }else{
            Toast.makeText(this, "Escribe tu respuesta", Toast.LENGTH_SHORT).show();
        }
    }

    public void ImgAleatorio(){
        if(score <= 19){
            aleatorio_uno = (int) (Math.random() * 10);
            aleatorio_dos = (int) (Math.random() * 10);
            System.out.println("\naleatorio1: "+aleatorio_uno);
            System.out.println("aleatorio2: "+aleatorio_dos);
            resultado = aleatorio_uno + aleatorio_dos;
            System.out.println("Suma: "+resultado);

                for(int i =  0 ; i < numero.length; i++){
                    int id = getResources().getIdentifier(numero[i], "drawable", getPackageName());
                    if(aleatorio_uno == i){
                        ivUno.setImageResource(id);
                    }
                    if(aleatorio_dos == i) {
                        ivDos.setImageResource(id);
                    }
                }
        }else{
            Intent seguir = new Intent(this, resta1.class);

            scoreString = String.valueOf(score);
            vidasString = String.valueOf(vidaInt);
            seguir.putExtra("jugador", jugadorString);
            seguir.putExtra("score", scoreString);
            seguir.putExtra("vida", vidasString);

            startActivity(seguir);
            finish();
            sound.stop();
            sound.release();
        }
    }

    @Override
    public void onBackPressed(){
        Intent seguir = new Intent(this, MainActivity.class);
        startActivity(seguir);
    }

    public void BDD(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "BD", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();

        Cursor consulta = BD.rawQuery("SELECT * FROM puntaje WHERE score = (SELECT MAX(score) FROM puntaje)", null);
        if(consulta.moveToFirst()){
            String temp_nombre = consulta.getString(0);
            String temp_score = consulta.getString(1);
            int bestScore = Integer.parseInt(temp_score);

            if(score > bestScore){
                ContentValues modificar = new ContentValues();
                modificar.put("nombre", jugadorString);
                modificar.put("score", scoreString);

                BD.update("puntaje", modificar, "score="+bestScore, null);
            }
            BD.close();
        }else{
            ContentValues insertar = new ContentValues();
            insertar.put("nombre", jugadorString);
            insertar.put("score", score);
            BD.insert("puntaje", null, insertar);
            BD.close();
        }
    }
}