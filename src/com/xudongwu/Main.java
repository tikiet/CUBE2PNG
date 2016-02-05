package com.xudongwu;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("input " + args[0]);
        for (String name : args) {
            encode(name);
        }
    }

    private static void encode(String arg) {
        try {
            int lutSize = 0;
            BufferedReader br = new BufferedReader(new FileReader(new File(arg)));
            String line;
            String[] parts;
            float[][] data = null;
            int index = 0;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.isEmpty()) {
                    continue;
                }
                parts = line.split(" ");
                if (parts.length == 2) {
                    if (parts[0].equalsIgnoreCase("LUT_3D_SIZE")) {
                        lutSize = Integer.parseInt(parts[1]);
                        if (data == null) {
                            data = new float[lutSize * lutSize * lutSize][];
                            continue;
                        } else {
                            System.out.println("corrupted data");
                            System.exit(0);
                        }
                    }
                }

                if (parts.length != 3) {
                    System.out.println("unknown data: " + line);
                    continue;
                }
                if (data == null) {
                    System.out.println("not ready yet");
                    continue;
                }

                float[] pixel = new float[3];
                pixel[0] = Float.parseFloat(parts[0]);
                pixel[1] = Float.parseFloat(parts[1]);
                pixel[2] = Float.parseFloat(parts[2]);
                data[index++] = pixel;
            }

            int dim = lutSize * (int) Math.sqrt(lutSize);
            index = 0;
            System.out.println("dimension " + dim + "x" + dim);

            BufferedImage bfi = new BufferedImage(dim, dim, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g2d = bfi.createGraphics();

            for (int y = 0; y < dim; y++) {
                for (int x = 0; x < dim; x++) {
                    float[] raw = data[index++];
                    Color color = new Color(raw[0], raw[1], raw[2]);
                    g2d.setColor(color);
                    g2d.drawLine(x, y, x, y);
                }
            }

            ImageIO.write(bfi, "png", new File(arg + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
