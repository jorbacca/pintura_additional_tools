package edu.udg.bcds.pintura.tools;

import java.io.InputStream;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import edu.udg.bcds.pintura.arapp.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;


public class SelfAssessment extends Activity {
	
	ImageView contenedor;
	Button btn_cerrar;
	Bitmap imagen_descargada;
	
	int ScreenWidth;
	int ScreenHeight;
	
	String url_imagen;
	
	String  url_servidor;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_assessment);
        
        Log.d("Unity","Se ha iniciado la actividad para mostrar la imagen - SelfAssessment");
        
        contenedor = (ImageView) findViewById(R.id.image_to_show);
        btn_cerrar = (Button) findViewById(R.id.btn_close);
        
        Bundle extras = getIntent().getExtras();
        
        url_imagen = extras.getString("img_url");
        url_servidor = extras.getString("url_server");
        
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        ScreenWidth = display.getWidth();
        ScreenHeight = display.getHeight();
        
      //new DownloadImageTask((ImageView) findViewById(R.id.image_to_show)).execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");
        if(url_imagen != null){
        	Log.d("Unity","SelfAssessment: Cargando la imagen: " + url_imagen);
        	Log.d("Unity","La URL para descargar la img es:  " + url_servidor + url_imagen);
        	 //new DownloadImageTask(contenedor).execute("http://piranya.udg.edu/pintuRA/images/bcds_logo.gif");
        	new DownloadImageTask(contenedor).execute(url_servidor + url_imagen);
        } else {
        	new DownloadImageTask(contenedor).execute(url_servidor + "images/" + "bcds_logo.gif");
        }
        
        /*
        Picasso.with(getApplicationContext()).load("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png").into(contenedor, new Callback(){
        	
        	@Override
        	public void onSuccess(){
        		Log.e("Unity", "Imagen Cargada!!!");
        	}

			@Override
			public void onError() {
				Log.e("Unity", "Error cargando la imagen");
				
			}
        });
        */
        /*
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
            	Log.e("Unity","Error cargando la imagen!!");
                Log.e("Unity", "Error: " + exception.getMessage());
            }
        });
        builder.build().load("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png").into(contenedor);
        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.self_assessment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public static void Call(Activity activity, String dos)
	 {	
		 Log.d("Unity", "Metodo estatico en SelfAssessment!!!" + activity.toString());
		 // Creating an intent with the current activity and the activity we wish to start
		 Intent myIntent = new Intent(activity, SelfAssessment.class);
		 Log.d("Unity", "Creación del intent SelfAssessment " + myIntent.toString());
		 activity.startActivity(myIntent);
		 Log.d("Unity", "Despues de start activity SelfAssessment " + myIntent.toString());
		 
	 }
    
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                Log.d("Unity","Despues de descargar la img!!");
                if(mIcon11 != null)
                	Log.d("Unity","Propiedades: " + mIcon11.getWidth());
                else Log.e("Unity", "No se ha podido cargar la imagen!!");
                    	
                
            } catch (Exception e) {
                Log.e("Unity", "Error en SelfAssessment: " + e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
        	Log.d("Unity","Llamando al PostExecute!!");
        	imagen_descargada = result;
        	LoadImageContenedor();
        }
    }
    
    public void LoadImageContenedor(){
    	
    	if (imagen_descargada != null){
    		/*
	    	if(imagen_descargada.getHeight() > ScreenHeight){
	        	Log.d("Unity", "La imagen es mayor que la dimension de la pantalla height=" + ScreenHeight);
	        	
	        	LayoutParams params_imagen = contenedor.getLayoutParams();
	        	LayoutParams params_btn = btn_cerrar.getLayoutParams();
	        	
	        	Log.d("Unity", "La dimension del boton es: " + params_btn.height);
	        	
	        	float scale = getResources().getDisplayMetrics().density;
	        	int padding_footer = (int) (scale * 50);
	        	                	
	        	params_imagen.height = ScreenHeight - padding_footer;
	        	
	        	contenedor.setLayoutParams(params_imagen);
	        	
	        }  //cierra if que valida si la img es mayor que la altura de la pantalla:
	    	*/
	    	contenedor.setImageBitmap(imagen_descargada);
	    	
    	} else {
    		//Si la imagen descargada es NULL entonces se deja la imagen por defecto.
    		Log.w("Unity", "No se carga la nueva imagen porque no se pudo descargar...");
    	}
    	  
    	
    	
    } //cierra metodo LoadImageContenedor
    
    
    /**
     * Metodo que cierra la imagen
     */
    public void CerrarActividad(View view){
    	SelfAssessment.this.finish();
    }
    
    
    

    
}
