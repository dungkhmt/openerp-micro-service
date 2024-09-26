package com.hust.baseweb.applications.education.classmanagement.utils;

import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.*;
import java.util.List;

public class ZipOutputStreamUtils {

    /**
     * @param outputStream
     * @param files             files to add
     * @param compressionMethod
     * @param password          to encrypt and decrypt the zip file. if the password is {@code null} or zero in length,
     *                          the zip file will not be encrypted
     * @param encryptionMethod
     * @param aesKeyStrength
     * @throws IOException
     */
    public static void zip(
        OutputStream outputStream,
        List<File> files,
        CompressionMethod compressionMethod,
        char[] password,
        EncryptionMethod encryptionMethod,
        AesKeyStrength aesKeyStrength
    ) throws IOException {
        boolean encrypt = true;

        if (null == password || 0 == password.length) {
            encrypt = false;
        }

        ZipParameters zipParameters = buildZipParameters(compressionMethod, encrypt, encryptionMethod, aesKeyStrength);
        byte[] buff = new byte[4096];
        int readLen;

        try (ZipOutputStream zos = initZipOutputStream(outputStream, encrypt, password)) {
            for (File file : files) {
                // Entry size has to be set if you want to add entries of STORE compression method (no compression)
                // This is not required for deflate compression
                if (zipParameters.getCompressionMethod() == CompressionMethod.STORE) {
                    zipParameters.setEntrySize(file.length());
                }

                zipParameters.setFileNameInZip(file.getName());
                zos.putNextEntry(zipParameters);

                try (InputStream is = new FileInputStream(file)) {
                    while ((readLen = is.read(buff)) != -1) {
                        zos.write(buff, 0, readLen);
                    }
                }

                zos.closeEntry();
            }
        }
    }

    private static ZipOutputStream initZipOutputStream(
        OutputStream os,
        boolean encrypt,
        char[] password
    ) throws IOException {
        return encrypt ? new ZipOutputStream(os, password) : new ZipOutputStream(os);

    }

    private static ZipParameters buildZipParameters(
        CompressionMethod compressionMethod,
        boolean encrypt,
        EncryptionMethod encryptionMethod,
        AesKeyStrength aesKeyStrength
    ) {
        ZipParameters zipParameters = new ZipParameters();

        zipParameters.setCompressionMethod(compressionMethod);

        if (encrypt) {
            zipParameters.setEncryptFiles(encrypt);
            zipParameters.setEncryptionMethod(encryptionMethod);
            zipParameters.setAesKeyStrength(aesKeyStrength);
        }

        return zipParameters;
    }
}
