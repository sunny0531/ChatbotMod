package com.github.sunny0531.chatbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class unTar {
       public static void main(String[] args) throws IOException {
           //Thanks, GitHub Copilot
        File file = new File(args[0]);
        FileInputStream fis = new FileInputStream(file);
        GZIPInputStream gis = new GZIPInputStream(fis);
        TarArchiveInputStream tarIn = new TarArchiveInputStream(gis);
        TarArchiveEntry entry = null;
        while ((entry = tarIn.getNextTarEntry()) != null) {
            File currFile = new File(args[1]+File.separator+entry.getName());
            if (entry.isDirectory()) {
                currFile.mkdirs();
                continue;
            }
            File parent = currFile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(currFile);
            IOUtils.copy(tarIn, fos);
            fos.close();
        }
        tarIn.close();
    }
}