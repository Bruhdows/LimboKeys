package com.bruhdows.limbokeys.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtil {

    public BufferedImage quickHueShift(BufferedImage img, float degrees) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        float shift = degrees / 360f;

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int pixel = img.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xFF;

                if (alpha == 0) {
                    result.setRGB(x, y, pixel);
                    continue;
                }

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                float[] hsb = Color.RGBtoHSB(r, g, b, null);
                hsb[0] = (hsb[0] + shift) % 1.0f;

                int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                result.setRGB(x, y, (alpha << 24) | (rgb & 0xFFFFFF));
            }
        }
        return result;
    }

    public BufferedImage shiftToFullSaturation(BufferedImage img, float hueDegrees) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        float hue = hueDegrees / 360f;

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int pixel = img.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xFF;

                if (alpha == 0) {
                    result.setRGB(x, y, pixel);
                    continue;
                }

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                float[] hsb = Color.RGBtoHSB(r, g, b, null);
                hsb[0] = hue;
                hsb[1] = 1.0f;
                hsb[2] = Math.max(0.8f, hsb[2]);

                int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                result.setRGB(x, y, (alpha << 24) | (rgb & 0xFFFFFF));
            }
        }
        return result;
    }

    public BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) return (BufferedImage) img;

        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return bi;
    }
}
