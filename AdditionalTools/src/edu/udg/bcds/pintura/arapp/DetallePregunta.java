package edu.udg.bcds.pintura.arapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.udg.bcds.pintura.library.Httppostlibrary;
import edu.udg.bcds.pintura.tools.SelfAssessment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class DetallePregunta extends Activity {
	
	TextView enunciado_pregunta;
	RadioButton radio_opciona;
	RadioButton radio_opcionb;
	RadioButton radio_opcionc;
	RadioButton radio_opciond;
		
	Button btn_ver_imagen;
	Button guardar_resp;
	
	String respuesta_seleccionada;
	String imag_to_show;
	
	boolean pregunta_respondida;
	String respuesta_selec_on_load;
	String id_student;
	String id_paso_ext;
	String id_test_ext;
	String id_pregunt;
	String intento_preg;
	String intento_resp;
	
	//variables para medir el tiempo que tarda el estudiante en responder la pregunta:
	private long startTime;
	private long time_to_answ;
	private long time_in_secs;
	
	Httppostlibrary post;
	private ProgressDialog pDialog;
	String url_server;
	
	boolean result_insert_question;
	
	boolean test_enviado_validando;
	
	public List<OpcionRespuesta> opciones_resp;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalle_pregunta);
		
		Log.v("Unity","Llamado al create de DetallePregunta...");
		
		Bundle extras = getIntent().getExtras();
		
		Log.v("Unity","GetDataExtras: enunciado=" + extras.getString("enunciado"));
		
		enunciado_pregunta = (TextView) findViewById(R.id.enunciado_preg);
		enunciado_pregunta.setText(extras.getString("enunciado"));
		
		//creando los objetos que tienen cada opcion de respuesta para generar el orden aleatorio:
		OpcionRespuesta opcion_a = new OpcionRespuesta(extras.getString("opciona"), "a");
		OpcionRespuesta opcion_b = new OpcionRespuesta(extras.getString("opcionb"), "b");
		OpcionRespuesta opcion_c = new OpcionRespuesta(extras.getString("opcionc"), "c");
		OpcionRespuesta opcion_d = new OpcionRespuesta(extras.getString("opciond"), "d");
		
		Log.d("Unity","Se va a crear la lista de opciones de respuesta...");
		//Creando la lista
		opciones_resp = new ArrayList<OpcionRespuesta>();
		opciones_resp.add(opcion_a);
		opciones_resp.add(opcion_b);
		opciones_resp.add(opcion_c);
		opciones_resp.add(opcion_d);
		//generando el orden aleatorio:
		Log.d("Unity","Se va a generar el orden aleatorio...");
		Collections.shuffle(opciones_resp);
		
		//se va a asignar a cada radiobutton cada elemento de la lista:		
		Log.v("Unity","GetExtras opciona=" + opciones_resp.get(0).opcion_respuesta);
		
		radio_opciona = (RadioButton) findViewById(R.id.radio_opciona);
		radio_opciona.setText(opciones_resp.get(0).opcion_respuesta);
		
		Log.v("Unity","GetExtras opcionb=" + opciones_resp.get(1).opcion_respuesta);
		
		radio_opcionb = (RadioButton) findViewById(R.id.radio_opcionb);
		radio_opcionb.setText(opciones_resp.get(1).opcion_respuesta);
		
		Log.v("Unity","GetExtras opcionc=" + opciones_resp.get(2).opcion_respuesta);
		
		radio_opcionc = (RadioButton) findViewById(R.id.radio_opcionc);
		radio_opcionc.setText(opciones_resp.get(2).opcion_respuesta);
		
		Log.v("Unity","GetExtras opciond=" + opciones_resp.get(3).opcion_respuesta);
		
		radio_opciond = (RadioButton) findViewById(R.id.radio_opciond);
		radio_opciond.setText(opciones_resp.get(3).opcion_respuesta);
		
		Log.v("Unity","GetExtras Imagen = " + extras.getString("imagen"));
		
		btn_ver_imagen = (Button) findViewById(R.id.btn_ver_img);
		
		Log.v("Unity","Antes de intentar obtener imagen = ");
		String imag_value =  extras.getString("imagen");
		Log.v("Unity","Valor Imagen = " + imag_value);
		
		if(imag_value != null){
			if(imag_value.equalsIgnoreCase(".")){
				btn_ver_imagen.setEnabled(false); //inhabilitando el btn para que no se muestre
				Log.d("Unity","Se va a ocultar el BTN porque no hay IMAGEN...");
			}else {
				imag_to_show = imag_value;
			}
		} else {
			btn_ver_imagen.setEnabled(false); //inhabilitando el btn para que no se muestre
		}
		
		//verificando si la pregunta ya se ha respondido antes:
		pregunta_respondida = extras.getBoolean("pregunta_respondida");
		//habilitando la opcion correspondiente
		if(pregunta_respondida){
			respuesta_selec_on_load = extras.getString("respuesta_dada");
			if(respuesta_selec_on_load.equalsIgnoreCase(opciones_resp.get(0).letter)){
				radio_opciona.setChecked(true);
				respuesta_seleccionada = opciones_resp.get(0).letter;
			} else if (respuesta_selec_on_load.equalsIgnoreCase(opciones_resp.get(1).letter)){
				radio_opcionb.setChecked(true);
				respuesta_seleccionada = opciones_resp.get(1).letter;
			} else if (respuesta_selec_on_load.equalsIgnoreCase(opciones_resp.get(2).letter)){
				radio_opcionc.setChecked(true);
				respuesta_seleccionada = opciones_resp.get(2).letter;
			} else{ 
				radio_opciond.setChecked(true);
				respuesta_seleccionada = opciones_resp.get(3).letter;
				}
		}//cierra if pregunta_respondida
			
		//inicializando la libreria para hacer la peticion HTTP cuando se guarde la pregunta:
		post = new Httppostlibrary();
		//recuperando la URL del servidor:
		url_server = extras.getString("url_servidor");
		id_student = extras.getString("cod_estud");
		id_paso_ext = extras.getString("id_paso");
		id_test_ext = extras.getString("id_test");
		id_pregunt = extras.getString("id_pregunta");
		intento_preg = extras.getString("intento");
		intento_resp = Integer.toString(extras.getInt("intento_resp"));
		test_enviado_validando = extras.getBoolean("test_enviado_validando");
		Log.d("Unity","DetallePregunta: OnCreate - Se reciben el parametro intento_resp=" + intento_resp);
		//guardando el tiempo de inicio:		
		startTime = System.nanoTime();
		
		if(test_enviado_validando){
			guardar_resp = (Button)findViewById(R.id.btn_guardar_resp);
			if(intento_resp.equalsIgnoreCase("0"))
				guardar_resp.setText("Enviar Primer Intento");
			else guardar_resp.setText("Enviar Ultimo Intento");
		}
		
			
	} //cierra metodo OnCreate
	
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_opciona:
	            if (checked)
	                respuesta_seleccionada = opciones_resp.get(0).letter;
	            break;
	        case R.id.radio_opcionb:
	            if (checked)
	            	respuesta_seleccionada = opciones_resp.get(1).letter;
	            break;
	        case R.id.radio_opcionc:
	            if (checked)
	            	respuesta_seleccionada = opciones_resp.get(2).letter;
	            break;
	        case R.id.radio_opciond:
	            if (checked)
	            	respuesta_seleccionada = opciones_resp.get(3).letter;
	            break;
	    }
	    Log.d("Unity","Se ha seleccionado la opcion de respuesta: " + respuesta_seleccionada);
	    
	} //cierra onRadioButtonClicked
	
	
	/**
	 * Metodo que se dispara al hacer click sobre el boton ver imagen
	 * para mostrar la imagen si es necesario.
	 * 
	 * @param view
	 */
	public void VerImgenAction(View view){
		Log.d("Unity","Se va a mostrar la imagen...");
		Intent intent_view_image = new Intent(this, SelfAssessment.class);
		intent_view_image.putExtra("img_url", imag_to_show);
		intent_view_image.putExtra("url_server", url_server);
		startActivity(intent_view_image);
	}
	
	
	/**
	 * Metodo que se llama cuando se hace click sobre el boton guardar respuesta
	 * 
	 * Este metodo valida si la respuesta fue correcta y cierra la actividad devolviendo un valor
	 * a la actividad SelfAssessmentMain
	 * 
	 * @param view
	 */
	public void GuardarRespuesta(View view){
		
		time_to_answ = System.nanoTime() - startTime;
		Log.d("Unity","Tiempo en responder (ns): " + time_to_answ);
		time_in_secs = time_to_answ / 1000000000;
		Log.d("Unity","Tiempo en secs: " + time_in_secs);		
		String time_str = String.valueOf(time_in_secs);
		
		if(test_enviado_validando){
			int intento_incr = Integer.parseInt(intento_resp);
			intento_incr++;
			intento_resp = String.valueOf(intento_incr);
		}
		
		new AsyncGuardarRespuestaPreg().execute(id_student,id_test_ext,id_paso_ext,id_pregunt,respuesta_seleccionada,"0",time_str,intento_preg,intento_resp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detalle_pregunta, menu);
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
	
	
	/**
	 * Clase para insertar la respuesta a la pregunta
	 * 
	 * @author Jorge Bacca
	 *
	 */
	
	class AsyncGuardarRespuestaPreg extends AsyncTask< String, String, String > {
		 
		String id_estudia;
		String id_test;
		String id_paso;
		String id_preg;
		String resp;
		String resp_def;
		String tiempo_res;
		String intento_pregunta;
		String intento_resp;
		boolean respuesta_servicio;
	    protected void onPreExecute() {
	    	//para el progress dialog
	        pDialog = new ProgressDialog(DetallePregunta.this);
	        pDialog.setMessage("Guardando Pregunta");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();
	    }

		protected String doInBackground(String... params) {
			//obtnemos usr y pass
			id_estudia = params[0];
			id_test = params[1];
			id_paso = params[2];
			id_preg = params[3];
			resp = params[4];
			resp_def = params[5];
			tiempo_res = params[6];
			intento_pregunta = params[7];
			intento_resp = params[8];
			Log.d("Unity","Parametros recibidos en el doInBackground.... id_estudia=" + id_estudia + ", id_test=" + id_test + ", id_paso=" + id_paso + " resp=" + resp + " intento_resp=" + intento_resp);
			
			
			respuesta_servicio = InsertarRespuestaHttp(id_estudia,id_test, id_paso,id_preg,resp,resp_def,tiempo_res,intento_pregunta,intento_resp);
			Log.d("Unity","Datos recibidos del JSON: Resultado servicio: "+respuesta_servicio);
			
			//validando si del servicio se obtuvieron datos correctos:
			if(!respuesta_servicio){
				Log.d("Unity","doInBackground - NO se obtuvieron datos del servicio o el array viene vacio...");
				return "err";
			}else {
				return "ok";
			}
			
				    	
		}
	   
		
	    protected void onPostExecute(String result) {

	       pDialog.dismiss();//ocultamos progess dialog.
	       Log.d("Unity","OnPostExecute= "+result);
	       
	       if (result.equalsIgnoreCase("ok")){
	    	   Log.v("Unity", "Resultado OK...");
	    	   fijarDatos(true);
	       }else if (result.equalsIgnoreCase("err")){
	    	   fijarDatos(false);
	       }else {
	        	err_consultando(true);
	        	//Pregunta[] pregunta_vacio = new Pregunta[1];
	        	//pregunta_vacio[0]=new Pregunta("0", "0", "0", "0", "0", "0", "0",true,"","");
	        	//fijarDatos(pregunta_vacio);
	        }
	        
	    }
		
	}//cierra clase async
	
	
	/**
	 * 
	 * @param num_preguntas
	 * @param paso_preg
	 * @return Pregunta[]
	 * 
	 * @
	 */
	public boolean InsertarRespuestaHttp(String id_estud, String idtest, String id_paso, String id_preg, String resp, String resp_def, String tiempo_res, String intento_p, String intento_res ) {
		
		int status=-1;
		String resultado_peticion;
		String preg_contestada_tmp;
		//esta variable almacena el resultado de la peticion HTTP:
		boolean resultado_insertar = false;
		
		
		ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	 		
		   postparameters2send.add(new BasicNameValuePair("id_estudiante",id_estud));
		   postparameters2send.add(new BasicNameValuePair("id_test",idtest));
		   postparameters2send.add(new BasicNameValuePair("id_paso",id_paso));
		   postparameters2send.add(new BasicNameValuePair("id_pregunta",id_preg));
		   postparameters2send.add(new BasicNameValuePair("respuesta",resp));
		   postparameters2send.add(new BasicNameValuePair("resp_definitiva",resp_def));
		   postparameters2send.add(new BasicNameValuePair("tiempo_respuesta",tiempo_res));
		   postparameters2send.add(new BasicNameValuePair("intento",intento_p));
		   postparameters2send.add(new BasicNameValuePair("intento_resp",intento_res));
		   		
		   Log.v("Unity", "Los parametros a enviar son: id_estudiante=" + id_estud + ", id_test=" + idtest + ",id_paso="+id_paso+",id_preg="+id_preg+",resp="+resp+",resp_def="+resp_def+",time="+tiempo_res + ",intento_res=" + intento_res);

		   //realizamos una peticion al servicio y como respuesta obtenes un array JSON
		   Log.v("Unity", "La URL de conexion sera: " + url_server + "saveanswer.php");
		   
	  	JSONArray jdata=post.getserverdata(postparameters2send, url_server + "saveanswer.php");
	  	   
	  	 Log.v("Unity", "Datos recibidos del servicio: " + jdata.toString());
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
			  			resultado_peticion = json_obj.getString("respuesta");
			  			//validando si el test es nuevo si viene un 1 o si es antiguo si viene un 0
			  			if(resultado_peticion.equalsIgnoreCase("0")){
			  				resultado_insertar = false;
			  			}else resultado_insertar = true;
			  			
			  						  			
			  			Log.v("Unity","Finaliza el ciclo de recorrer JSON...");
			  			  				  			
			  			
			  		}catch(Exception e){
			  			e.printStackTrace();
			  			Log.v("Unity", "DetallePregunta: Error obteniendo JSON del servicio en InsertarRespuestaHttp" + e);
			  			resultado_insertar = false;
			  		} //cierra catch
			  		
			  	}else { //cuando jdata.length < 0 (esto significa que hubo algun error) entonces:
			  		Log.v("Unity","DetallePregunta: El array viene vacio!!");
			  		resultado_insertar = false;
			  	}
	  		}  //cierra if jdata!=null	 	
	  		  
	  		return resultado_insertar;
	  		
	}//cierra metodo InsertarRespuesta
	
	/**
	 * Metodo que permite fijar los datos en la lista
	 * 
	 * @param Preguntas[]
	 */
	public void fijarDatos(boolean result){
		
		if (respuesta_seleccionada != null){
			if(respuesta_seleccionada.equalsIgnoreCase("")){
				Log.d("Unity", "DetallePregunta: No se ha seleccionado ninguna opcion");
				Log.d("Unity","Se va a llamar al servicio para almacenar la respuesta...");
				if(!result)
					err_consultando(true);
				else {
					setResult(5);
					finish();
				}
				
			}else {
				if(!result)
					err_consultando(false);
				else {
					Intent resultado = new Intent();
					resultado.putExtra("respuesta_dada", respuesta_seleccionada);
					setResult(RESULT_OK, resultado);
					finish();
				}
						
			}
		} else {
			setResult(5);
			finish();
		}
		
		result_insert_question = result;
		
	} //cierra metodo fijarDatos
	
	
	/**
	 * Metodo que permite notificar de errores por medio de un cuadro de dialogo
	 * pueden haber dos modos:
	 * En el primero simplemente se retorna un parametro RESULT_CANCELED y se finaliza la actividad
	 * En el segundo modo se retorna el parametro RESULT_OK y se retorna la respuesta que ha dado el estudiante
	 */
	public void err_consultando(boolean mode){
		
		 Log.e("Unity", "Error INSERTANDO respuesta a pregunta en DetallePregunta se va a notificar al usuario");
		 
		 if(mode){
		    AlertDialog.Builder builder1 = new AlertDialog.Builder(DetallePregunta.this);
            builder1.setMessage(R.string.message_insert_question_err_msg);
            builder1.setTitle(R.string.message_insert_question_err_title);
            builder1.setPositiveButton(R.string.message_video_error_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.e("Unity", "llamado al metodo OnClick del cuadro de dialogo");
					
					setResult(RESULT_CANCELED);
					finish();
					
					Log.e("Unity", "Despues de cerrar la actividad!!!");
				}
			} );
            
            AlertDialog alert_error = builder1.create();
            alert_error.show();
		 }else {
			 AlertDialog.Builder builder1 = new AlertDialog.Builder(DetallePregunta.this);
	            builder1.setMessage(R.string.message_insert_question_err_msg);
	            builder1.setTitle(R.string.message_insert_question_err_title);
	            builder1.setPositiveButton(R.string.message_video_error_ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.e("Unity", "llamado al metodo OnClick del cuadro de dialogo");
						//creando un Intent para retornar la respuesta del estudiante:
						Intent resultado = new Intent();
						resultado.putExtra("respuesta_dada", respuesta_seleccionada);
						setResult(RESULT_OK, resultado);
						DetallePregunta.this.finish();
						
						Log.e("Unity", "Despues de cerrar la actividad!!!");
					}
				} );
	            
	            AlertDialog alert_error = builder1.create();
	            alert_error.show();
		 }
	}
	
}
