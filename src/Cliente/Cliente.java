package Cliente;

import JogadorFrame.JogadorFrame;

import javax.swing.*;

public class Cliente {

    public static void main(String [] args){

        JFrame frame = new JogadorFrame().rodar();

        frame.setVisible(true);

    }
}
