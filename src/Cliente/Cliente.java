package Cliente;

import JogadorFrame.JogadorFrame;

import javax.swing.*;
import java.util.Scanner;

public class Cliente {

    public static void main(String [] args){

        System.out.println("Digite o host:");
        Scanner entrada = new Scanner(System.in);
        String host = entrada.nextLine();

        JFrame frame = JogadorFrame.rodar(host);

        frame.setVisible(true);

    }
}
