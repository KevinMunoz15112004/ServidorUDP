package test;

import entidades.Servidor;

public class TestServidor {
    static void main(String[] args) throws Exception {
        Servidor servidor = new Servidor();
        servidor.operar(5000);
    }
}
