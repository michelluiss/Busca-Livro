package com.example.michel.buscalivro;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import org.jsoup.nodes.Element;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;

public class MainActivity extends AppCompatActivity {


    EditText etResponse;
    TextView tvIsConnected;
    ArrayAdapter<String> originalAdapter;

    ArrayList<Livro> livros = new ArrayList<Livro>();
    String urlSelect = "http://192.168.0.37/conexaoBancoAndroid/showStudent.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        // get reference to the views
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);

        // check if you are connected or not
        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are conncted");
        }
        else{
            tvIsConnected.setText("You are NOT conncted");
        }
        */

        Button btnPrev = (Button) findViewById(R.id.prevPage);
        btnPrev.setEnabled(false);
        Button btnNext = (Button) findViewById(R.id.nextPage);
        btnNext.setEnabled(false);

        // call AsynTask to perform network operation on separate thread
        final String texto = ((TextView) findViewById(R.id.editText)).getText().toString();

        originalAdapter = new ArrayAdapter(MainActivity.this, R.layout.layout,  new String[]{"Nada encotrado"});

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(originalAdapter);


        final TextView textView = findViewById(R.id.editText);

        // Set an item click listener for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            sendData( this , position );

            }
        });

        //new HttpAsyncTask().execute(texto);

    }

    public void sendData(AdapterView.OnItemClickListener view , int pos ) {
        Intent intent = new Intent(this, showBookData.class  );

        Livro livroAux;
        livroAux = livros.get(pos);
        livroAux.setAutor("x");
        livroAux.setEstante("x");
        livroAux.setPrateleira("x");
        livroAux.setPosiX("x");
        livroAux.setPosiY("x");

        intent.putExtra("livro", livroAux );
        startActivity(intent);
    }


    public void busca(View view){

        String texto = ((TextView) findViewById(R.id.editText)).getText().toString();
        new HttpAsyncTask().execute(texto);
    }

    public void nextPage(View view){
        atualizaPageAtual( "next" );
        new HttpAsyncTask().execute( "next" );
    }

    public void prevPage(View view){
        atualizaPageAtual( "prev" );
        new HttpAsyncTask().execute( "prev" );
    }


    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void atualizaPageAtual( String acao ) {

        if ( acao.equals("next") ){
            TextView numPageAtual = (TextView) findViewById(R.id.paginaAtual);
            Integer page = Integer.parseInt(numPageAtual.getText().toString());
            page ++;
            numPageAtual.setText( page.toString() );
        }else if ( acao.equals("prev") ){
            TextView numPageAtual = (TextView) findViewById(R.id.paginaAtual);
            Integer page = Integer.parseInt(numPageAtual.getText().toString());
            page --;
            numPageAtual.setText( page.toString() );
        }

    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public static String POST(String termo , String pagina  ){
        InputStream inputStream = null;
        String result = "";

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://biblioteca.uniformg.edu.br:21010/index.php?module=gnuteca3&action=main:search:simpleSearch");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            nameValuePairs.add(new BasicNameValuePair("personIdUpper" ,""));
            nameValuePairs.add(new BasicNameValuePair("passwordUpper",""));
            nameValuePairs.add(new BasicNameValuePair("advFilterControl","11"));
            nameValuePairs.add(new BasicNameValuePair("termControl",""));
            nameValuePairs.add(new BasicNameValuePair("libraryUnitId","1"));
            nameValuePairs.add(new BasicNameValuePair("termType[]","090.d,100.a,110.a,110.b,111.a,245.a,245.b,246.a,490.a,490.v,600.a,650.a,650.z,653.a,656.a,700.a,780.t,785.t,901.e,950.a,949.1,949.a,949.b,949.d"));
            nameValuePairs.add(new BasicNameValuePair("termCondition[]","LIKE"));
            nameValuePairs.add(new BasicNameValuePair("termText[]",termo));
            nameValuePairs.add(new BasicNameValuePair("termOpt[]",""));
            nameValuePairs.add(new BasicNameValuePair("materialTypeId",""));
            nameValuePairs.add(new BasicNameValuePair("searchFormat","1"));
            nameValuePairs.add(new BasicNameValuePair("exemplaryStatusId",""));
            nameValuePairs.add(new BasicNameValuePair("editionYearFrom",""));
            nameValuePairs.add(new BasicNameValuePair("editionYearTo",""));
            nameValuePairs.add(new BasicNameValuePair("orderField","5"));
            nameValuePairs.add(new BasicNameValuePair("orderType","3"));
            nameValuePairs.add(new BasicNameValuePair("letter",""));
            nameValuePairs.add(new BasicNameValuePair("letterField","245.a"));
            nameValuePairs.add(new BasicNameValuePair("aquisitionFrom",""));
            nameValuePairs.add(new BasicNameValuePair("aquisitionTo",""));
            nameValuePairs.add(new BasicNameValuePair("formContentTypeId_current",""));
            nameValuePairs.add(new BasicNameValuePair("initialStatusConfirmed",""));
            nameValuePairs.add(new BasicNameValuePair("GRepetitiveField",""));
            nameValuePairs.add(new BasicNameValuePair("arrayItemTemp",""));
            nameValuePairs.add(new BasicNameValuePair("keyCode","68"));
            nameValuePairs.add(new BasicNameValuePair("isModified","t"));
            nameValuePairs.add(new BasicNameValuePair("functionMode","search"));
            nameValuePairs.add(new BasicNameValuePair("frm5a8c03c57e849_action:https","//biblioteca.uniformg.edu.br:21010/index.php?module=gnuteca3&action=main:search:simpleSearch"));
            nameValuePairs.add(new BasicNameValuePair("__mainForm__VIEWSTATE",""));
            nameValuePairs.add(new BasicNameValuePair("__mainForm__ISPOSTBACK","yes"));
            nameValuePairs.add(new BasicNameValuePair(" __mainForm__EVENTTARGETVALUE","searchFunction"));
            nameValuePairs.add(new BasicNameValuePair("__mainForm__EVENTARGUMENT",pagina));
            nameValuePairs.add(new BasicNameValuePair("__FORMSUBMIT","__mainForm"));
            nameValuePairs.add(new BasicNameValuePair("__ISAJAXCALL","yes"));
            nameValuePairs.add(new BasicNameValuePair(" __THEMELAYOUT","dynamic"));
            nameValuePairs.add(new BasicNameValuePair("__MIOLOTOKENID",""));
            nameValuePairs.add(new BasicNameValuePair("__ISFILEUPLOAD","no"));
            nameValuePairs.add(new BasicNameValuePair("__ISAJAXEVENT","yes"));
            nameValuePairs.add(new BasicNameValuePair("cpaint_response_type","json"));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpclient.execute(httppost);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... termos ) {

            if ( termos[0].equals("next") ){
                String pageAtual = ((TextView) findViewById(R.id.paginaAtual)).getText().toString();
                Integer aux = Integer.parseInt(pageAtual);
                String nextPage = "gridName|~|grdSimpleSearch|#|pn_page|~|"+ aux.toString() +"|#|orderMask|~|";
                String termoPesquisa = ((EditText) findViewById(R.id.editText)).getText().toString();

                return POST( termoPesquisa , nextPage );
            } else if ( termos[0].equals("prev") ) {
                String pageAtual = ((TextView) findViewById(R.id.paginaAtual)).getText().toString();
                Integer aux = Integer.parseInt(pageAtual);
                String nextPage = "gridName|~|grdSimpleSearch|#|pn_page|~|"+ aux.toString() +"|#|orderMask|~|";
                String termoPesquisa = ((EditText) findViewById(R.id.editText)).getText().toString();
                TextView novaPage = (TextView) ((TextView) findViewById(R.id.paginaAtual));

                return POST( termoPesquisa , nextPage );
            } else {
                String nextPage = "";
                return POST( termos[0] , nextPage );
            }

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            ArrayList<String> itens = new ArrayList<String>() ;
            livros = new ArrayList<Livro>();
            // tratar JSON
            try {

                JSONObject json = new JSONObject(result);
                JSONObject data = json.getJSONObject("data");
                result = data.getJSONArray("html").getString(0);

                Document doc = Jsoup.parse(result);
                Elements classe = doc.getElementsByClass("divMaterialContent");
                Iterator<Element> it = classe.iterator();

                Element paginacao = doc.getElementById("countLabel");

                Integer qntPages = Integer.parseInt(paginacao.text().substring( paginacao.text().indexOf("em")+3 , paginacao.text().indexOf("página(s)")-1 ));
                TextView numPageAtual = (TextView) findViewById(R.id.paginaAtual);
                Integer page = Integer.parseInt(numPageAtual.getText().toString());
                if ( qntPages > 1 ){
                    Button btnNext = (Button) findViewById(R.id.nextPage);
                    btnNext.setEnabled(true);
                }else if ( qntPages == 1 ){
                    Button btnNext = (Button) findViewById(R.id.nextPage);
                    btnNext.setEnabled(false);
                    Button btnPrev = (Button) findViewById(R.id.prevPage);
                    btnPrev.setEnabled(false);
                    numPageAtual.setText( "1" );
                }
                if ( page == 0 ){
                    page ++;
                    numPageAtual.setText( page.toString() );
                }
                if ( page == qntPages ){
                    Button btnNext = (Button) findViewById(R.id.nextPage);
                    btnNext.setEnabled(false);
                }
                if ( page > 1 ){
                    Button btnPrev = (Button) findViewById(R.id.prevPage);
                    btnPrev.setEnabled(true);
                }else {
                    Button btnPrev = (Button) findViewById(R.id.prevPage);
                    btnPrev.setEnabled(false);
                }

                Element metaElem;
                while(it.hasNext()){
                    metaElem = it.next();
                    Livro livro = new Livro();

                    String texto = metaElem.text();
                    String titulo = "";
                    String classi = "";
                    Boolean anexado = false;

                    if ( texto.contains("Data")) {
                        titulo = texto.substring(texto.indexOf("Título: ")+"Título: ".length(), texto.indexOf("Data") - 1 );
                        classi = texto.substring(texto.indexOf("Classificação: ")+"Classificação: ".length() );
                        titulo = titulo.replace( "/","");
                        livro.setNome( titulo );
                        livro.setClassificacao( classi );
                        anexado = true;
                    }else if ( texto.contains("Autor:") && texto.contains("Número do item:") && anexado == false ) {
                        titulo = texto.substring(texto.indexOf("Título: ")+"Título: ".length(), texto.indexOf("Autor:") - 1 );
                        classi = texto.substring(texto.indexOf("Classificação: ")+"Classificação: ".length() , texto.indexOf("Número do item:") - 1 );
                        titulo = titulo.replace( "/","");
                        livro.setNome( titulo );
                        livro.setClassificacao( classi );
                        anexado = true;
                    }else if ( texto.contains("Autor:") && anexado == false  ) {
                        titulo = texto.substring(texto.indexOf("Título: ")+"Título: ".length(), texto.indexOf("Autor:") - 1 );
                        classi = texto.substring(texto.indexOf("Classificação: ")+"Classificação: ".length() );
                        titulo = titulo.replace( "/","");
                        livro.setNome( titulo );
                        livro.setClassificacao( classi );
                        anexado = true;
                    }else if ( texto.contains("Ano:") && texto.contains("Número do item:") && anexado == false ) {
                        titulo = texto.substring(texto.indexOf("Título: ")+"Título: ".length(), texto.indexOf("Ano:") - 1 );
                        classi = texto.substring(texto.indexOf("Classificação: ")+"Classificação: ".length() , texto.indexOf("Número do item:") - 1 );
                        titulo = titulo.replace( "/","");
                        livro.setNome( titulo );
                        livro.setClassificacao( classi );titulo = titulo.replace( titulo,"/");
                        anexado = true;
                    }
                    else if ( texto.contains("Ano:") && anexado == false ) {
                        titulo = texto.substring(texto.indexOf("Título: ")+"Título: ".length(), texto.indexOf("Ano:") - 1 );
                        classi = texto.substring(texto.indexOf("Classificação: ")+"Classificação: ".length() );
                        titulo = titulo.replace( "/","");
                        livro.setNome( titulo );
                        livro.setClassificacao( classi );
                        anexado = true;
                    }
                    livros.add(livro);
                    itens.add( titulo);
                }


            }catch (Exception e){
                e.printStackTrace();
            }

            originalAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, itens);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(originalAdapter);
        }
    }

}
