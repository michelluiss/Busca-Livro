package com.example.michel.buscalivro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class showBookData extends AppCompatActivity {

    Livro livro;
    Livro LivroAux = new Livro();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_book_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        // Recuperando o objeto
        livro = (Livro) getIntent().getSerializableExtra("livro");

        final TextView titulo = findViewById(R.id.nameBook);
        final TextView classificacao = findViewById(R.id.classificacao);
        final TextView numEstante = findViewById(R.id.numberBookcase);
        final TextView numPrateleira = findViewById(R.id.shelfNumber);
        final TextView autor = findViewById(R.id.autor);
        //final EditText posicaoX = findViewById(R.id.posicaoX);
        //final EditText posicaoY = findViewById(R.id.posicaoY);



        String aux = livro.getClassificacao();


        @SuppressLint("StaticFieldLeak") HttpAsyncTaskQuery assync = new HttpAsyncTaskQuery() {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {

                    String tituloL = this.livro.getString("titulo");
                    String autorL = this.livro.getString("autor");
                    String classificacaoL = this.livro.getString("classificacao");
                    String numEstanteL = this.livro.getString("numeroEst");
                    String numPrateleiraL = this.livro.getString("numeroPrat");
                    String posiXL = this.livro.getString("posiX");
                    String posiYL = this.livro.getString("posiY");

                    titulo.setText(this.livro.getString("titulo"));
                    classificacao.setText(this.livro.getString("classificacao"));
                    numEstante.setText(this.livro.getString("numeroEst"));
                    numPrateleira.setText(this.livro.getString("numeroPrat"));
                    autor.setText(this.livro.getString("autor"));
                    //posicaoX.setText(this.livro.getString("posiX"));
                    //posicaoY.setText(this.livro.getString("posiY"));

                    LivroAux.setNome(tituloL);
                    LivroAux.setAutor("x");
                    LivroAux.setClassificacao(classificacaoL);
                    LivroAux.setEstante(numEstanteL);
                    LivroAux.setPrateleira(numPrateleiraL);
                    LivroAux.setPosiX( posiXL );
                    LivroAux.setPosiY( posiYL );

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        assync.execute( aux );

    }


    public void showMapa(View view ) {
        //Intent intent = new Intent(this, MapaActivity.class);
        //startActivity(intent);

        Intent intent = new Intent(this, MapaActivity.class  );

        intent.putExtra("LivroAux", LivroAux );
        startActivity(intent);

    }


}