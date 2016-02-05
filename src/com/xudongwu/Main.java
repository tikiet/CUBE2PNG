package com.xudongwu;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("input " + args[0]);
        try {
            for (String name : args) {
                System.out.println("processing " + name);
                encode(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void encode(String arg) throws IOException {
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

        for (int blockY = 0; blockY < dim / lutSize; blockY++) {
            for (int blockX = 0; blockX < dim / lutSize; blockX++) {
                for (int y = 0; y < lutSize; y++) {
                    for (int x = 0; x < lutSize; x++) {
                        float[] raw = data[index++];
                        int r = (int) (raw[0] * 255.0 + 0.5);
                        int g = (int) (raw[1] * 255.0 + 0.5);
                        int b = (int) (raw[2] * 255.0 + 0.5);
                        Color color = new Color(r, g, b);
                        g2d.setColor(color);
                        g2d.drawLine(
                                blockX * lutSize + x,
                                blockY * lutSize + y,
                                blockX * lutSize + x,
                                blockY * lutSize + y);
                    }
                }
            }
        }

        ImageIO.write(bfi, "png", new File(arg + ".png"));
    }
}
