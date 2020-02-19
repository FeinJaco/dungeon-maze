package maze;

import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author 19feinjaco
 */
public class MazeMain {

    public static void main(String args[]) throws IOException {
        /*
        JFrame frame = new JFrame("Maze");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setVisible(true);
         */
        MazeModel m = new MazeModel();
        m.gameLoop();
    }
}
