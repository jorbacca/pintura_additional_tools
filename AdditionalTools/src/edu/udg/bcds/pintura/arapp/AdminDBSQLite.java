package edu.udg.bcds.pintura.arapp;

import java.util.ArrayList;
import java.util.List;
import edu.udg.bcds.pintura.dbauxobjs.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AdminDBSQLite extends SQLiteOpenHelper {

	public AdminDBSQLite(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}
	
	private static final String DATABASE_NAME="pintura_db";
	private static final int VERSION = 6; //OJO siempre que se modifique algo aqui tengo que incrementar la version sobre todo si se crean tablas.
	
	//nombre de la tabla que almacena las secuencias de ordenamiento de las fases del proceso:
	private static final String SEQUENCE_PHASES_TABLE_NAME = "secuencias_phases";
	//nombre de la tabla que almacena las secuencias de ordenamiento de los pasos de la FASE 1:
	private static final String SEQUENCE_STEPS_PHASE_ONE_TBL_NAME = "secuencias_steps_phase_one";
	//nombre de la tabla que almacena las secuencias de ordenamiento de los pasos de la FASE 1:
	private static final String SEQUENCE_STEPS_PHASE_TWO_TBL_NAME = "secuencias_steps_phase_two";
	
	//nombre de la tabla que almacena la navegacion sobre las interfaces de la app:
	private static final String NAVIGATION_MONITORING_TBL_NAME = "navigation_monitoring";
	
		
	//nombres de los campos de la tabla secuencias_phases:
	private static final String ID_SEQUENCE_FIELD = "id_secuencia";
	private static final String FECHA_SECUENCIA = "fecha";
	private static final String SEQUENCE_FIELD = "secuencia";
	
	//NOMBRES DE los campos de la tabla secuencias de pasos de la FASE 1:
	private static final String ID_SEQ_STEPS_PHASE_ONE = "id_seq_steps_one";
	private static final String FECHA_SEQ_STEPS_PHASE_ONE = "fecha_pasos_phase_one";
	private static final String SEQUENCE_STEPS_PHASE_ONE = "sequence_phase_one";
	
	//NOMBRES DE los campos de la tabla secuencias de pasos de la FASE 2:
		private static final String ID_SEQ_STEPS_PHASE_TWO = "id_seq_steps_two";
		private static final String FECHA_SEQ_STEPS_PHASE_TWO = "fecha_pasos_phase_two";
		private static final String SEQUENCE_STEPS_PHASE_TWO = "sequence_phase_two";
		
	//NOMBRES DE los campos de la tabla navigation_monitoring
	private static final String ID_EVENT_INTERFACE = "id_registry";
	private static final String ID_STUDENT = "id_student";
	private static final String DATE_REGISTRY = "date_registry";
	private static final String INTERFACE_CODE = "interface_code";
	private static final String STEP_CODE = "step";
	private static final String MARKER_ERROR = "marker_error";
	private static final String TYPE_NAVIGATION = "type_navigation";
	
	
	//sentencias de creacion de las tablas:
	private static final String CREATE_TABLE_SECUENCES_PHASES_EVAL_MODE = "CREATE TABLE "+SEQUENCE_PHASES_TABLE_NAME+"("+ID_SEQUENCE_FIELD+" INTEGER PRIMARY KEY,"
																														+FECHA_SECUENCIA+" TEXT,"
																														+SEQUENCE_FIELD+" TEXT)";
	
	private static final String CREATE_TABLE_SEQUENCE_STEPS_PHASE_ONE = "CREATE TABLE "+SEQUENCE_STEPS_PHASE_ONE_TBL_NAME+"("+ID_SEQ_STEPS_PHASE_ONE+" INTEGER PRIMARY KEY,"
																															 +FECHA_SEQ_STEPS_PHASE_ONE+" TEXT,"
																															 +SEQUENCE_STEPS_PHASE_ONE+" TEXT)";
	
	private static final String CREATE_TABLE_SEQUENCE_STEPS_PHASE_TWO = "CREATE TABLE "+SEQUENCE_STEPS_PHASE_TWO_TBL_NAME+"("+ID_SEQ_STEPS_PHASE_TWO+" INTEGER PRIMARY KEY,"
																															 +FECHA_SEQ_STEPS_PHASE_TWO+" TEXT,"
																															 +SEQUENCE_STEPS_PHASE_TWO+" TEXT)";
	
	private static final String CREATE_TABLE_NAVIGATION_MONITORING = "CREATE TABLE " + NAVIGATION_MONITORING_TBL_NAME + "(" + ID_EVENT_INTERFACE + " INTEGER PRIMARY KEY,"
																															+ ID_STUDENT + " TEXT,"
																															+ DATE_REGISTRY + " TEXT,"
																															+ INTERFACE_CODE + " TEXT,"
																															+ STEP_CODE + " TEXT,"
																															+ MARKER_ERROR + " TEXT,"
																															+ TYPE_NAVIGATION + " TEXT)";
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.d("Unity","Llamado al metodo OnCreate...");
		db.execSQL(CREATE_TABLE_SECUENCES_PHASES_EVAL_MODE);
		db.execSQL(CREATE_TABLE_SEQUENCE_STEPS_PHASE_ONE);
		db.execSQL(CREATE_TABLE_SEQUENCE_STEPS_PHASE_TWO);
		db.execSQL(CREATE_TABLE_NAVIGATION_MONITORING);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.d("Unity","Llamado al metodo OnUpgrade...");
		db.execSQL("DROP TABLE IF EXISTS "+SEQUENCE_PHASES_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+SEQUENCE_STEPS_PHASE_ONE_TBL_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+SEQUENCE_STEPS_PHASE_TWO_TBL_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+NAVIGATION_MONITORING_TBL_NAME);
		 onCreate(db);
	}
	
	//metodo que cierra la conexion a la BD:
	public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
	
	/**
	 * Metodo que inserta un registro de ordenamiento de fases del proceso en la base de datos local
	 * 
	 * @param sequence
	 * @param date
	 * @return long id de registro insertado
	 */
	public long InsertSequenceOfPhasesOrdered(String sequence, String date){
		SQLiteDatabase db = this.getWritableDatabase();
		 
        ContentValues values = new ContentValues();
        values.put(FECHA_SECUENCIA, date);
        values.put(SEQUENCE_FIELD, sequence);
 
        // insert row
        long todo_id = db.insert(SEQUENCE_PHASES_TABLE_NAME, null, values);
 
       //RETORNA EL ID DE LA FILA INSERTADA 
        return todo_id;
	}//cierra metodo que inserta registro de secuencia de ORDENAMIENTO DE FASES
	
	
	/**
	 * Metodo que permite insertar un registro en la BD sobre una secuencia de ordenamiento de pasos de la fase 1.
	 * 
	 * @param sequence
	 * @param date
	 * @return long id de registro insertado
	 */
	public long InsertSequenceOfStepsPhaseOne(String sequence, String date){
		SQLiteDatabase db = this.getWritableDatabase();
		 
        ContentValues values = new ContentValues();
        values.put(FECHA_SEQ_STEPS_PHASE_ONE, date);
        values.put(SEQUENCE_STEPS_PHASE_ONE, sequence);
 
        // insert row
        long todo_id = db.insert(SEQUENCE_STEPS_PHASE_ONE_TBL_NAME, null, values);
 
       //RETORNA EL ID DE LA FILA INSERTADA 
        return todo_id;
	}//cierra metodo que inserta registro de secuencias de pasos de la FASE 1
	

	public long InsertSequenceOfStepsPhaseTwo(String sequence, String date){
		SQLiteDatabase db = this.getWritableDatabase();
		 
        ContentValues values = new ContentValues();
        values.put(FECHA_SEQ_STEPS_PHASE_TWO, date);
        values.put(SEQUENCE_STEPS_PHASE_TWO, sequence);
 
        // insert row
        long todo_id = db.insert(SEQUENCE_STEPS_PHASE_TWO_TBL_NAME, null, values);
       
 
       //RETORNA EL ID DE LA FILA INSERTADA 
        return todo_id;
            
	} // cierra InsertSequenceOfStepsPhaseTwo
	
	//metodo que inserta registros de navegacion:
	public long InsertRegistryOfNavigationEvent(String id_estudiante, String fecha, String cod_interfaz, String step_code, String marcador_error, String tipo_navegacion){
		SQLiteDatabase db = this.getWritableDatabase();
		
		 ContentValues values = new ContentValues();
	     values.put(ID_STUDENT, id_estudiante);
	     values.put(DATE_REGISTRY, fecha);
	     values.put(INTERFACE_CODE, cod_interfaz);
	     values.put(STEP_CODE, step_code);
	     values.put(MARKER_ERROR, marcador_error);
	     values.put(TYPE_NAVIGATION, tipo_navegacion);
	     
	     // insert row
	        long todo_id = db.insert(NAVIGATION_MONITORING_TBL_NAME, null, values);
	       
	     //RETORNA EL ID DE LA FILA INSERTADA 
	    return todo_id;
	} //cierra InsertRegistryOfNavigationEvent
	
	
	
	public List<Sequence> ConsultarSecuenciasPhases() {
        List<Sequence> secuencias = new ArrayList<Sequence>();
        String selectQuery = "SELECT  * FROM " + SEQUENCE_PHASES_TABLE_NAME;
 
        Log.d("Unity", "Se va a ejecutar la consulta de secuencias de phases...");
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
            	Sequence seq = new Sequence(c.getString(c.getColumnIndex(FECHA_SECUENCIA)),c.getString(c.getColumnIndex(SEQUENCE_FIELD)));
                Log.d("Unity", "Secuencia recuperada: " + seq.sequence + ",fecha:" + seq.date_sequence);
                // adding to todo list
            	secuencias.add(seq);
            } while (c.moveToNext());
        }
 
        return secuencias;
    } // cierra ConsultarSecuenciasPhases
	
	public List<Sequence> ConsultarSecuenciasPhaseOne() {
        List<Sequence> secuencias_one = new ArrayList<Sequence>();
        String selectQuery = "SELECT  * FROM " + SEQUENCE_STEPS_PHASE_ONE_TBL_NAME;
 
        Log.d("Unity", "Se va a ejecutar la consulta de secuencias de pasos de la fase 1...");
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cur.moveToFirst()) {
            do {
            	Sequence seq = new Sequence(cur.getString(cur.getColumnIndex(FECHA_SEQ_STEPS_PHASE_ONE)),cur.getString(cur.getColumnIndex(SEQUENCE_STEPS_PHASE_ONE)));
                Log.d("Unity", "Secuencia recuperada: " + seq.sequence + ",fecha:" + seq.date_sequence);
                // adding to todo list
            	secuencias_one.add(seq);
            } while (cur.moveToNext());
        }
 
        return secuencias_one;
    } // cierra ConsultarSecuenciasPhaseOne
	
	
	public List<Sequence> ConsultarSecuenciasPhaseTwo() {
        List<Sequence> secuencias_two = new ArrayList<Sequence>();
        String selectQuery = "SELECT  * FROM " + SEQUENCE_STEPS_PHASE_TWO_TBL_NAME;
 
        Log.d("Unity", "Se va a ejecutar la consulta de secuencias de pasos de la fase 2...");
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curs = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (curs.moveToFirst()) {
            do {
            	Sequence seq = new Sequence(curs.getString(curs.getColumnIndex(FECHA_SEQ_STEPS_PHASE_TWO)),curs.getString(curs.getColumnIndex(SEQUENCE_STEPS_PHASE_TWO)));
                Log.d("Unity", "Secuencia recuperada: " + seq.sequence + ",fecha:" + seq.date_sequence);
                // adding to todo list
            	secuencias_two.add(seq);
            } while (curs.moveToNext());
        }
 
        return secuencias_two;
    } // cierra ConsultarSecuenciasPhaseOne
	
	
	
	public List<InterfaceRegistry> ConsultarRegistrosNavegacion() {
		
        List<InterfaceRegistry> navegaciones = new ArrayList<InterfaceRegistry>();
        String selectQuery = "SELECT  * FROM " + NAVIGATION_MONITORING_TBL_NAME;
 
        Log.d("Unity", "AdminDBSQLite: Se va a ejecutar la consulta de registros de NAVEGACION");
 
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor curs = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (curs.moveToFirst()) {
            do {
            	InterfaceRegistry regis = new InterfaceRegistry(curs.getString(curs.getColumnIndex(ID_EVENT_INTERFACE)),
            													curs.getString(curs.getColumnIndex(ID_STUDENT)),
            													curs.getString(curs.getColumnIndex(DATE_REGISTRY)),
            													curs.getString(curs.getColumnIndex(INTERFACE_CODE)),
            													curs.getString(curs.getColumnIndex(STEP_CODE)),
            													curs.getString(curs.getColumnIndex(MARKER_ERROR)),
            													curs.getString(curs.getColumnIndex(TYPE_NAVIGATION)));
            
              //Log.d("Unity", "Navegacion Consultada: Interfaz: " + regis.codigo_interfaz + ",fecha:" + regis.fecha_evento);
                // adding to todo list
              	navegaciones.add(regis);
            } while (curs.moveToNext());
        }
 
        return navegaciones;
    } // cierra ConsultarSecuenciasPhaseOne
	
	/**
	 * Metodo para eliminar todos los registros de la tabla de secuencias de ordenamiento de fases
	 * cuando ya se han sincronizado todos los registros hacia el servidor.
	 * 
	 */
	public void DeleteSequencesOfPhases(){
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(SEQUENCE_PHASES_TABLE_NAME, null, null);
		Log.d("Unity","Se han eliminado los datos de secuencias de ORDENAMIENTO DE FASES");
		ConsultarSecuenciasPhases();
	} //cierra DeleteSequencesOfPhases
	
	/**
	 * Metodo para eliminar todos los registros de la tabla de secuencias de ordenamiento de la FASE 1
	 * cuando ya se han sincronizado todos los registros hacia el servidor. (FASE 1)
	 */
	public void DeleteSequencesOfPhaseOne(){
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(SEQUENCE_STEPS_PHASE_ONE_TBL_NAME, null, null);
		Log.d("Unity","Se han eliminado los datos de secuencias de la FASE 1...");
	} //cierra DeleteSequencesOfPhases
	
	/**
	 * * Metodo para eliminar todos los registros de la tabla de secuencias de ordenamiento de la FASE 2
	 * cuando ya se han sincronizado todos los registros hacia el servidor. (FASE 2)
	 */
	public void DeleteSequencesOfPhaseTwo(){
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(SEQUENCE_STEPS_PHASE_TWO_TBL_NAME, null, null);
		Log.d("Unity","Se han eliminado los datos de secuencias de la FASE 2...");
	} //cierra DeleteSequencesOfPhases
	
	/**
	 * Metodo que permite eliminar los registros de la tabla de REGISTROS DE NAVEGACION
	 * cuando ya se han sincronizado todos los datos hacia el servidor:
	 */
	public void DeleteNavigationRegistry(){
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(NAVIGATION_MONITORING_TBL_NAME, null, null);
		Log.d("Unity","Se han eliminado TODOS los datos de NAVEGACION");
	} //cierra DeleteNavigationRegistry
	
}
