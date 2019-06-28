package com.example.michel.buscalivro;


import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MapaActivity extends AppCompatActivity implements View.OnTouchListener {

    private ImageView mapa;
    private ImageView livro;
    private ImageView pessoa;
    //private TextView subTitle;

    private double valorCMinPxX;
    private double valorCMinPxY;

    private int posiX;
    private int posiY;
    private float posiXuser;
    private float posiYuser;
    private double triAdistanceToPointX;
    private double triAdistanceToPointY;
    private double triBdistanceToPointYFim;
    private double baseXCasa;
    private double baseYCasa;

    private double alturaTriA;
    private double alturaTriB;

    private double baseXProd;
    private double baseYProd;

    private float triALadoA;
    private float triALadoB;

    private float triBLadoA;
    private float triBLadoB;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest = new LocationRequest();
    private Boolean mRequestingLocationUpdates;


    Livro livroPosi;

    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    Matrix livroMatrix = new Matrix();
    Matrix pessoaMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    PointF pessoaPoint = new PointF();
    PointF livroPoint = new PointF();
    float oldDist = 1f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        mRequestingLocationUpdates = true;
        //createLocationRequest();

        baseXCasa = 2.68;
        baseYCasa = 2.74;
        //baseXProd = 3.21;
        //baseYProd = 7.09;
        baseXProd = 12.3;
        baseYProd = 17.79;
        valorCMinPxX = 0.7882;
        valorCMinPxY = 0.6850;

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        // Recuperando o objeto
        livroPosi = (Livro) intent.getSerializableExtra("LivroAux");

        final TextView titulo = findViewById(R.id.titulo);
        final TextView classificacao = findViewById(R.id.classificacao);
        final TextView estante = findViewById(R.id.estante);
        final TextView prateleira = findViewById(R.id.prateleira);

        titulo.setText( livroPosi.getNome().toString());
        classificacao.setText( livroPosi.getClassificacao().toString());
        estante.setText( livroPosi.getEstante().toString());
        prateleira.setText( livroPosi.getPrateleira().toString());

        posiX = Integer.parseInt( livroPosi.getPosiX() );
        posiY = Integer.parseInt( livroPosi.getPosiY() );

        mapa = (ImageView) this.findViewById(R.id.mapa);
        mapa.setOnTouchListener(this);
        livro = (ImageView) this.findViewById(R.id.livro);
        pessoa = (ImageView) this.findViewById(R.id.pessoa);
        //subTitle = (TextView) this.findViewById(R.id.subtitle);
        //pessoaPoint.set(325,700);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if(permissionGranted) {
            // {Some Code}
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }


        Task<Location> task =  mFusedLocationClient.getLastLocation();

        Task<Location> locationTask = mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.

                if (location != null) {
                    //loc.setText(location.toString());
                    Location myLocTest = new Location("teste");
                    myLocTest.setLatitude(-20.457237);
                    myLocTest.setLongitude(-45.451771);

                    Location coordPontoX = new Location("Ponto X");
                    //pullup
                    //coordPontoX.setLatitude(-20.463886);
                    //coordPontoX.setLongitude(-45.428885);
                    //biblioteca
                    coordPontoX.setLatitude(-20.457332);
                    coordPontoX.setLongitude(-45.451836);
                    //casa
                    //coordPontoX.setLatitude(-20.471953);
                    //coordPontoX.setLongitude(-45.419290);

                    Location coordPontoY = new Location("Ponto Y");
                    //pullup
                    //coordPontoY.setLatitude(-20.461211);
                    //coordPontoY.setLongitude(-45.428896);
                    //biblioteca
                    coordPontoY.setLatitude(-20.457226);
                    coordPontoY.setLongitude(-45.451873);
                    //casa
                    //coordPontoY.setLatitude(-20.471959);
                    //coordPontoY.setLongitude(-45.419287);

                    Location coordPontoYFim = new Location("Ponto Y fim");
                    //pullup
                    //coordPontoYFim.setLatitude(-20.461196);
                    //coordPontoYFim.setLongitude(-45.428893);
                    //biblioteca
                    coordPontoYFim.setLatitude(-20.457177);
                    coordPontoYFim.setLongitude(-45.451710);
                    //-20.457177, -45.451710 nova medida com 17.79 metros
                    // -20.457166 , -45.451659 antiga medida com 23 metros
                    //casa
                    //coordPontoYFim.setLatitude(-20.471950);
                    //coordPontoYFim.setLongitude(-45.419294);

                    triAdistanceToPointX  = location.distanceTo( coordPontoX );
                    triAdistanceToPointY  = location.distanceTo( coordPontoY );
                    triBdistanceToPointYFim  = location.distanceTo( coordPontoYFim );


                }
            }
        });

        locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    // Task completed successfully
                    alturaTriA = calcDistToX( triAdistanceToPointX , triAdistanceToPointY , baseXProd );
                    alturaTriB = calcDistToY( triAdistanceToPointY, triBdistanceToPointYFim , baseYProd );
                    //Toast.makeText(getBaseContext(), "tri A : " + triAdistanceToPointX +", "+ triAdistanceToPointY+", "+ baseXCasa , Toast.LENGTH_LONG).show();
                    float x = (340 * (float)alturaTriA) / (float)baseXProd;
                    float y = (400 * (float)alturaTriB) / (float)baseYProd;
                    double conversao1 = alturaTriA * valorCMinPxX;
                    double conversao2 = alturaTriB * valorCMinPxX;
                    posiXuser = (float) conversao1;
                    posiYuser = (float) conversao2;

                    // Task completed successfully
                    //int ptoX = (int)alturaTriA;
                    //int ptoY = (int)alturaTriB;
                    if( y > 400 ){
                        Toast.makeText(getBaseContext(), "Você está fora da área de mapeamento!", Toast.LENGTH_LONG).show();
                    }
                    pessoaPoint.set( x , 400 - y);
                    livroPoint.set(posiX , posiY);
                    livroMatrix.setTranslate( livroPoint.x,livroPoint.y);
                    livro.setImageMatrix(livroMatrix);
                    pessoaMatrix.setTranslate( pessoaPoint.x,pessoaPoint.y);
                    pessoa.setImageMatrix(pessoaMatrix);
                    //Toast.makeText(getBaseContext(), "location!" + alturaTriA +", "+ alturaTriB, Toast.LENGTH_LONG).show();
                    Location result = task.getResult();
                } else {
                    // Task failed with an exception
                    Exception exception = task.getException();
                }
            }
        });



    } // fim metodo onCreate()

    public void locationUpdate ( View view ){

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(permissionGranted) {
            // {Some Code}
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }


        //Task<Location> task =  mFusedLocationClient.getLastLocation();

        Task<Location> locationTask = mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.

                if (location != null) {
                    //loc.setText(location.toString());
                    Location myLocTest = new Location("teste");
                    myLocTest.setLatitude(-20.457237);
                    myLocTest.setLongitude(-45.451771);

                    Location coordPontoX = new Location("Ponto X");
                    //pullup
                    //coordPontoX.setLatitude(-20.463886);
                    //coordPontoX.setLongitude(-45.428885);
                    //biblioteca
                    coordPontoX.setLatitude(-20.457332);
                    coordPontoX.setLongitude(-45.451836);
                    //casa
                    //coordPontoX.setLatitude(-20.471953);
                    //coordPontoX.setLongitude(-45.419290);

                    Location coordPontoY = new Location("Ponto Y");
                    //pullup
                    //coordPontoY.setLatitude(-20.461211);
                    //coordPontoY.setLongitude(-45.428896);
                    //biblioteca
                    coordPontoY.setLatitude(-20.457226);
                    coordPontoY.setLongitude(-45.451873);
                    //casa
                    //coordPontoY.setLatitude(-20.471959);
                    //coordPontoY.setLongitude(-45.419287);

                    Location coordPontoYFim = new Location("Ponto Y fim");
                    //pullup
                    //coordPontoYFim.setLatitude(-20.461196);
                    //coordPontoYFim.setLongitude(-45.428893);
                    //biblioteca
                    coordPontoYFim.setLatitude(-20.457166);
                    coordPontoYFim.setLongitude(-45.451659);
                    //casa
                    //coordPontoYFim.setLatitude(-20.471950);
                    //coordPontoYFim.setLongitude(-45.419294);

                    triAdistanceToPointX  = location.distanceTo( coordPontoX );
                    triAdistanceToPointY  = location.distanceTo( coordPontoY );
                    triBdistanceToPointYFim  = location.distanceTo( coordPontoYFim );


                }
            }
        });

        locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    // Task completed successfully
                    alturaTriA = calcDistToX( triAdistanceToPointX , triAdistanceToPointY , baseXProd );
                    alturaTriB = calcDistToY( triAdistanceToPointY, triBdistanceToPointYFim , baseYProd );
                    //Toast.makeText(getBaseContext(), "tri A : " + triAdistanceToPointX +", "+ triAdistanceToPointY+", "+ baseXCasa , Toast.LENGTH_LONG).show();
                    float x = (340 * (float)alturaTriA) / (float)baseXProd;
                    float y = (400 * (float)alturaTriB) / (float)baseYProd;
                    double conversao1 = alturaTriA * valorCMinPxX;
                    double conversao2 = alturaTriB * valorCMinPxX;
                    posiXuser = (float) conversao1;
                    posiYuser = (float) conversao2;

                    // Task completed successfully
                    //int ptoX = (int)alturaTriA;
                    //int ptoY = (int)alturaTriB;
                    if( y > 400 ){
                        Toast.makeText(getBaseContext(), "Você está fora da área de mapeamento!", Toast.LENGTH_LONG).show();
                    }
                    pessoaPoint.set( x , 400 - y);
                    livroPoint.set(posiX , posiY);
                    livroMatrix.setTranslate( livroPoint.x,livroPoint.y);
                    livro.setImageMatrix(livroMatrix);
                    pessoaMatrix.setTranslate( pessoaPoint.x,pessoaPoint.y);
                    pessoa.setImageMatrix(pessoaMatrix);
                    //Toast.makeText(getBaseContext(), "location!" + alturaTriA +", "+ alturaTriB, Toast.LENGTH_LONG).show();
                    Location result = task.getResult();
                } else {
                    // Task failed with an exception
                    Exception exception = task.getException();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // {Some Code}
                    //LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, startActivity );
                }
            }
        }
    }

    public double calcArea( double a, double b , double c ){

        double area;
        area = ( a + b + c ) / 2;
        return area;
    }

    public double calcDistToX( double a, double b , double c ){

        double s = calcArea( a , b , c );
        double area , aux , h;

        area = s * ( (s - a)* (s - b) * ( s - c ) );
        aux = 0.5 * c;
        h = Math.sqrt( area ) / aux;

        return h;
    }

    public double calcDistToY( double a, double b , double c ){

        double s = calcArea( a , b , c );
        double area , aux , h;

        area = s * ( (s - a)* (s - b) * ( s - c ) );
        aux = 0.5 * c;
        h = Math.sqrt( area ) / aux;

        return h;
    }


/*
    protected void createLocationRequest() {

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        //LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // Todas as configurações de localização estão satisfeitas. O cliente pode inicializar
                // pedidos de localização aqui.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // As configurações de localização não estão satisfeitas, mas isso pode ser corrigido
                    // mostrando ao usuário uma caixa de diálogo.
                    try {
                        // Mostra o diálogo chamando startResolutionForResult (),
                        // e verifique o resultado em onActivityResult ().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapaActivity.this, 1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (permissionGranted) {
            // {Some Code}
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
    */

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        view.setImageMatrix(matrix); // display the transformation on screen
        //livro.setImageMatrix(matrix);
        livro.setImageMatrix(livroMatrix);
        pessoa.setImageMatrix(pessoaMatrix);
        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(MotionEvent event)
    {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
        {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++)
        {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }
}
