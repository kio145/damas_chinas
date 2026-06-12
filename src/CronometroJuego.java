package src;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CronometroJuego {

    private Timer timer;
    private int segundos;
    private JLabel etiqueta;

    public CronometroJuego(JLabel etiqueta) {
        this.etiqueta = etiqueta;
        this.segundos = 0;
        actualizarTexto();
    }

    public void iniciar() {
        detener();

        segundos = 0;
        actualizarTexto();

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                segundos++;
                actualizarTexto();
            }
        });

        timer.start();
    }

    public void detener() {
        if (timer != null) {
            timer.stop();
        }
    }

    public void reiniciar() {
        iniciar();
    }

    public String getTextoTiempo() {
        if (etiqueta != null) {
            return etiqueta.getText();
        }

        return "Tiempo de partida: 00:00";
    }

    private void actualizarTexto() {
        int minutos = segundos / 60;
        int seg = segundos % 60;

        String tiempo = String.format("%02d:%02d", minutos, seg);

        if (etiqueta != null) {
            etiqueta.setText("Tiempo de partida: " + tiempo);
        }
    }
}