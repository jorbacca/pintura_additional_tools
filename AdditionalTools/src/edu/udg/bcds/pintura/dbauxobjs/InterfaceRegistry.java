package edu.udg.bcds.pintura.dbauxobjs;

public class InterfaceRegistry {
	
	public String id_registro;
	public String id_estudiante;
	public String fecha_evento;
	public String codigo_interfaz;
	public String codigo_paso;
	public String marcador_error;
	public String tipo_navegacion;
	
			
	public InterfaceRegistry(String id_reg, String id_estud, String date, String codigo_interf, String paso, String marcador_err, String tipo_naveg){
		this.id_registro = id_reg;
		this.id_estudiante = id_estud;
		this.fecha_evento = date;
		this.codigo_interfaz = codigo_interf;
		this.codigo_paso = paso;
		this.marcador_error = marcador_err;
		this.tipo_navegacion = tipo_naveg;
		
	}

}
