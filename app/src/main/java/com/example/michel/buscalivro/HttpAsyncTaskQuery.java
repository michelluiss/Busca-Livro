package com.example.michel.buscalivro;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public abstract class HttpAsyncTaskQuery extends AsyncTask<String, Void, String> {


    public JSONObject livro;

    @Override
    protected String doInBackground(String... termos ) {

        return POST( termos[0] );

    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {

        // tratar JSON
        try {
            JSONObject json = new JSONObject(result);
            JSONArray livros = json.getJSONArray("livro");
            for(int i = 0 ; i < livros.length(); i++){
                this.livro = livros.getJSONObject(i);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static String POST(String termo ){

        InputStream inputStream = null;
        String result = "";

        HttpClient httpclient = new DefaultHttpClient();
        //local
        //HttpPost httppost = new HttpPost("http://192.168.1.30/conexaoBancoAndroid/selectLivroLocal.php");
        //prod
        HttpPost httppost = new HttpPost("https://buscalivro.000webhostapp.com/selectLivro.php");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            nameValuePairs.add(new BasicNameValuePair("classificacao" , termo ));

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

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

}
