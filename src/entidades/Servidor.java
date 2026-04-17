package entidades;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Servidor {

    public void operar(int puerto) throws Exception {
        // crear socket datagrama
        DatagramSocket socket = new DatagramSocket(puerto);
        System.out.println("Servidor iniciado en el puerto " + puerto);

        // buffer reutilizable para recibir datos
        byte[] bufferE = new byte[1024];
        // modelo para operaciones
        Modelo modelo = new Modelo();

        DatagramPacket entrada = null;
        // bucle infinito para que el servidor siempre esté escuchando
        while (true) {
            try {
                entrada = new DatagramPacket(bufferE, bufferE.length);
                socket.receive(entrada);

                // procesar
                String recibido = new String(entrada.getData(), 0, entrada.getLength());
                String[] partes = recibido.trim().split(",");

                if (partes.length < 2) {
                    throw new IllegalArgumentException("Formato inválido. Se esperaba: n1,n2[,op]");
                }

                double n1 = Double.parseDouble(partes[0]);
                double n2 = Double.parseDouble(partes[1]);
                double respuesta;

                if (partes.length == 2) {
                    respuesta = modelo.sumar(n1, n2);
                } else {
                    String op = partes[2].trim();
                    switch (op) {
                        case "+":
                            respuesta = modelo.sumar(n1, n2);
                            break;
                        case "-":
                            respuesta = modelo.restar(n1, n2);
                            break;
                        case "*":
                            respuesta = modelo.multiplicar(n1, n2);
                            break;
                        case "/":
                            respuesta = modelo.dividir(n1, n2);
                            break;
                        default:
                            throw new IllegalArgumentException("Operador no reconocido: " + op);
                    }
                }

                // devolver la solicitud
                byte[] bufferS = String.valueOf(respuesta).getBytes(); // por la comunicacion solo se envian bytes
                DatagramPacket salida = new DatagramPacket(bufferS, bufferS.length, entrada.getAddress(), entrada.getPort());
                socket.send(salida);

                System.out.println("Procesada petición de " + entrada.getAddress() + ":" + entrada.getPort() + " -> " + recibido + " = " + respuesta);
            } catch (Exception e) {
                String error = "Error: " + e.getMessage();

                byte[] bufferS = error.getBytes();
                DatagramPacket salida = new DatagramPacket(
                        bufferS,
                        bufferS.length,
                        entrada.getAddress(),
                        entrada.getPort()
                );

                socket.send(salida);
                System.err.println("Error al procesar petición: " + e.getMessage());
            }
        }
    }
}
