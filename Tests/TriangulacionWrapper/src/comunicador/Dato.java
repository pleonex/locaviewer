/*
* The MIT License (MIT)
*
* Copyright (c) 2014 Prometheus
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package comunicador;

import java.util.Date;

public class Dato {
	private String ID;//ID del escritor del dato
	private Par<Integer,Integer> posicionSensor;//Posición del sensor bluetooth (una misma ID puede tener varios sensores)
	private String IDnino; //ID del niño
	private int intensidad;//Intensidad de la señal
	private long creacion;

	/*Constructor para la clase Dato
	 * @param newID		ID del sensor emisor
	 * @param posXB 	Posición X del sensor
	 * @param posYB		Posición Y del sensor
	 * @param IDnino	ID del niño
	 * @param newIntensidad		Intensidad de señal
	 * */

	public Dato(String newID, Integer posXB,Integer posYB, String newIDnino, Integer newIntensidad){
		ID=newID;
		posicionSensor = new Par<Integer, Integer>(posXB,posYB);
		IDnino=newIDnino;
		intensidad=newIntensidad;
		creacion = new Date().getTime();
	}


	/*Constructor para la clase Dato
	 * @param newID		ID del sensor emisor
	 * @param posB 		Posición del sensor
	 * @param IDnino	ID del niño
	 * @param newIntensidad		Intensidad de la señal
	 * */
	public Dato(String newID, Par<Integer,Integer> posB, String newIDnino, Integer newIntensidad){
		ID=newID;
		posicionSensor=posB;
		IDnino=newIDnino;
		intensidad=newIntensidad;
		creacion = new Date().getTime();
	}



	/*Constructor para la clase Dato
	 * @param newID		String con los datos de la clase
	 * */
	public Dato(String mensaje){
		String[] arr = mensaje.split(" ");
		ID=arr[0];
		Integer posXB = Integer.parseInt(arr[1]);
		Integer posYB = Integer.parseInt(arr[2]);
		posicionSensor = new Par<Integer, Integer>(posXB,posYB);
		IDnino=arr[3];
		intensidad=Integer.parseInt(arr[4]);
		creacion = Long.parseLong(arr[5]);
	}





	/*Devuelve la ID del sensor emisor
	 * @return	ID del sensor emisor
	 * */
	public String getID(){
		return ID;
	}


	/*Devuelve la posición del sensor
	 * @return	Posición del sensor
	 * */
	public Par<Integer,Integer> getPosicionSensor(){
		return posicionSensor;
	}


	/*Devuelve intensidad de señal
	 * @return	Intensidad de señal
	 * */
	public Integer getIntensidad(){
		return intensidad;
	}

	/*Devuelve la ID del niño
	 * @return	ID del niño
	 * */
	public String getIDNino(){
		return IDnino;
	}

	/*Devuelve el momento en el que fue creado el dato
	 * @return	Hora del dato
	 * */
	public long getCreacion(){
		return creacion;
	}


	/*Método toString sobrecargado
	 * @return Cadena de caracteres con el valor de los atributos
	 * */
	@Override
    public String toString() {
		return ID+" "+posicionSensor.getPrimero()+" "+posicionSensor.getSegundo()+" "+IDnino+" "+intensidad+" "+creacion;
    }



}
