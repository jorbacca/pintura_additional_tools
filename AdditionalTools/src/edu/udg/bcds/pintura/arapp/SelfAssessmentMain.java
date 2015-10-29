package edu.udg.bcds.pintura.arapp;



import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.udg.bcds.pintura.library.Httppostlibrary;
import edu.udg.bcds.pintura.tools.SelfAssessment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.unity3d.player.UnityPlayer;

public class SelfAssessmentMain extends ListActivity {
	
	Httppostlibrary post;
	
	private ProgressDialog pDialog;
	public String numero_preguntas;
	public String numero_paso;
	public String url_server;
	public String codigo_estudiante;
	public String tipo_test;
	ArrayList<String> nombres_preguntas= new ArrayList<String>(1);
	Pregunta[] preguntas_guardadas;
	MyArrayAdapter<String> adapter;
	
	private int posicion_array;
	private String respuesta_obtenida;
	
	//variable que indica si el test que se carga es nuevo o es antiguo. Si es antiguo entonces trae respuestas a las preguntas
	private boolean test_nuevo;
	
	private boolean test_enviado_validando;
	
	private int intento_contador;
	
	private boolean test_failed;
	
	//private boolean control_iconos_display; //si se habilita esta variable y todos los lugares donde es utilizada entonces el sistema hace lo siguiente: Primero
											  //no cambia el icono cuando la respuesta se vuelve correcta o incorrecta despues de haber enviado la evaluacion una vez.
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intention = getIntent();
		numero_preguntas = intention.getStringExtra("pregs");
		numero_paso = intention.getStringExtra("paso");
		url_server = intention.getStringExtra("url_conexion");
		codigo_estudiante = intention.getStringExtra("id_estudiante");
		tipo_test = intention.getStringExtra("tipo_test");
		setContentView(R.layout.activity_self_assessment_main);
		
		posicion_array = 0;
		
		post = new Httppostlibrary();
		//OJO: TENGO QUE HABILITAR  ESTE LLAMADO PARA QUE SE EJECUTE EL PROCESO:
		new AsyncConsultarDatosPreguntas().execute(numero_preguntas,numero_paso,codigo_estudiante,tipo_test);
		
		//se inicializa la variable test enviado en false porque no se esta validando el test.
		//Esta variable solo se hace true cuando se hace click en enviar el test y cuando se ha validado que todas las preguntas han sdo contestadas
		//en el metodo EnviarEvaluacion:
		test_enviado_validando = false;
		
