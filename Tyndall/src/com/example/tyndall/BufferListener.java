/**
 * 
 * File : BufferListener.java
 * Created : June 2013 
 * Author : Emilie Michaud 
 * 			Tyndall Institute
 * 
 */
package com.example.tyndall;
import java.util.EventListener;

/**
 * Interface for buffer (data received) from the client
 */
public interface BufferListener extends EventListener{
	//called by reportBufferHasChanged (so by setNewBuffer) in HandlerThread
	public void bufferHasChanged(byte[] newBuffer);//What you have to do when buffer has changed and is now newBuffer
	public void bufferHasStopped(); // What you have to do when a socket is disconnected

}



