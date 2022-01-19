/**
 * Copyright (c) 2019-2099, Raptor
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.javafx.printclient.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 文件压缩工具类
 *
 * @author hxy
 */
public class ZipUtil {
    private static final int BUFFER_SIZE = 2 * 1024;
    /**
     * 是否保留原来的目录结构
     * true:  保留目录结构;
     * false: 所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     */
    private static final boolean KeepDirStructure = true;
    private static final Logger log = LoggerFactory.getLogger(ZipUtil.class);

//    public static void main(String[] args) {
//        try {
//            toZip("E:/app1", "E:/app.zip", true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 压缩成ZIP
     *
     * @param srcDir       压缩 文件/文件夹 路径
     * @param outPathFile  压缩 文件/文件夹 输出路径+文件名 D:/xx.zip
     * @param isDelSrcFile 是否删除原文件: 压缩前文件
     */
    public static void toZip(String srcDir, String outPathFile, boolean isDelSrcFile) throws Exception {
        long start = System.currentTimeMillis();
        FileOutputStream out = null;
        ZipOutputStream zos = null;
        try {
            out = new FileOutputStream(new File(outPathFile));
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            if (!sourceFile.exists()) {
                throw new Exception("需压缩文件或者文件夹不存在");
            }
            compress(sourceFile, zos, sourceFile.getName());
            if (isDelSrcFile) {
                delDir(srcDir);
            }
            log.info("原文件:{}. 压缩到:{}完成. 是否删除原文件:{}. 耗时:{}ms. ", srcDir, outPathFile, isDelSrcFile, System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("zip error from ZipUtils: {}. ", e.getMessage());
            e.printStackTrace();
            throw new Exception("zip error from ZipUtils");
        } finally {
            IoUtil.closeQuietly(out);
            IoUtil.closeQuietly(zos);
        }
    }

    /**
     * 递归压缩方法
     *
     * @param sourceFile 源文件
     * @param zos        zip输出流
     * @param name       压缩后的名称
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name)
            throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            zos.putNextEntry(new ZipEntry(name));
            int len;

            FileInputStream in = null;
            try {
                in = new FileInputStream(sourceFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IoUtil.closeQuietly(in);
            }

        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (KeepDirStructure) {
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    if (KeepDirStructure) {
                        compress(file, zos, name + "/" + file.getName());
                    } else {
                        compress(file, zos, file.getName());
                    }
                }
            }
        }
    }

    /**
     * 解压文件到指定目录
     */
    @SuppressWarnings({"rawtypes", "resource"})
    public static void unZipFiles(String zipPath, String descDir) throws IOException {
        log.info("文件:{}. 解压路径:{}. 解压开始.", zipPath, descDir);
        long start = System.currentTimeMillis();
        ZipFile zip = null;
        try {
            File zipFile = new File(zipPath);
            System.err.println(zipFile.getName());
            if (!zipFile.exists()) {
                throw new IOException("需解压文件不存在.");
            }
            File pathFile = new File(descDir);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            zip = new ZipFile(zipFile, Charset.forName("GBK"));
            InputStream in = null;
            OutputStream out = null;
            ZipEntry entry = null;
            BufferedInputStream nf = null;
            for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
                try {
                    entry = (ZipEntry) entries.nextElement();
                    String zipEntryName = entry.getName();
                    System.err.println(zipEntryName);

                    in = zip.getInputStream(entry);
                    nf = new BufferedInputStream(in);

                    String outPath = (descDir + File.separator + zipEntryName).replaceAll("\\\\\\\\", "\\\\");
                    System.err.println(outPath);
                    // 判断路径是否存在,不存在则创建文件路径
                    File file = new File(descDir);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                    if (new File(outPath).isDirectory()) {
                        continue;
                    }
                    // 输出文件路径信息
                    out = new FileOutputStream(outPath);
                    byte[] buf1 = new byte[1024];
                    int len;
                    while ((len = nf.read(buf1)) > 0) {
                        out.write(buf1, 0, len);
                    }
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException(e);
                } finally {
                    IoUtil.closeQuietly(in);
                    IoUtil.closeQuietly(nf);
                    IoUtil.closeQuietly(out);
                }

            }
            log.info("文件:{}. 解压路径:{}. 解压完成. 耗时:{}ms. ", zipPath, descDir, System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.info("文件:{}. 解压路径:{}. 解压异常:{}. 耗时:{}ms. ", zipPath, descDir, e, System.currentTimeMillis() - start);
            e.printStackTrace();
            throw new IOException(e);
        } finally {
            IoUtil.closeQuietly(zip);
        }
    }

    // 删除文件或文件夹以及文件夹下所有文件
    public static void delDir(String dirPath) throws IOException {
        log.info("删除文件开始:{}.", dirPath);
        long start = System.currentTimeMillis();
        try {
            File dirFile = new File(dirPath);
            if (!dirFile.exists()) {
                return;
            }
            if (dirFile.isFile()) {
                log.debug("删除1");
                dirFile.delete();
                return;
            }
            File[] files = dirFile.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                delDir(files[i].toString());
            }
            log.debug("删除2");
            dirFile.delete();
            log.info("删除文件:{}. 耗时:{}ms. ", dirPath, System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.info("删除文件:{}. 异常:{}. 耗时:{}ms. ", dirPath, e, System.currentTimeMillis() - start);
            e.printStackTrace();
            throw new IOException("删除文件异常.");
        }
    }
}