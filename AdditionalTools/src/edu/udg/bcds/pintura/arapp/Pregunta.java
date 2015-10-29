package edu.udg.bcds.pintura.arapp;

import android.util.Log;

public class Pregunta {
	
	public String id_pregunta;
	public String enunciado;
	public String opciona;
	public String opcionb;
	public String opcionc;
	public String opciond;
	public String opcion_correcta;
	public String imagen_url;
	public boolean pregunta_contestada; //indica si esta pregunta ya ha sido respondida anteriormente
	public String respuesta_del_estud; //almacena la respuesta que ha dado el estudiante
	public boolean pregunta_correcta; //indica si la pregunta es correcta o no.
	public String id_test; //indica el test al que pertenece esta pregunta
	public String intento; //indica el intento en el que va el test 1er o 2do intento
	public int intent_resp_preg; //indica cuantas veces el estudiante ha tratado de responder la pregunta desde 
	
	
	public Pregunta(String id_preg, String enunc, String opc_a, String opc_b, String opc_c, String opc_d, String opc_correct, String img_url, boolean contestada, String resp_estud, String idtest, String intent, int intent_respond){
		this.id_pregunta = id_preg;
		this.enunciado = enunc;
		this.opciona = opc_a;
		this.opcionb = opc_b;
		this.opcionc = opc_c;
		this.opciond = opc_d;
		this.opcion_correcta = opc_correct;
		this.imagen_url = img_url;
		this.pregunta_contestada = contestada;
		pregunta_correcta = false;
		respuesta_del_estud = resp_estud;
		this.id_test = idtest;
		this.intento = intent;
		this.intent_resp_preg = intent_respond;
	}
	
	public void ValidarRespuesta(String respuesta_dada){
		
		this.respuesta_del_estud = respuesta_dada;
		
		Log.d("Unity", "ValidarRespuesta: respondio=" + respuesta_dada + ", resp_correct= " + this.opcion_correcta);
		if(!respuesta_dada.equalsIgnoreCase("")){
			if(respuesta_dada.equalsIgnoreCase(this.opcion_correcta)){
				pregunta_correcta = true;
			} else {
				pregunta_correcta = false;
			}
		}else {
			pregunta_correcta = false;
		}
		
		Log.d("Unity","Resultado de Validacion respuesta=" + pregunta_correcta);
	}
	
}