		//inicializando la variable que permite que solo una vez se actualicen los iconos de bien o mal en ellistado de preguntas:
		//esta variable se usa en el ArrayAdapter
		//control_iconos_display = true;
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.self_assessment_main, menu);
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
	
	public static void StartModule(Activity activity, String num_pregs, String paso, String url, String codigo, String tipo_test_param)
	 {	
		 Log.d("Unity", "Llamado al metodo estatico!!!" + activity.toString());
		 Log.d("Unity","Parametro 1: " + num_pregs);
		 Log.d("Unity","Parametro 2: " + paso);
		 Log.d("Unity","Parametro 3: " + url);
		 Log.d("Unity", "Parametro 4: " + codigo);
		 Log.d("Unity", "Parametro 5: " + tipo_test_param);
		 
		 //Testing the module values assigned provisional:
		 //num_pregs = "8";
		 //paso = "1";
		 
		 
		 // Creating an intent with the current activity and the activity we wish to start
		 Intent myIntent = new Intent(activity, SelfAssessmentMain.class);
		 //Se envia como parametro la url del video que se envio desde Unity:
		 myIntent.putExtra("pregs", num_pregs);
		 myIntent.putExtra("paso", paso);
		 myIntent.putExtra("url_conexion", url);
		 myIntent.putExtra("id_estudiante", codigo);
		 myIntent.putExtra("tipo_test", tipo_test_param);
		 //myIntent.putExtra("URL", "http://piranya.udg.edu/pintuRA/videos/1_limpieza.mp4");
		 Log.d("Unity", "Creación del intent: " + myIntent.toString());
		 activity.startActivity(myIntent);
		 Log.d("Unity", "Despues de start activity " + myIntent.toString());
		 
	 } // cierra metodo StartModuleAssessment
	
	
	/**
	 * 
	 * @param num_preguntas
	 * @param paso_preg
	 * @return Pregunta[]
	 * 
	 * @
	 */
	public Pregunta[] ConsultarPreguntasHttp(String num_preguntas, String paso_preg, String cod_estudiante, String tipo_test_env ) {
		
		int status=-1;
		String resultado_test_nuevo;
		String preg_contestada_tmp;
		//array de objetos tipo Pregunta donde se almacenaran todas las preguntas que retorne el servicio.
		//este array es de tipo Pregunta (ver la clase Pregunta en el paquete)
		Pregunta[] preguntas_test = new Pregunta[1];
		//inicializando el array con un elemento vacio en caso de que se presente algun error consultando el servicio:
		preguntas_test[0] = new Pregunta("0","0", "0", "0", "0", "0", "0", "0",true,"","","",0);
		/*Creamos un ArrayList del tipo nombre valor para agregar los datos recibidos por los parametros anteriores
		 * y enviarlo mediante POST a nuestro sistema para relizar la validacion*/ 
		ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	 		
		   postparameters2send.add(new BasicNameValuePair("numero_pregs",num_preguntas));
		   postparameters2send.add(new BasicNameValuePair("paso",paso_preg));
		   postparameters2send.add(new BasicNameValuePair("id_estudiante",cod_estudiante));
		   postparameters2send.add(new BasicNameValuePair("tipo_test",tipo_test_env));
		   
		    		
		    		
		   Log.v("Unity", "Los parametros a enviar son: numero_pregs=" + num_preguntas + ", paso=" + paso_preg);

		   //realizamos una peticion al servicio y como respuesta obtenes un array JSON
		   Log.v("Unity", "La URL de conexion sera: " + url_server + "getquestions.php");
		   
	  	JSONArray jdata=post.getserverdata(postparameters2send, url_server + "getquestions.php");
	  	   
	  	 Log.v("Unity", "Datos recibidos del servicio: " + jdata.toString());
	  	 Log.v("Unity", "Longitud del array: " + jdata.length());
	  	 
	  		
	  		//se verifica si la respuesta del servicio es diferente de null
	  		if (jdata!=null){
	  			//se verifica si la respuesta del servicio es mayor que 0 es decir 1 o mas ofertas
	  			if(jdata.length() > 1){
	  				//variable que almacena el array JSON de las ofertas:
			  		JSONArray json_data;
			  		JSONArray json_status;
			  		JSONObject json_obj;
			  		//contador para hacer un ciclo sobre todas las ofertas:
			  		int cont=0;
			  		try{
			  			Log.d("Unity", "El primer elemento del array es: " + jdata.getString(0));
			  			//el primero elemento del array trae el valor que indica si el test es nuevo o no:
			  			resultado_test_nuevo = jdata.getString(0);
			  			//validando si el test es nuevo si viene un 1 o si es antiguo si viene un 0
			  			if(resultado_test_nuevo.equalsIgnoreCase("0")){
			  				test_nuevo = false;
			  			}else test_nuevo = true;
			  			
			  			preguntas_test = new Pregunta[jdata.length()-1];
			  			Log.v("Unity","Se ha creado el array de preguntas con longitud=" + preguntas_test.length);
			  			
			  			for(int i = 1; i < jdata.length(); i++){
			  				json_obj = jdata.getJSONObject(i);
			  				preg_contestada_tmp = json_obj.getString("resp_pregunta");
			  				Log.d("Unity","El intent_respond que se recibe del servicio es: " + json_obj.getInt("intento_respond"));
			  				if(preg_contestada_tmp.equalsIgnoreCase("")){
			  					preguntas_test[i-1] = new Pregunta(json_obj.getString("id_pregunta"),json_obj.getString("enunciado"), json_obj.getString("opcion_a"), json_obj.getString("opcion_b"), json_obj.getString("opcion_c"), json_obj.getString("opcion_d"), json_obj.getString("respuesta_correcta"), json_obj.getString("archivo_imagen"),false, json_obj.getString("resp_pregunta"),json_obj.getString("id_test"),json_obj.getString("intento"),json_obj.getInt("intento_respond"));
			  					
			  				} else {
			  					preguntas_test[i-1] = new Pregunta(json_obj.getString("id_pregunta"),json_obj.getString("enunciado"), json_obj.getString("opcion_a"), json_obj.getString("opcion_b"), json_obj.getString("opcion_c"), json_obj.getString("opcion_d"), json_obj.getString("respuesta_correcta"), json_obj.getString("archivo_imagen"),true, json_obj.getString("resp_pregunta"),json_obj.getString("id_test"),json_obj.getString("intento"),json_obj.getInt("intento_respond"));
			  					//debido a que la pregunta ya ha sido respondida anteriormente, entonces se valida la respuesta para cargar bien el dato en el objeto Pregunta:
			  					preguntas_test[i-1].ValidarRespuesta(json_obj.getString("resp_pregunta"));
			  				}
			  				Log.v("Unity","Iteracion=" + i);
			  			}
			  			
			  			Log.v("Unity","Finaliza el ciclo de recorrer JSON...");
			  				  			
			  			
			  		}catch(Exception e){
			  			e.printStackTrace();
			  			Log.v("Unity", "Error obteniendo JSON del servicio en ConsultarPreguntasHttp" + e);
			  			preguntas_test = new Pregunta[1];
				  		preguntas_test[0] = new Pregunta("0","0", "0", "0", "0", "0", "0", "0", true,"","","",0);
			  		} //cierra catch
			  		
			  	}else { //cuando jdata.length < 0 (esto significa que hubo algun error) entonces:
			  		Log.v("Unity","El array viene vacio!!");
			  		preguntas_test = new Pregunta[1];
			  		preguntas_test[0] = new Pregunta("0","0", "0", "0", "0", "0", "0", "0", true,"","","",0);
			  	}
	  		}  //cierra if jdata!=null	 	
	  		  
	  		return preguntas_test;
	  		
	}//cierra metodo ofertasguardadas
	
	
	/**
	 * Clase para consultar el listado de preguntas
	 * 
	 * @author Jorge Bacca
	 *
	 */
	
	class AsyncConsultarDatosPreguntas extends AsyncTask< String, String, String > {
		 
		String numero_preguntas;
		String pregunta_paso;
		String cod_estud;
		String tipo_test_p;
		//String intento_preg_actual;
		Pregunta[] datos_recibidos;
	    protected void onPreExecute() {
	    	//para el progress dialog
	        pDialog = new ProgressDialog(SelfAssessmentMain.this);
	        pDialog.setMessage("Consultando Preguntas");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();
	    }

		protected String doInBackground(String... params) {
			//obtnemos usr y pass
			numero_preguntas=params[0];
			pregunta_paso = params[1];
			cod_estud = params[2];
			tipo_test_p = params[3];
			//intento_preg_actual = params[3];
			
			Log.v("Unity","Parametros recibidos en el doInBackground.... num=" + numero_preguntas + ", paso=" + pregunta_paso + ", estud=" + cod_estud + ",tipo="+tipo_test_p);
			
			
			datos_recibidos = ConsultarPreguntasHttp(numero_preguntas,pregunta_paso, cod_estud,tipo_test_p);
			Log.v("Unity","Datos recibidos del JSON: Enunciado de pregunta 1="+datos_recibidos[0].enunciado);
			
			//validando si del servicio se obtuvieron datos correctos:
			if(datos_recibidos[0].enunciado.equalsIgnoreCase("0") && datos_recibidos[0].opciona.equalsIgnoreCase("0")){
				Log.v("Unity","doInBackground - NO se obtuvieron datos del servicio o el array viene vacio...");
				return "err";
			}else {
				return "ok";
			}
			
				    	
		}
	   
		
	    protected void onPostExecute(String result) {

	       pDialog.dismiss();//ocultamos progess dialog.
	       Log.e("Unity","OnPostExecute= "+result);
	       
	       if (result.equalsIgnoreCase("ok")){
	    	   Log.v("Unity", "Resultado OK...");
	    	   fijarDatos(datos_recibidos);
	       }else if (result.equalsIgnoreCase("err")){
	    	   err_consultando();
	        }else {
	        	Pregunta[] pregunta_vacio = new Pregunta[1];
	        	pregunta_vacio[0]=new Pregunta("0","0", "0", "0", "0", "0", "0", "0",true,"","","",0);
	        	fijarDatos(pregunta_vacio);
	        }
	        
	    }
		
	}//cierra clase async
	
	
	/**
	 * Clase Async para consultar la cantidad de preguntas que se requiere responder para pasar el test.
	 * 
	 * @author Jorge Bacca
	 *
	 */
	
	class AsyncGetCantidadPregsAprob extends AsyncTask< String, String, String > {
		 
		String id_paso;
		String cantidad_pregs_aprobar;
	    protected void onPreExecute() {
	    	//para el progress dialog
	        pDialog = new ProgressDialog(SelfAssessmentMain.this);
	        pDialog.setMessage("Validando preguntas.... Porfavor espera un momento.");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();
	    }

		protected String doInBackground(String... params) {
			//obtnemos usr y pass
			id_paso=params[0];
			
			Log.v("Unity","Parametros recibidos en el doInBackground.... id_paso=" + id_paso);
			
			
			cantidad_pregs_aprobar = ConsultarCantidadAprobarHttp(id_paso);
			Log.v("Unity","Datos recibidos del JSON: Cantidad de pregs aprobar="+cantidad_pregs_aprobar);
			
			//validando si del servicio se obtuvieron datos correctos:
			if(cantidad_pregs_aprobar.equalsIgnoreCase("") ){
				Log.v("Unity","doInBackground - NO se obtuvieron datos del servicio o el array viene vacio...");
				return "err";
			}else {
				return "ok";
			}
			
				    	
		}
	   
		
	    protected void onPostExecute(String result) {

	       pDialog.dismiss();//ocultamos progess dialog.
	       Log.e("Unity","OnPostExecute= "+result);
	       
	       if (result.equalsIgnoreCase("ok")){
	    	   Log.v("Unity", "Resultado OK...");
	    	   fijarDatosCantAprobar(cantidad_pregs_aprobar);
	       }else if (result.equalsIgnoreCase("err")){
	    	   error_cosultando_cantidad();
	        }else {
	        	
	        	fijarDatosCantAprobar("");
	        }
	        
	    }
		
	}//cierra clase async
	
	
	public String ConsultarCantidadAprobarHttp(String idpaso) {
		
		int status=-1;
		String resultado_num_aprobar;
		String cantidad_aprobar;
		
		resultado_num_aprobar = "";
		cantidad_aprobar = "";
		
		ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	 		
		  
		   postparameters2send.add(new BasicNameValuePair("paso",idpaso));
		   
		   	    		
		   Log.v("Unity", "Los parametros a enviar en ConsultarCantidadAprobarHttp son: id_paso=" + idpaso);

		   //realizamos una peticion al servicio y como respuesta obtenes un array JSON
		   Log.v("Unity", "ConsultarCantidadAprobarHttp: La URL de conexion sera: " + url_server + "getcantaprobar.php");
		   
	  	JSONArray jdata=post.getserverdata(postparameters2send, url_server + "getcantidades.php");
	  	   
	  	 Log.v("Unity", "Datos recibidos del servicio en ConsultarCantidadAprobarHttp: " + jdata.toString());
	  	 Log.v("Unity", "Longitud del array: " + jdata.length());
	  	 
	  		
	  		//se verifica si la respuesta del servicio es diferente de null
	  		if (jdata!=null){
	  			//se verifica si la respuesta del servicio es mayor que 0 es decir 1 o mas ofertas
	  			if(jdata.length() > 0){
	  				//variable que almacena el array JSON de las ofertas:
			  		JSONArray json_data;
			  		JSONArray json_status;
			  		JSONObject json_obj;
			  		//contador para hacer un ciclo sobre todas las ofertas:
			  		int cont=0;
			  		try{
			  			Log.d("Unity", "El primer elemento del array es: " + jdata.getString(0));
			  			//el primero elemento del array trae el valor que indica si el test es nuevo o no:
			  			json_obj = jdata.getJSONObject(0);
			  			resultado_num_aprobar = json_obj.getString("cantidad");
			  			//validando si el test es nuevo si viene un 1 o si es antiguo si viene un 0
			  			if(resultado_num_aprobar.equalsIgnoreCase("-1")){
			  				cantidad_aprobar = "";
			  			}else cantidad_aprobar = resultado_num_aprobar;
			  			
			  				Log.v("Unity","Finaliza el ciclo de recorrer JSON...");
			  				  			
			  		
			  		}catch(Exception e){
			  			e.printStackTrace();
			  			Log.v("Unity", "Error obteniendo JSON del servicio en ConsultarPreguntasHttp" + e);
			  			cantidad_aprobar="";
			  			resultado_num_aprobar = "";
			  		} //cierra catch
			  		
			  	}else { //cuando jdata.length < 0 (esto significa que hubo algun error) entonces:
			  		Log.v("Unity","El array viene vacio!!");
			  		cantidad_aprobar = "";
			  		resultado_num_aprobar = "";
			  	}
	  		}  //cierra if jdata!=null	 	
	  		  
	  		return cantidad_aprobar;
	  		
	}//cierra metodo ConsultarCantidadAprobar
	
	/**
	 * Metodo que permite fijar los datos en la lista
	 * 
	 * @param Preguntas[]
	 */
	public void fijarDatosCantAprobar(String cantidad){
			
			Log.v("Unity", "fijarDatosCantAprobar: LLama al metodo fijar datos SelfAssessmentMain con cantidad="+cantidad);
			if(cantidad.equalsIgnoreCase("")){
				Log.d("Unity","Por algun motivo el servicio no ha retornado nada sobre el numero de preguntas que se deben responder para el test...");
				
			} else {
				Log.v("Unity", "fijarDatosCantAprobar:Ingresa al else con pregs para aprobar=" + cantidad + ", cantidad pregs del test: " + preguntas_guardadas.length );
				
				
				//validado si el numero de preguntas disponibles para el test es menor que el numero de preguntas necesarias para aprobar
				//entonces la cantidad minima de preguntas para aprobar sera igual a la cantidad de preguntas actual de test:
				if(preguntas_guardadas.length < Integer.valueOf(cantidad)){
					cantidad = String.valueOf(preguntas_guardadas.length);
				}
				
				int cont_pregs_correctas = 0;
				for (int cont=0;cont<preguntas_guardadas.length;cont++){
					if(preguntas_guardadas[cont].pregunta_correcta)
						cont_pregs_correctas++; 
				}
				
				//inicializando la variable test_failed para este ciclo:
				test_failed = false;
				
				Log.d("Unity","fijarDatosCantAprobar: La cantidad de preguntas correctas es: " + cont_pregs_correctas);
				
				
				//se incrementa el contador porque se va a proceder a enviar el test:
				intento_contador++;
				
				//Ahora se incrementa el intento de cada una de las preguntas del test porque en caso
				// de que el estudiante guarde una nueva respuesta se debe guardar con el nuevo intento:
				for(int cont_pregs = 0; cont_pregs < preguntas_guardadas.length; cont_pregs++){
					preguntas_guardadas[cont_pregs].intento = String.valueOf(intento_contador);
				}
				
				//validando si el estudiante ha alcanzando el minimo de respuestas correctas:
				if(cont_pregs_correctas >= Integer.valueOf(cantidad) ){
					test_failed = false;
					Log.d("Unity","fijarDatosCantAprobar: test_failed es: " + test_failed);
					
					Log.d("Unity","fijarDatosCantAprobar: Se ha alcanzado el numero de preguntas correctas necesario...");
					//guardando el envío del test.
					new AsyncSaveTest().execute(preguntas_guardadas[0].id_test,numero_paso,codigo_estudiante,"1",String.valueOf(intento_contador));
					
				} else {
					Log.d("Unity","fijarDatosCantAprobar: NO hay suficientes respuestas correctas...");
					test_failed = true;
					Log.d("Unity","fijarDatosCantAprobar: test_failed es: " + test_failed);
					new AsyncSaveTest().execute(preguntas_guardadas[0].id_test,numero_paso,codigo_estudiante,"0",String.valueOf(intento_contador));
											            
				}
				
				//notificando al adaptador de la lista que debe actualizar la lista y por tanto mostrar los iconos correspondientes.
				adapter.notifyDataSetChanged();
				
				//se invalida de aqui en adelante la actualizacion de los iconos que indican si se debe mostrar que la pregunta esta bien o mal:
				//control_iconos_display = false;
				
						
			} // cierra else de que se ha obtenido la cantidad bien
			
	} //cierra metodo fijarDatos
	
	
	/**
	 * Metodo que permite notificar de errores
	 */
	public void err_consultando(){
		//he comentareado el Vibrator porque requiere solicitar un permiso en el android manifest. Si dejo esto habilitado
		//sin solicitar el permiso entonces obtengo el siguente error: java.lang.SecurityException: Requires VIBRATE permission
		//y la aplicacion se cierra inmediatamente.
		/*
		Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	    vibrator.vibrate(200);
	    Toast toast1 = Toast.makeText(getApplicationContext(),"Error: No se pudieron consultar las preguntas", Toast.LENGTH_SHORT);
		    toast1.show();
		    Log.e("Unity", "Error consultando las preguntas en SelfAssessmentMain");
		*/  
		 Log.e("Unity", "Error consultando las preguntas en SelfAssessmentMain se va a notificar al usuario");
		    AlertDialog.Builder builder1 = new AlertDialog.Builder(SelfAssessmentMain.this);
            builder1.setMessage(R.string.message_assessment_error);
            builder1.setTitle(R.string.message_assessment_err_title);
            builder1.setPositiveButton(R.string.message_video_error_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.e("Unity", "llamado al metodo OnClick del cuadro de dialogo");
						SelfAssessmentMain.this.finish();
					Log.e("Unity", "Despues de cerrar la actividad!!!");
				}
			} );
            
            AlertDialog alert_error = builder1.create();
            alert_error.show();
	} //cierra metodo
	
	public void error_cosultando_cantidad(){
		
		 Log.e("Unity", "Error consultando la cantidad para aprobar en SelfAssessmentMain se va a notificar al usuario");
		    AlertDialog.Builder builder1 = new AlertDialog.Builder(SelfAssessmentMain.this);
            builder1.setMessage(R.string.message_cant_aprobar_msg);
            builder1.setTitle(R.string.message_cant_aprobar_title);
            builder1.setPositiveButton(R.string.message_video_error_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.e("Unity", "llamado al metodo OnClick del cuadro de dialogo");
						SelfAssessmentMain.this.finish();
					Log.e("Unity", "Despues de cerrar la actividad!!!");
				}
			} );
            
            AlertDialog alert_error = builder1.create();
            alert_error.show();
	}//cierra error_consultando_cantidad
	
	
	/**
	 * Clase Async para guardar en envío de un TEST nuevo.
	 * 
	 * @author Jorge Bacca
	 *
	 */
	
	class AsyncSaveTest extends AsyncTask< String, String, String > {
		 
		String id_test_save;
		String id_paso_save;
		String id_estud_save;
		String test_aprobado_save;
		String intento_save;
		
		String resultado_guardar;
		
	    protected void onPreExecute() {
	    	//para el progress dialog
	        pDialog = new ProgressDialog(SelfAssessmentMain.this);
	        pDialog.setMessage("Validando y Guardando el test.... Porfavor espera un momento.");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();
	    }

		protected String doInBackground(String... params) {
			//obtnemos usr y pass
			id_test_save = params[0];
			id_paso_save = params[1];
			id_estud_save = params[2];
			test_aprobado_save = params[3];
			intento_save = params[4];
			
			Log.v("Unity","Parametros recibidos en el doInBackground.... id_test_save=" + id_test_save + ",id_estud=" + id_estud_save + " test_aprob=" + test_aprobado_save);
			
			
			resultado_guardar = SaveTestHttp(id_test_save,id_estud_save,id_paso_save,test_aprobado_save,intento_save);
			Log.v("Unity","Datos recibidos del JSON: resultado_guardar="+resultado_guardar);
			
			//validando si del servicio se obtuvieron datos correctos:
			if(resultado_guardar.equalsIgnoreCase("") ||  resultado_guardar.equalsIgnoreCase("0")){
				Log.v("Unity","doInBackground - NO se obtuvieron datos del servicio o el array viene vacio...");
				return "err";
			}else {
				return "ok";
			}
			
				    	
		}
	   
		
	    protected void onPostExecute(String result) {

	       pDialog.dismiss();//ocultamos progess dialog.
	       Log.e("Unity","OnPostExecute= "+result);
	       
	       if (result.equalsIgnoreCase("ok")){
	    	   Log.v("Unity", "Resultado OK...");
	    	   fijarDatosSaveTest(resultado_guardar);
	       }else if (result.equalsIgnoreCase("err")){
	    	   error_guardando_test();
	        }else {
	        	
	        	fijarDatosSaveTest("vacio");
	        }
	        
	    }
		
	}//cierra clase async
	
	
	public String SaveTestHttp(String idtest_save, String idestud_save, String idpaso_save, String test_aprob_save, String intent_save) {
		
		
		String result_save;
		
		
		result_save = "";
		
		
		ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	 		
		  
		   postparameters2send.add(new BasicNameValuePair("idtest",idtest_save));
		   postparameters2send.add(new BasicNameValuePair("idstudent",idestud_save));
		   postparameters2send.add(new BasicNameValuePair("idpaso",idpaso_save));
		   postparameters2send.add(new BasicNameValuePair("aprobado",test_aprob_save));
		   postparameters2send.add(new BasicNameValuePair("intento",intent_save));
		   
		   
		   	    		
		   Log.v("Unity", "Los parametros a enviar en ConsultarCantidadAprobarHttp son: idtest=" + idtest_save + ",idstudent=" + idestud_save + ",idpaso=" + idpaso_save);

		   //realizamos una peticion al servicio y como respuesta obtenes un array JSON
		   Log.v("Unity", "ConsultarCantidadAprobarHttp: La URL de conexion sera: " + url_server + "savetest.php");
		   
	  	JSONArray jdata=post.getserverdata(postparameters2send, url_server + "savetest.php");
	  	   
	  	 Log.v("Unity", "Datos recibidos del servicio en ConsultarCantidadAprobarHttp: " + jdata.toString());
	  	 Log.v("Unity", "Longitud del array: " + jdata.length());
	  	 
	  		
	  		//se verifica si la respuesta del servicio es diferente de null
	  		if (jdata!=null){
	  			//se verifica si la respuesta del servicio es mayor que 0 es decir 1 o mas ofertas
	  			if(jdata.length() > 0){
	  				//variable que almacena el array JSON de las ofertas:
			  		JSONArray json_data;
			  		JSONArray json_status;
			  		JSONObject json_obj;
			  		//contador para hacer un ciclo sobre todas las ofertas:
			  		int cont=0;
			  		try{
			  			Log.d("Unity", "El primer elemento del array es: " + jdata.getString(0));
			  			//el primero elemento del array trae el valor que indica si el test es nuevo o no:
			  			json_obj = jdata.getJSONObject(0);
			  			result_save = json_obj.getString("respuesta");
			  			//validando si el test es nuevo si viene un 1 o si es antiguo si viene un 0
			  			
			  			Log.v("Unity","Finaliza el ciclo de recorrer JSON...");
			  				  			
			  		
			  		}catch(Exception e){
			  			e.printStackTrace();
			  			Log.v("Unity", "Error obteniendo JSON del servicio en ConsultarPreguntasHttp" + e);
			  			result_save="";
			  			
			  		} //cierra catch
			  		
			  	}else { //cuando jdata.length < 0 (esto significa que hubo algun error) entonces:
			  		Log.v("Unity","El array viene vacio!!");
			  		result_save = "";
			  		
			  	}
	  		}  //cierra if jdata!=null	 	
	  		  
	  		return result_save;
	  		
	}//cierra metodo SaveTestHttp
	
	
	/**
	 * Metodo que permite fijar los datos del resultado de guardar el test
	 * 
	 * @param Preguntas[]
	 */
	public void fijarDatosSaveTest(String res){
			
			Log.v("Unity", "fijarDatos: LLama al metodo fijar datos GuardarTest con res= " + res);
			
			if(test_failed){
				
				if(intento_contador == 2){
					Log.d("Unity","El intento es 2 por lo tanto se debe generar un nuevo test.");
					//el test se ha fallado, entonces se notifica al usuario que debe resolver un nuevo test:
					 AlertDialog.Builder build_new_test = new AlertDialog.Builder(SelfAssessmentMain.this);
					 build_new_test.setMessage(R.string.message_new_test_msg);
					 build_new_test.setTitle(R.string.message_new_test_title);
					 build_new_test.setPositiveButton(R.string.message_video_error_ok, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Log.e("Unity", "llamado al metodo OnClick del cuadro de dialogo");
								//se intenta recuperar un nuevo test:
								//new AsyncConsultarDatosPreguntas().execute(numero_preguntas,numero_paso,codigo_estudiante);
								SelfAssessmentMain.this.finish();
								test_enviado_validando = false;
								Log.e("Unity", "Despues de cerrar la actividad!!!");
							}
						} );
			            
			            AlertDialog alert_new_test = build_new_test.create();
			            alert_new_test.show();            
		          
					
					
				 
					
					
				} else { //esto se ejecuta si el hay preguntas con error y no se supera el minimo de respuestas correctas para aprobar el test:
				//notificando al usuario:
				 AlertDialog.Builder build_responses = new AlertDialog.Builder(SelfAssessmentMain.this);
				 	build_responses.setMessage(R.string.message_not_resp_enough_msg);
				 	build_responses.setTitle(R.string.message_not_resp_enough_title);
				 	build_responses.setPositiveButton(R.string.message_video_error_ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Log.e("Unity", "llamado al metodo OnClick del cuadro de dialogo");
								
							Log.e("Unity", "Despues de cerrar la actividad!!!");
						}
					} );
		            
		            AlertDialog alert_error = build_responses.create();
		            alert_error.show();
		            
		          }
				
			} else { //esto se ejecuta si el test fue respondido correctamente y se supera el minimo de respuestas correctas necesarias:
				Log.v("Unity", "El test se ha APROBADO...");
				//dependiendo del tipo de test que se este contestando se notifica a un metodo o al otro:
				if(tipo_test.equalsIgnoreCase("1")){
					//Notificando al AppManager de Unity que se ha aprobado el test. Unicamente se envía el numero de paso para que se registre como aprobado el test de ese paso
					UnityPlayer.UnitySendMessage("AppManager", "ResultSelfAssessment", numero_paso);
				} else if (tipo_test.equalsIgnoreCase("2")){
					UnityPlayer.UnitySendMessage("AppManager", "ResultSelfAssessmEvalMode", numero_paso);
				}
					
				//notificando al usuario:
				 	AlertDialog.Builder build_test_ok = new AlertDialog.Builder(SelfAssessmentMain.this);
				 	build_test_ok.setMessage(R.string.message_success_test_msg);
				 	build_test_ok.setTitle(R.string.message_success_test_title);
				 	build_test_ok.setPositiveButton(R.string.message_video_error_ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Log.e("Unity", "llamado al metodo OnClick del cuadro de dialogo");
							SelfAssessmentMain.this.finish();
							Log.e("Unity", "Despues de cerrar la actividad!!!");
						}
					} );
		            
		            AlertDialog alert_success = build_test_ok.create();
		            alert_success.show();
			}
			
			
		} //cierra metodo fijarDatos
	
	
	public void error_guardando_test(){
		
		 Log.e("Unity", "Error guardando el test en SelfAssessmentMain se va a notificar al usuario...");
		   AlertDialog.Builder builder1 = new AlertDialog.Builder(SelfAssessmentMain.this);
           builder1.setMessage(R.string.message_error_save_test_msg);
           builder1.setTitle(R.string.message_error_save_test_title);
           builder1.setPositiveButton(R.string.message_video_error_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.e("Unity", "llamado al metodo OnClick del cuadro de dialogo");
						
					Log.e("Unity", "Despues de cerrar la actividad!!!");
				}
			} );
           
           AlertDialog alert_error = builder1.create();
           alert_error.show();
	}//cierra error_consultando_cantidad
	
	
	
	
	
	
	/**
	 * Metodo que permite fijar los datos en la lista
	 * 
	 * @param Preguntas[]
	 */
	public void fijarDatos(Pregunta[] preguntas){
			
			Log.v("Unity", "fijarDatos: LLama al metodo fijar datos SelfAssessmentMain");
			if(preguntas[0].enunciado.equalsIgnoreCase("0") && preguntas[0].opciona.equalsIgnoreCase("0") ){
				nombres_preguntas = new ArrayList<String>(0);
				
			} else {
				Log.v("Unity", "fijarDatos:Ingresa al else a cargar listado de preguntas COMPLETO");
				Log.v("Unity", "fijarDatos: Primera pregunta recibida="+preguntas[0].enunciado);
				nombres_preguntas = new ArrayList<String>();
				int cont=0;
				for(int i=0;i<preguntas.length;i++){
					cont = i+1;
					nombres_preguntas.add("Pregunta " + cont);
				}
				preguntas_guardadas = preguntas;
			}
			
			
			//aqui se debe determinar cual es el intento en que estamos:
			intento_contador = Integer.valueOf(preguntas_guardadas[0].intento);
			Log.d("Unity", "El intento en el que estamos actualmente es: " + intento_contador);
			
			//ahora teniendo el dato del intento debemos verificar si todas las preguntas se han respondido
			boolean pregs_responds = true;
			for(int cont=0;cont<preguntas_guardadas.length;cont++){
				 if(!preguntas_guardadas[cont].pregunta_contestada){
					 pregs_responds = false;
					 break;
				 }
				 Log.d("Unity", "Pregunta=" + cont + "Respondida= " + preguntas_guardadas[cont].pregunta_contestada);
			 }//cierra ciclo for
			
			//si todas las preguntas se han respondido y el intento es 1, entonces se fija el test_enviado_validando para 
			//retomar el estado en el que esta el test del estudiante y dejar los iconos que indican cuales pregs estan bien o mal:
			if(pregs_responds && intento_contador==1){
				test_enviado_validando = true;
				//habilitando la variable que permite que se muestren los iconos de bien o mal al lado de las preguntas
				//control_iconos_display = true;
			}
			
			
			//definiendo el nuevo adaptador
			adapter = new MyArrayAdapter<String>(this, R.layout.layout_preguntas_list, R.id.titulo, nombres_preguntas);
			//asignando el adaptador:
			Log.d("Unity","Antes de definir el array adapter!!");
			setListAdapter(adapter);
			
						
		} //cierra metodo fijarDatos
	
	/**
	 * 
	 * Clase que define el adaptador a ser usado en el List. Este adaptador permite modificar la imagen que se muestra y el texto del subtitulo.
	 * 
	 * @author JorgeBacca
	 *
	 * @param <T>
	 */
	 private class MyArrayAdapter<T> extends ArrayAdapter<String>
	    {
	        public MyArrayAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
	            super(context, resource, textViewResourceId, objects);
	        }

	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            View itemView = super.getView(position, convertView, parent);
	            ImageView imageView = (ImageView) itemView.findViewById(R.id.icono);
	            TextView subtitulo = (TextView) itemView.findViewById(R.id.subtitulo);
	            if(preguntas_guardadas[position].pregunta_contestada){
	            	//Log.d("Unity","Se coloca un Tick");
	            	
	            	subtitulo.setText("Pregunta Contestada");
	            } else {
	            	//Log.d("Unity","Se coloca un question");
	            	//imageView.setImageResource(R.drawable.question);
	            	subtitulo.setText("Responder Pregunta");
	            }
	            //if(test_enviado_validando && control_iconos_display){
	            if(test_enviado_validando){	
	            	if(preguntas_guardadas[position].pregunta_correcta){
	            		imageView.setImageResource(R.drawable.tick);
	            	} else {
	            		imageView.setImageResource(R.drawable.error);
	            	}
	            }
	            
	            return itemView;
	        }
	    }
	
	
	
	 @Override 
	 protected void onListItemClick(ListView listView, View view, int position, long id) {
		 super.onListItemClick(listView, view, position, id);
		 
		
		 
		 //Iniciando el activity que permite mostrar los detalles de la oferta guardada
		 Intent detalle = new Intent(getApplicationContext(), DetallePregunta.class);
		 detalle.putExtra("enunciado", preguntas_guardadas[position].enunciado);
		 detalle.putExtra("opciona", preguntas_guardadas[position].opciona);
		 detalle.putExtra("opcionb", preguntas_guardadas[position].opcionb);
		 detalle.putExtra("opcionc", preguntas_guardadas[position].opcionc);
		 detalle.putExtra("opciond", preguntas_guardadas[position].opciond);
		 detalle.putExtra("imagen", preguntas_guardadas[position].imagen_url);
		 
		 detalle.putExtra("url_servidor",url_server);
		 detalle.putExtra("cod_estud", codigo_estudiante);
		 detalle.putExtra("id_paso", numero_paso);
		 detalle.putExtra("id_test", preguntas_guardadas[position].id_test);
		 detalle.putExtra("id_pregunta", preguntas_guardadas[position].id_pregunta);
		 detalle.putExtra("intento",preguntas_guardadas[position].intento);
		 detalle.putExtra("intento_resp", preguntas_guardadas[position].intent_resp_preg);
		 detalle.putExtra("test_enviado_validando", test_enviado_validando);
		 
		 Log.d("Unity","SelfAssMain: onListItemClick: Se va enviar en intento_resp=" + preguntas_guardadas[position].intent_resp_preg);
		 
		 //validando si ya se ha respondido la pregunta se deben cargar los valores de respuesta que ha dado el estudiante anteriormente:
		 Log.d("Unity","Pregunta contestada=" + preguntas_guardadas[position].pregunta_contestada);
		 if(preguntas_guardadas[position].pregunta_contestada){
			 
			 Log.d("Unity","SelfAssessmentMain: La pregunta ya ha sido contestada y la respuesta fue: " + preguntas_guardadas[position].respuesta_del_estud);
			 
			 detalle.putExtra("respuesta_dada", preguntas_guardadas[position].respuesta_del_estud);
			 detalle.putExtra("pregunta_respondida", true);
		 } else {
			 Log.d("Unity","La pregunta aun no ha sido respondida...");
			 detalle.putExtra("pregunta_respondida", false);
		 }
			 
		 
		 //definiendo la posicion del array para poderlo utilizar cuando se retorne la respuesta a la pregunta
		 posicion_array = position;
		 
		 Log.v("Unity", "enunciado="+preguntas_guardadas[position].enunciado);
		 
		 if(!test_enviado_validando){
			 startActivityForResult(detalle, 1);
		 } else {
			 if(!preguntas_guardadas[position].pregunta_correcta){ //en este caso la pregunta esta mal y se le permite al estudiante ingresar a solucionarla
				 Log.d("Unity","La pregunta esta mal y se va a corregir con intento=" + preguntas_guardadas[position].intent_resp_preg);
				 if (preguntas_guardadas[position].intent_resp_preg < 2){
					 
					 detalle.putExtra("intento_resp", preguntas_guardadas[position].intent_resp_preg); //se guarda el nuevo intento
					 startActivityForResult(detalle, 1);
				 } else {
					 Log.d("Unity","Se ha superado el numero de intentos posibles con intento=" + preguntas_guardadas[position].intent_resp_preg);
					 	AlertDialog.Builder builder_notif_intent = new AlertDialog.Builder(SelfAssessmentMain.this);
					 	builder_notif_intent.setMessage(R.string.message_error_max_intent_msg);
					 	builder_notif_intent.setTitle(R.string.message_error_max_intent_title);
					 	builder_notif_intent.setPositiveButton(R.string.message_video_error_ok, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Log.e("Unity", "llamado al metodo OnClick del cuadro de dialogo");
									
								Log.e("Unity", "Despues de cerrar la actividad!!!");
							}
						} );
			            
			            AlertDialog alert_notif_intent = builder_notif_intent.create();
			            alert_notif_intent.show();
				 }
			 } else {
				 AlertDialog.Builder builder_notif_resp = new AlertDialog.Builder(SelfAssessmentMain.this);
				 builder_notif_resp.setMessage(R.string.message_error_resp_correct_msg);
				 builder_notif_resp.setTitle(R.string.message_error_resp_correct_title);
				 builder_notif_resp.setPositiveButton(R.string.message_video_error_ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Log.e("Unity", "llamado al metodo OnClick del cuadro de dialogo");
								
							Log.e("Unity", "Despues de cerrar la actividad!!!");
						}
					} );
		            
		            AlertDialog alert_notif_resp = builder_notif_resp.create();
		            alert_notif_resp.show();
			 }
		 }//cierra else 
		 
	 } // cierra metodo onListItemClick
	 
	 
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     // Check which request we're responding to
	     if (requestCode == 1) {
	         // Make sure the request was successful
	    	 Log.d("Unity", "SelfAssMain: ActivityResult - El result code fue 1");
	         if (resultCode == RESULT_OK && data != null) {
	        	 Log.d("Unity", "SelfAssMain: ActivityResult - El resultado fue OK");
	        	 
	        	 //obteniendo la respuesta dada:
	        	 respuesta_obtenida =  data.getStringExtra("respuesta_dada");
	        	 Log.d("Unity", "La respuesta obtenida fue: " + respuesta_obtenida);
	        	 //verificando si la pregunta se respondio correctamente:
	        	 preguntas_guardadas[posicion_array].ValidarRespuesta(respuesta_obtenida);
	        	 preguntas_guardadas[posicion_array].pregunta_contestada = true;
	        	 if(test_enviado_validando)
	        		 preguntas_guardadas[posicion_array].intent_resp_preg++; //se incrementa el numero del intento
	        	 
	         }else if(resultCode == RESULT_CANCELED){
	        	 Log.d("Unity", "SelfAssMain: ActivityResult - El resultado fue CANCELED y se mantiene la respuesta anterior");
	         } else if (resultCode == 5){
	        	 Log.d("Unity", "SelfAssMain: ActivityResult - El resultado fue codigo 5 es decir respuesta vacia");
	        	 preguntas_guardadas[posicion_array].ValidarRespuesta("");
	        	 
	         } else {
	        	 Log.d("Unity", "SelfAssMain: ActivityResult - El resultado fue otro y se mantiene la respuesta anterior");
	        	 
	         }
	         //Si el test ya ha sido enviado por primera vez entonces despues de responder cada pregunta se inhabilita el icono de bien o mal
	         //para que no se modifique
	         /*
	         if(test_enviado_validando)
	        	 control_iconos_display = false;
	        */
	         
	        //Notificando al adaptador de que se pudo haber producido un cambio en la lista
	        //debido a que posiblemente el estudiante respondió una pregunta
	         adapter.notifyDataSetChanged();
	     } //finaliza if requestCode
	 } //Finaliza metodo onActivityResult
	 
	 /**
	  * Metodo que permite enviar la evaluación completa y almacenarla en el servidor
	  * 
	  * @param view
	  */
	 public void EnviarEvaluacion(View view){
		 
		 //recorriendo todas las preguntas para verificar si todas las preguntas se han respondido:
		 boolean todas_respondidas = true;
		 int contad_pregs_resueltas;
		 for(int cont=0;cont<preguntas_guardadas.length;cont++){
			 if(!preguntas_guardadas[cont].pregunta_contestada){
				 todas_respondidas = false;
				 break;
			 }
			 Log.d("Unity", "Pregunta=" + cont + "Respondida= " + preguntas_guardadas[cont].pregunta_contestada);
		 }//cierra ciclo for
		 
		 if(todas_respondidas){
			 Log.d("Unity", "SelfAssessmentMain: Todas las preguntas han sido respondidas");
			 test_enviado_validando = true;
			 //se habilita la opción para que se muestren los iconos de bien o mal cuando la rpegunta esta bien o mal
			 //porque se va a enviar el test.
			 //control_iconos_display = true;
			 new AsyncGetCantidadPregsAprob().execute(numero_paso);
			 
		 } else {
			 	//notificando al estudiante de que no se han respondido todas las preguntas
			 	AlertDialog.Builder builder_dg = new AlertDialog.Builder(SelfAssessmentMain.this);
			 	builder_dg.setMessage(R.string.message_error_preguntas_resp_msg);
	            builder_dg.setTitle(R.string.message_error_preguntas_resp_title);
	            builder_dg.setPositiveButton(R.string.message_video_error_ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.e("Unity", "llamado al metodo OnClick del cuadro de dialogo");
						
					}
				} );
	            
	            AlertDialog alert_respuestas = builder_dg.create();
	            alert_respuestas.show();
		 }
	 }// cierra metodo EnviarEvaluacion

	 
	
}// cierra clase SelfAssessmentMain
