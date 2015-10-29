package edu.udg.bcds.pintura.arapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.udg.bcds.pintura.dbauxobjs.InterfaceRegistry;
import edu.udg.bcds.pintura.dbauxobjs.Sequence;
import edu.udg.bcds.pintura.library.Httppostlibrary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SaveDatabaseData extends Activity {
	
	public static Httppostlibrary post;
	public static Activity actividad_actual;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save_database_data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.save_database_data, menu);
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
	
	//Metodo que se llama desde Unity para guardar en la BD local del movil
	//una secuencia de ordenamiento de fases del estudiante:
	public static void SavePhasesSequenceToLocalDB(Activity activity, String fecha, String secuencia){
				
		Log.d("Unity","La secuencia DE FASES que se recibe en SaveData es: " + secuencia);
		Log.d("Unity","La fecha de la secuencia es: " + fecha);
		Log.d ("Unity","Se va a obtener la referencia al AdminDBSQLite...");
		AdminDBSQLite db = new AdminDBSQLite(activity.getApplicationContext());
		Log.d ("Unity","Referencia a clase AdminDBSQLite obtenida... se procede a insertar");
		long id_inserted = db.InsertSequenceOfPhasesOrdered(secuencia, fecha);
		Log.d("Unity","Se ha insertado el registro DE FASES en la BD, el id es: " + id_inserted);
		//Log.d("Unity", "Se va a consultar las secuencias");
		//cerrando conexion a la BD:
		db.closeDB();
		
	}//cierra metodo SavePhasesSequenceToLocalDB
	
	
	//Metodo que se llama desde Unity para guardar en la BD local del movil
		//una secuencia de ordenamiento de pasos de la FASE 1
	public static void SaveStepsPhaseOneSequenceToDB(Activity activity, String fecha_fase_uno, String secuencia_fase_uno){
					
			Log.d("Unity","La secuencia DE FASE 1 que se recibe en SaveData es: " + secuencia_fase_uno);
			Log.d("Unity","La fecha de la secuencia es: " + fecha_fase_uno);
			Log.d ("Unity","Se va a obtener la referencia al AdminDBSQLite...");
			AdminDBSQLite db = new AdminDBSQLite(activity.getApplicationContext());
			Log.d ("Unity","Referencia a clase AdminDBSQLite obtenida... se procede a insertar");
			long id_inserted = db.InsertSequenceOfStepsPhaseOne(secuencia_fase_uno, fecha_fase_uno);
			Log.d("Unity","Se ha insertado el registro en la BD FASE 1, el id es: " + id_inserted);
			//cerrando conexion a la BD:
			db.closeDB();
			
	}//cierra metodo SavePhasesSequenceToLocalDB
	
	//Metodo que se llama desde Unity para guardar en la BD local del movil
			//una secuencia de ordenamiento de pasos de la FASE 2
	public static void SaveStepsPhaseTwoSeqToDB(Activity activity, String fecha_fase_dos, String secuencia_fase_dos){
						
				Log.d("Unity","La secuencia DE FASE 2 que se recibe en SaveData es: " + secuencia_fase_dos);
				Log.d("Unity","La fecha de la secuencia es: " + fecha_fase_dos);
				Log.d ("Unity","Se va a obtener la referencia al AdminDBSQLite...");
				AdminDBSQLite db = new AdminDBSQLite(activity.getApplicationContext());
				Log.d ("Unity","Referencia a clase AdminDBSQLite obtenida... se procede a insertar");
				long id_inserted = db.InsertSequenceOfStepsPhaseTwo(secuencia_fase_dos, fecha_fase_dos);
				Log.d("Unity","Se ha insertado el registro en la BD FASE 2, el id es: " + id_inserted);
				//cerrando la conexion a la BD
				db.closeDB();
	}//cierra metodo SavePhasesSequenceToLocalDB
	
	/**
	 * Metodo que permite guardar en la BD del movil los datos del registro de navegacion de interfaces.
	 * Este metodo se llama desde Unity y por este motivo es estatico.
	 * 
	 * @param activity
	 * @param cod_estudiante
	 * @param fecha
	 * @param cod_interfaz
	 * @param step_code
	 * @param marcador_err
	 */
	public static void SaveNavigationMonitoringToDB(Activity activity, String cod_estudiante, String fecha, String cod_interfaz, String step_code, String marcador_err, String tipo_naveg){
		
		 Log.d("Unity","Se van a guardar los siguientes datos de navegacion: codigo_est=" + cod_estudiante + ",fecha=" + fecha + ",interf=" + cod_interfaz + ",step=" + step_code + ",marker=" + marcador_err);
		 Log.d ("Unity","Se va a obtener la referencia al AdminDBSQLite...");
		 AdminDBSQLite db = new AdminDBSQLite(activity.getApplicationContext());
		 Log.d ("Unity","Referencia a clase AdminDBSQLite obtenida... se procede a insertar");
		 long id_inserted = db.InsertRegistryOfNavigationEvent(cod_estudiante, fecha, cod_interfaz, step_code, marcador_err,tipo_naveg);
		 Log.d("Unity","Se ha insertado el registro de NAVEGACION, el id es: " + id_inserted);
		 
		 Log.d("Unity","Se van a consultar los registros de interfaz:");
		 List<InterfaceRegistry> interfaces_reg = db.ConsultarRegistrosNavegacion();
		 for(int i=0;i<interfaces_reg.size();i++){
			 InterfaceRegistry reg = interfaces_reg.get(i);
			 //Log.d("Unity","Registro Navegacion: interfaz:" + reg.codigo_interfaz + ",fecha=" + reg.fecha_evento + ",tipo="+reg.tipo_navegacion);
		 }
		 //cerrando la conexion a la base de datos
		 db.closeDB();
		 //for debugging:
		 //SendNavigationDataToServer(activity,"URL del server");
	}// cierra SaveNavigationStepsToDB
	
	/**
	 * Metodo que permite enviar las secuencias de ordenamiento de las fases y de cualquiera de los pasos
	 * de cualquiera de las 6 fases hacia el servidor y almacenarlas en el servidor.
	 * 
	 * @param activity
	 * @param tipo_secuencia
	 * @param url_servidor
	 * @param cod_estudiante
	 */
	public static void SendSequencesToServer(Activity activity, String tipo_secuencia, String url_servidor, String cod_estudiante){
		Log.d("Unity","Llamado al metodo SendSequencesToServer...");
		Log.d("Unity","Los datos enviados son: tipo_seq:" + tipo_secuencia + ", url: " + url_servidor + ", cod_estud=" + cod_estudiante);
		//creando la referencia a la libreria HTTP
		post = new Httppostlibrary();
		//asignando la referencia de la actividad actual:
		actividad_actual = activity;
		//obteniendo referencia del objeto para controlar la BD:
		AdminDBSQLite db = new AdminDBSQLite(activity.getApplicationContext());
		//creando el objeto Lista para recibir las secuencias que vienen de la BD:
		List<Sequence> sequencias =  new ArrayList<Sequence>();
		String json_secuencia = "";
		if(tipo_secuencia.equalsIgnoreCase("phases")){
			Log.d("Unity", "Se va a consultar el listado de secuencias de FASES...");
			sequencias =  db.ConsultarSecuenciasPhases();
		} else if(tipo_secuencia.equalsIgnoreCase("phase1")){
			Log.d("Unity", "Se va a consultar el listado de secuencias de FASE 1...");
			sequencias =  db.ConsultarSecuenciasPhaseOne();
		} else if (tipo_secuencia.equalsIgnoreCase("phase2")){
			Log.d("Unity", "Se va a consultar el listado de secuencias de FASE 2...");
			sequencias =  db.ConsultarSecuenciasPhaseTwo();
		} else if(tipo_secuencia.equalsIgnoreCase("phase3")){
			Log.d("Unity", "Se va a consultar el listado de secuencias de FASE 3...");
			sequencias =  db.ConsultarSecuenciasPhases();//a futuro esto se debe cambiar por la consulta a la tabla correspondiente:
		} else if (tipo_secuencia.equalsIgnoreCase("phase4")){
			Log.d("Unity", "Se va a consultar el listado de secuencias de FASE 4...");
			sequencias =  db.ConsultarSecuenciasPhases();//a futuro esto se debe cambiar por la consulta a la tabla correspondiente:
		} else if(tipo_secuencia.equalsIgnoreCase("phase5")){
			Log.d("Unity", "Se va a consultar el listado de secuencias de FASE 5...");
			sequencias =  db.ConsultarSecuenciasPhases();//a futuro esto se debe cambiar por la consulta a la tabla correspondiente:
		} else if (tipo_secuencia.equalsIgnoreCase("phase6")){
			Log.d("Unity", "Se va a consultar el listado de secuencias de FASE 6...");
			sequencias =  db.ConsultarSecuenciasPhases();//a futuro esto se debe cambiar por la consulta a la tabla correspondiente:
		} else {
			sequencias =  new ArrayList<Sequence>(); //si no es ninguno de los otros casos entonces se crea un array vacio
		}
			//validando si hay secuencias para enviar al servidor:
			if(sequencias.size() > 0){
				JSONArray secuences_array = new JSONArray();
				//JSONObject secuencia_individual = new JSONObject(); //objeto JSON que almacena un registro de secuencia
				JSONObject secuencia_individual; //objeto JSON que almacena un registro de secuencia
				Sequence secuencia; //objeto del tipo Sequence que almacena los datos de una secuencia
				
				try{
					for(int cont=0;cont<sequencias.size();cont++){
						secuencia = sequencias.get(cont);
						Log.d("Unity","Iteracion: " + cont + ", elemento_secuencia=" + secuencia.sequence + ",fecha=" + secuencia.date_sequence);
						secuencia_individual = new JSONObject();
						secuencia_individual.accumulate("secuencia", secuencia.sequence);
						secuencia_individual.accumulate("fecha", secuencia.date_sequence);
						//secuencia_individual.put("secuencia",secuencia.sequence);
						//secuencia_individual.put("fecha", secuencia.date_sequence);
						secuences_array.put(secuencia_individual);
						
					}//cierra ciclo for
					Log.d("Unity","El JSON creado al final del ciclo es: " + secuences_array.toString());
					json_secuencia = secuences_array.toString();
				}catch (Exception e){
					Log.d("Unity","SendSequencesToServer: Error creando el JSON:  " + e);
					e.printStackTrace();
				}//cierra catch
				
				//cerrando la conexion a la BD:
				db.closeDB();
				//se va a hacer el envio hacia el servidor en un metodo asincronico:
				new AsyncSaveSequencesToServ().execute(json_secuencia,tipo_secuencia,url_servidor,cod_estudiante);
				
		} else { //si secuencias.size es menor o igual que 0 quiere decir que no hay secuencias para enviar al servidor
			Log.d("Unity","SaveDatabaseData: NO hay secuencias para enviar al servidor... se espera a la proxima sincronizacion");
		} //cierra sequencias.size() > 0
		
	}//cierra SendSequencesToServer
	
	
	
	public static void SendNavigationDataToServer(Activity activity, String url_servidor){
		Log.d("Unity","Llamado al metodo SendNavigationDataToServer...");
		Log.d("Unity","Los datos enviados son: url_servidor:" + url_servidor);
		//creando la referencia a la libreria HTTP
		post = new Httppostlibrary();
		//asignando la referencia de la actividad actual:
		actividad_actual = activity;
		//obteniendo referencia del objeto para controlar la BD:
		AdminDBSQLite db = new AdminDBSQLite(activity.getApplicationContext());
		//creando el objeto Lista para recibir las secuencias que vienen de la BD:
		List<InterfaceRegistry> navegaciones =  new ArrayList<InterfaceRegistry>();
		String json_navegacion = "";
		
		Log.d("Unity", "Se va a consultar el listado de registros de navegacion");
		navegaciones =  db.ConsultarRegistrosNavegacion();
		
			//validando si hay secuencias para enviar al servidor:
			if(navegaciones.size() > 0){
				JSONArray navegacion_array = new JSONArray();
				//JSONObject secuencia_individual = new JSONObject(); //objeto JSON que almacena un registro de secuencia
				JSONObject navegacion_individual; //objeto JSON que almacena un registro de secuencia
				InterfaceRegistry navegacion; //objeto del tipo Sequence que almacena los datos de una secuencia
				
				try{
					for(int cont=0;cont<navegaciones.size();cont++){
						navegacion = navegaciones.get(cont);
						//Log.d("Unity","Iteracion: " + cont + ", Id_Interfaz=" + navegacion.codigo_interfaz + ",fecha=" + navegacion.fecha_evento);
						navegacion_individual = new JSONObject();
						navegacion_individual.accumulate("id_registro", navegacion.id_registro);
						navegacion_individual.accumulate("id_estudiante", navegacion.id_estudiante);
						navegacion_individual.accumulate("fecha", navegacion.fecha_evento);
						navegacion_individual.accumulate("interfaz", navegacion.codigo_interfaz);
						navegacion_individual.accumulate("paso", navegacion.codigo_paso);
						navegacion_individual.accumulate("marker", navegacion.marcador_error);
						navegacion_individual.accumulate("tipo", navegacion.tipo_navegacion);
												
						navegacion_array.put(navegacion_individual);
						
					}//cierra ciclo for
					Log.d("Unity","SendNavigationDataToServer: El JSON creado al final del ciclo es: " + navegacion_array.toString());
					json_navegacion = navegacion_array.toString();
				}catch (Exception e){
					Log.d("Unity","SendNavigationDataToServer: Error creando el JSON:  " + e);
					e.printStackTrace();
				}//cierra catch
				
				//cierra la conexion a la bD:
				db.closeDB();
				//se va a hacer el envio hacia el servidor en un metodo asincronico:
				new AsyncSendNavigationToServer().execute(json_navegacion,url_servidor);
				
		} else { //si secuencias.size es menor o igual que 0 quiere decir que no hay secuencias para enviar al servidor
			Log.d("Unity","SendNavigationDataToServer: NO hay registros de navegacion para enviar al servidor... se espera a la proxima sincronizacion");
		} //cierra sequencias.size() > 0
		
	}//cierra SendSequencesToServer
	
	
	
	
	
	/**
	 * Clase para sincronizar datos de la secuencia de fases o pasos hacia el servidor 
	 * 
	 * @author Jorge Bacca
	 *
	 */
	static class AsyncSaveSequencesToServ extends AsyncTask< String, String, String > {
		 
		String json_sequence_data;
		String type_secuence;
		String url_server;
		String student_code;
		boolean respuesta_servicio;
		
	    protected void onPreExecute() {
	    	//para el progress dialog
	       Log.d("Unity","Se va a ejecutar el metodo asincrono para enviar al servidor las secuencias");
	    }

		protected String doInBackground(String... params) {
			//obtnemos usr y pass
			json_sequence_data = params[0];
			type_secuence = params[1];
			url_server = params[2];
			student_code = params[3];
			
			Log.d("Unity","Parametros recibidos en el doInBackground.... json_secuencia=" + json_sequence_data + ", type_secuence=" + type_secuence + ", url:" + url_server + ", cod=" + student_code);
			
			
			respuesta_servicio = SaveDataHttpToServ(json_sequence_data,type_secuence,url_server,student_code);
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

	       Log.d("Unity","OnPostExecute= "+result);
	       
	       if (result.equalsIgnoreCase("ok")){
	    	   Log.v("Unity", "Resultado OK...");
	    	   fijarDatosResultado(true,type_secuence);
	       }else if (result.equalsIgnoreCase("err")){
	    	   fijarDatosResultado(false,type_secuence);
	       }else {
	    	   fijarDatosResultado(false,type_secuence);
	        	//Pregunta[] pregunta_vacio = new Pregunta[1];
	        	//pregunta_vacio[0]=new Pregunta("0", "0", "0", "0", "0", "0", "0",true,"","");
	        	//fijarDatos(pregunta_vacio);
	        }
	        
	    }
		
	}//cierra clase async
	
	
	/**
	 * Metodo que permite indicar si se debe limpiar la base de datos porque los resultados
	 * se enviaron satisfactoriamente al servidor.
	 * 
	 * @param Preguntas[]
	 */
	public static void fijarDatosResultado(boolean result, String tipo){
		
		if(result){
			Log.d("Unity", "Datos sincronizados con el servidor, se procede a eliminarlos de la BD local para tipo=" + tipo);
			AdminDBSQLite db_eliminar = new AdminDBSQLite(actividad_actual.getApplicationContext());
			if(tipo.equalsIgnoreCase("phases"))
				db_eliminar.DeleteSequencesOfPhases();
			else if(tipo.equalsIgnoreCase("phase1"))
				db_eliminar.DeleteSequencesOfPhaseOne();
			else if(tipo.equalsIgnoreCase("phase2"))
				db_eliminar.DeleteSequencesOfPhaseTwo();
		}else {
			Log.d("Unity","No se pueden eliminar los datos de la BD local porque hubo errores en tipo=" + tipo);
		}
			
		
	} //cierra metodo fijarDatos
	
	
	
	/**
	 * 
	 * @param num_preguntas
	 * @param paso_preg
	 * @return Pregunta[]
	 * 
	 * @see static class
	 */
	public static boolean SaveDataHttpToServ(String json_secuencia, String tipo, String url_serv,String codigo_est ) {
		
		String resultado_peticion;
		//esta variable almacena el resultado de la peticion HTTP y es lo que retorna el servicio:
		boolean resultado_insertar = false;
		
		
		ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	 		
		   postparameters2send.add(new BasicNameValuePair("json",json_secuencia));
		   postparameters2send.add(new BasicNameValuePair("tipo",tipo));
		   postparameters2send.add(new BasicNameValuePair("estudiante",codigo_est));
		  		   
		   		
		   Log.v("Unity", "Los parametros a enviar son: json_secuencia=" + json_secuencia + ", tipo=" + tipo + ",cod_est="  + codigo_est);

		   //realizamos una peticion al servicio y como respuesta obtenes un array JSON
		   Log.v("Unity", "La URL de conexion sera: " + url_serv + "savesequence.php");
		   
	  	JSONArray jdata=post.getserverdata(postparameters2send, url_serv + "savesequence.php");
	  	   
	  	 Log.v("Unity", "Datos recibidos del servicio: " + jdata.toString());
	  	 Log.v("Unity", "Longitud del array: " + jdata.length());
	  	 
	  		
	  		//se verifica si la respuesta del servicio es diferente de null
	  		if (jdata!=null){
	  			//se verifica si la respuesta del servicio es mayor que 0 es decir 1 o mas ofertas
	  			if(jdata.length() > 0){
	  				//variable que almacena el array JSON de las ofertas:
			  		Log.d("Unity", "Ingresa con jdata.lenght()>0 es decir que el servicio ha respondido");
			  		JSONObject json_obj;
			  		//contador para hacer un ciclo sobre todas las ofertas:
			  		
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
	 * Clase para sincronizar datos del registro de navegacion de interfaces hacia el servidor: 
	 * 
	 * @author Jorge Bacca
	 *
	 */
	static class AsyncSendNavigationToServer extends AsyncTask< String, String, String > {
		 
		String json_sequence_data;
		String url_server;
		boolean respuesta_servicio;
		
	    protected void onPreExecute() {
	    	//para el progress dialog
	       Log.d("Unity","Se va a ejecutar el metodo asincrono para enviar al servidor las secuencias");
	    }

		protected String doInBackground(String... params) {
			//obtnemos usr y pass
			json_sequence_data = params[0];
			url_server = params[1];
			
			
			//Log.d("Unity","Parametros recibidos en el doInBackground.... json_secuencia=" + json_sequence_data + ",url:" + url_server);
			
			
			respuesta_servicio = SaveNavigationHttpToServ(json_sequence_data,url_server);
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

	       Log.d("Unity","OnPostExecute= "+result);
	       
	       if (result.equalsIgnoreCase("ok")){
	    	   Log.v("Unity", "Resultado OK...");
	    	   fijarDatosResNavegacion(true);
	       }else if (result.equalsIgnoreCase("err")){
	    	   fijarDatosResNavegacion(false);
	       }else {
	    	   fijarDatosResNavegacion(false);
	        	//Pregunta[] pregunta_vacio = new Pregunta[1];
	        	//pregunta_vacio[0]=new Pregunta("0", "0", "0", "0", "0", "0", "0",true,"","");
	        	//fijarDatos(pregunta_vacio);
	        }
	        
	    }
		
	}//cierra clase async
	
	/**
	 * Metodo que permite indicar si se debe limpiar la base de datos porque los resultados
	 * se enviaron satisfactoriamente al servidor.
	 * 
	 * @param Preguntas[]
	 */
	public static void fijarDatosResNavegacion(boolean result){
		
		if(result){
			Log.d("Unity", "Datos sincronizados con el servidor, se procede a eliminarlos de la BD local para NAVEGACION");
			AdminDBSQLite db_eliminar = new AdminDBSQLite(actividad_actual.getApplicationContext());
			db_eliminar.DeleteNavigationRegistry();
			
		}else {
			Log.d("Unity","No se pueden eliminar los datos DE NAVEGACION de la BD local porque hubo errores");
		}
			
		
	} //cierra metodo fijarDatos
	
	
public static boolean SaveNavigationHttpToServ(String json_secuencia, String url_serv ) {
		
		String resultado_peticion;
		//esta variable almacena el resultado de la peticion HTTP y es lo que retorna el servicio:
		boolean resultado_insertar = false;
		
		
		ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
	 		
		   postparameters2send.add(new BasicNameValuePair("json",json_secuencia));
		   	   		
		   //Log.v("Unity", "Los parametros a enviar son: json_navegacion=" + json_secuencia);

		   //realizamos una peticion al servicio y como respuesta obtenes un array JSON
		   Log.v("Unity", "La URL de conexion sera: " + url_serv + "savenavigation.php");
		   
	  	JSONArray jdata=post.getserverdata(postparameters2send, url_serv + "savenavigation.php");
	  	   
	  	 Log.v("Unity", "Datos recibidos del servicio: " + jdata.toString());
	  	 Log.v("Unity", "Longitud del array: " + jdata.length());
	  	 
	  		
	  		//se verifica si la respuesta del servicio es diferente de null
	  		if (jdata!=null){
	  			//se verifica si la respuesta del servicio es mayor que 0 es decir 1 o mas ofertas
	  			if(jdata.length() > 0){
	  				//variable que almacena el array JSON de las ofertas:
			  		Log.d("Unity", "Ingresa con jdata.lenght()>0 es decir que el servicio ha respondido");
			  		JSONObject json_obj;
			  		//contador para hacer un ciclo sobre todas las ofertas:
			  		
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
	


}
