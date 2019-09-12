package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;

public class Main {

	public static void main(String[] args) {
		try {
			int puertoPropio = 5215;
			DatagramSocket socket = new DatagramSocket(puertoPropio);
			HashSet<String> direccionesDisponibles = new HashSet<String>(); //
			ArrayList<String> nodos = new ArrayList<String>();         //Arreglo que contiene las direciones de los vecinos
			
			//Vecinos
			nodos.add("");  //Agrego las direciones de mis vecinos
			
			//Disponibles
			
			direccionesDisponibles.add("");
			
			while(true) {
				byte buffer[] = new byte[100];
				System.out.println("Esperando mensaje del cliente");
				DatagramPacket recivedPacket = new DatagramPacket(buffer, buffer.length);
				socket.receive(recivedPacket);
				byte[] bytesRecibidos = recivedPacket.getData();
				String mensajeRecivido = new String(bytesRecibidos).trim();     //Tenga el nombre que buscamos
				
				String protocolo[] = mensajeRecivido.split(",,");
				
				if(protocolo.length == 3){ //LLego del cliente
					
					String respuesta = "No encontramos nada";
					
					if(direccionesDisponibles.contains(mensajeRecivido)) {
						InetAddress ref = InetAddress.getByName(mensajeRecivido);
						respuesta = ref.getHostAddress();							//La respuesta sera la ip encontrada
					}else {
						for (int i = 0; i < nodos.size(); i++) {
							String dirAmigo[] = nodos.get(i).split(" ");
							
							String mensaje = protocolo[0]+",,"+InetAddress.getLocalHost().getAddress()+",,"+ puertoPropio +",,noRespuesta"; 
							//Mando el mensaje
							DatagramPacket sendingPacket = new DatagramPacket(mensaje.getBytes(), mensaje.getBytes().length,InetAddress.getByName(dirAmigo[0]),Integer.parseInt(dirAmigo[1]));
							DatagramSocket socket1 = new DatagramSocket();
							socket1.send(sendingPacket);
							
							//Devolvio el mensaje
							buffer = new byte[100];
							System.out.println("Esperando la respuesta del nodo de contacto");
							recivedPacket = new DatagramPacket(buffer, buffer.length);
							socket.receive(recivedPacket);
							bytesRecibidos = recivedPacket.getData();
							mensajeRecivido = new String(bytesRecibidos).trim();
							
							
							if(!mensajeRecivido.contains("noRespuesta")) {
								String infoRecibida[] = mensajeRecivido.split(" "); 
								respuesta = infoRecibida[3];
								break;
							}
							
						}

						DatagramPacket sendingPacket = new DatagramPacket(respuesta.getBytes(), respuesta.getBytes().length,InetAddress.getByName(protocolo[1]),Integer.parseInt(protocolo[2]));
						DatagramSocket socket1 = new DatagramSocket();
						socket1.send(sendingPacket);
						
					}
					

					System.out.println("Recevido> "+ respuesta);
					
				}else if(protocolo.length == 4 ){						//LLego de un servidor amigo
					String respuesta = "No encontramos nada";
					
					if(direccionesDisponibles.contains(protocolo[0])) {
						InetAddress ref = InetAddress.getByName(mensajeRecivido);
						respuesta = ref.getHostAddress();							//La respuesta sera la ip encontrada
					}
					
					DatagramPacket sendingPacket = new DatagramPacket(respuesta.getBytes(), respuesta.getBytes().length,InetAddress.getByName(protocolo[1]),Integer.parseInt(protocolo[2]));
					DatagramSocket socket1 = new DatagramSocket();
					socket1.send(sendingPacket);
					
				}
				
				
				
				
			}
			
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
