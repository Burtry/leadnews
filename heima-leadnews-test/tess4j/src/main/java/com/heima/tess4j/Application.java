package com.heima.tess4j;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class Application {

    public static void main(String[] args) throws TesseractException {

        ITesseract tesseract = new Tesseract();

        tesseract.setDatapath("D:\\DEV\\code\\Javaweb\\leadnews\\heima-leadnews-test\\tess4j\\src\\main\\resources");

        tesseract.setLanguage("chi_sim");

        File file = new File("D:\\Snipaste_2024-08-07_10-37-41.png");

        String result = tesseract.doOCR(file);
        System.out.println(result.replaceAll("\\r|\\n", "-"));
    }
}
