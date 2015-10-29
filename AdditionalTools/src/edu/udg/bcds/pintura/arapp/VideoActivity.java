package edu.udg.bcds.pintura.arapp;
//package edu.udg.bcds.pintura.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnErrorListener;

public class VideoActivity extends Activity {

	private VideoView myVideoView;
	private int position = 0;
	private ProgressDialog progressDialog;
	private MediaController mediaControls;
	
	private String URL_video;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      
	      Intent intention = getIntent();
	      URL_video = intention.getStringExtra("URL");
	       // set the main layout of the activity
	      setContentView(R.layout.activity_video);
	      //set the media controller buttons
	       if (mediaControls == null) {
	           mediaControls = new MediaController(VideoActivity.this);
		        }
	
	       //initialize the VideoView
	       myVideoView = (VideoView) findViewById(R.id.video_view);
		 
	
	       // create a progress bar while the video file is loading
	       progressDialog = new ProgressDialog(VideoActivity.this);
	
	       // set a title for the progress bar
	       progressDialog.setTitle("Cargando el video... Porfavor espera");
	
	       // set a message for the progress bar
	       progressDialog.setMessage("Cargando video...");
	
	       //set the progress bar not cancelable on users' touch
	       progressDialog.setCancelable(false);
	       
	       //Definiendo un boton de cancelar para que el usuario pueda cancelar la carga del video en caso de que tarde mucho.
	       //este boton cierra completamente toda la actividad
	       progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				VideoActivity.this.finish();
			}
		});
	
	       // show the progress bar
	       progressDialog.show();
			 
	       try {
	           //set the media controller in the VideoView
	           myVideoView.setMediaController(mediaControls);
			 
	           //set the uri of the video to be played
	           myVideoView.setVideoURI(Uri.parse(URL_video));
	
	      } catch (Exception e) {
	           Log.e("Error", e.getMessage());
	           e.printStackTrace();
	        }
	
	      myVideoView.requestFocus();
	
	      //we also set an setOnPreparedListener in order to know when the video file is ready for playback
	      
	      myVideoView.setOnPreparedListener(new OnPreparedListener() {
	        
	      public void onPrepared(MediaPlayer mediaPlayer) {
	         // close the progress bar and play the video
	         progressDialog.dismiss();
	        
	         //if we have a position on savedInstanceState, the video playback should start from here
	          myVideoView.seekTo(position);
	           if (position == 0) {
	                myVideoView.start();
	           } else {
	           //if we come from a resumed activity, video playback will be paused
	               //myVideoView.pause();
	        	   myVideoView.start();
	           }
	      }
	 });
	      
	      
	      
	      myVideoView.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				Log.e("Unity", "Llamado al Listener ERROR del videoplayer");
				//if(extra == MediaPlayer.MEDIA_ERROR_IO || extra == MediaPlayer.MEDIA_ERROR_MALFORMED || extra == MediaPlayer.MEDIA_ERROR_UNSUPPORTED){
					AlertDialog.Builder builder1 = new AlertDialog.Builder(VideoActivity.this);
		            builder1.setMessage(R.string.message_video_error);
		            builder1.setTitle(R.string.message_video_error_title);
		            builder1.setPositiveButton(R.string.message_video_error_ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Log.e("Unity", "llamado al metodo OnClick del cuadro de dialogo");
								VideoActivity.this.finish();
							Log.e("Unity", "Despues de cerrar la actividad!!!");
						}
					} );
		            
		            AlertDialog alert11 = builder1.create();
		            alert11.show();
										
					return true;
				//} else 
					//return false;
			}
		});
    }
		 
	   @Override
	   public void onSaveInstanceState(Bundle savedInstanceState) {
	      super.onSaveInstanceState(savedInstanceState);
	     //we use onSaveInstanceState in order to store the video playback position for orientation change
	     savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
	       myVideoView.pause();
	    }
	
	 
	  @Override
	  public void onRestoreInstanceState(Bundle savedInstanceState) {
	      super.onRestoreInstanceState(savedInstanceState);
	     //we use onRestoreInstanceState in order to play the video playback from the stored position 
	     position = savedInstanceState.getInt("Position");
	       myVideoView.seekTo(position);
	 }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video, menu);
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
	
	public static void Call(Activity activity, String url_video)
	 {	
		 Log.d("Unity", "Llamado al metodo estatico!!!" + activity.toString());
		 Log.d("Unity","Parametro 2: " + url_video);
		 // Creating an intent with the current activity and the activity we wish to start
		 Intent myIntent = new Intent(activity, VideoActivity.class);
		 //Se envia como parametro la url del video que se envio desde Unity:
		 myIntent.putExtra("URL", url_video);
		 //myIntent.putExtra("URL", "http://piranya.udg.edu/pintuRA/videos/1_limpieza.mp4");
		 Log.d("Unity", "Creación del intent: " + myIntent.toString());
		 activity.startActivity(myIntent);
		 Log.d("Unity", "Despues de start activity " + myIntent.toString());
		 
	 }
}
