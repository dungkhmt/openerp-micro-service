package com.hust.baseweb.applications.programmingcontest.utils;

import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;
import com.hust.baseweb.applications.programmingcontest.utils.executor.GccExecutor;
import com.hust.baseweb.applications.programmingcontest.utils.executor.GolangExecutor;
import com.hust.baseweb.applications.programmingcontest.utils.executor.JavaExecutor;
import com.hust.baseweb.applications.programmingcontest.utils.executor.Python3Executor;
import com.hust.baseweb.config.FileSystemStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.FileSystemUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Configuration
public class TempDir {

    private final GccExecutor gccExecutor = new GccExecutor();

    private final JavaExecutor javaExecutor = new JavaExecutor();

    private final Python3Executor python3Executor = new Python3Executor();

    private final GolangExecutor golangExecutor = new GolangExecutor();

    public static ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();

    public static String TEMPDIR = null;

    @Autowired
    public TempDir(FileSystemStorageProperties properties) {
        // TEMPDIR = properties.getFilesystemRoot() + "temp_dir/";
        TEMPDIR = properties.getFilesystemRoot() + "./temp_dir/";
    }

    @Bean
    public void initTempDir(){
        File theDir = new File(TEMPDIR);
        if (!theDir.exists()){
            theDir.mkdirs();
        }
    }

    @Bean
    public void startRemoveTempDirThread(){
        RmTempDirTheard rmTempDirTheard = new RmTempDirTheard();
        rmTempDirTheard.start();
        System.out.println("-------------------------------  done start ---------------------------");
    }

    public void pushToConcurrentLinkedQueue(String dirName){
        concurrentLinkedQueue.add(dirName);
    }

    public String createRandomScriptFileName(String startName){
        //int generateRandom = r.nextInt();
        //String resp = startName + "-" + generateRandom;
        Date date = new Date();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = formatter.format(date);
        String[] dt = s.split(" ");
        String[] d = dt[0].split("-");
        String[] t = dt[1].split(":");
        String resp = startName + "-" + d[0] + d[1] + d[2] + t[0] + t[1] + t[2];
        resp = resp.replaceAll("\n", " ");
        resp = resp.replaceAll("&","");
        return resp.replaceAll("( +)", "-").trim();
    }

    public String createDirInContainer(String startName){
        return startName+"/"+startName+".sh";
    }

    public void createScriptFile(String source, String testCase, int timeLimit, ComputerLanguage.Languages languages, String tmpName ) throws IOException {
        File theDir = new File(TEMPDIR+tmpName);
        theDir.mkdirs();
        String sourceSh;
        switch (languages){
            case CPP:
                sourceSh = gccExecutor.generateScriptFileWithTestCaseAndCorrectSolution(source, testCase, tmpName, timeLimit);
                break;
            case JAVA:
                sourceSh = javaExecutor.generateScriptFileWithTestCaseAndCorrectSolution(source, testCase, tmpName, timeLimit);
                break;
            case PYTHON3:
                sourceSh = python3Executor.generateScriptFileWithTestCaseAndCorrectSolution(source, testCase, tmpName, timeLimit);
                break;
            case GOLANG:
                sourceSh = golangExecutor.generateScriptFileWithTestCaseAndCorrectSolution(source, testCase, tmpName, timeLimit);
                break;
            default:
                sourceSh = null;
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(TEMPDIR + tmpName+"/"+tmpName+".sh"));
        writer.write(sourceSh);
        writer.close();
    }

    public void createScriptCompileFile(String source, ComputerLanguage.Languages languages, String tmpName ) throws IOException {
        File theDir = new File(TEMPDIR+tmpName);
        theDir.mkdirs();
        String sourceSh;
        switch (languages){
            case CPP:
                sourceSh = gccExecutor.checkCompile(source, tmpName);
                break;
            case JAVA:
                sourceSh = javaExecutor.checkCompile(source, tmpName);
                break;
            case PYTHON3:
                sourceSh = python3Executor.checkCompile(source, tmpName);
                break;
            case GOLANG:
                sourceSh = golangExecutor.checkCompile(source, tmpName);
                break;
            default:
                sourceSh = null;
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(TEMPDIR + tmpName+"/"+tmpName+".sh"));
        writer.write(sourceSh);
        writer.close();
    }

    public void createScriptSubmissionFile(ComputerLanguage.Languages languages, String tmpName, List<TestCaseEntity> testCases, String source, int timeout, int memoryLimit) throws IOException {
        File theDir = new File(TEMPDIR+tmpName);
        theDir.mkdirs();
        String sourceSh;
        switch (languages){
            case CPP:
                sourceSh = gccExecutor.genSubmitScriptFile(testCases, source, tmpName, timeout, memoryLimit);
                break;
            case JAVA:
                sourceSh = javaExecutor.genSubmitScriptFile(testCases, source, tmpName, timeout, memoryLimit);
                break;
            case PYTHON3:
                sourceSh = python3Executor.genSubmitScriptFile(testCases, source, tmpName, timeout, memoryLimit);
                break;
            case GOLANG:
                sourceSh = golangExecutor.genSubmitScriptFile(testCases, source, tmpName, timeout);
                break;
            default:
                sourceSh = null;
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(TEMPDIR + tmpName+"/"+tmpName+".sh"));
        writer.write(sourceSh);
        writer.close();
    }
    public void createScriptSubmissionSolutionOutputFile(ComputerLanguage.Languages languages, String tmpName, String solutionOutput, TestCaseEntity testCase, String sourceChecker, int timeout) throws IOException {
        File theDir = new File(TEMPDIR+tmpName);
        theDir.mkdirs();
        String sourceSh = "";
        switch (languages){
            case CPP:
                sourceSh = gccExecutor.genSubmitScriptFileChecker(sourceChecker,testCase, solutionOutput, tmpName, timeout);
                break;
            case JAVA:
                //sourceSh = javaExecutor.genSubmitScriptFile(testCases, source, tmpName, timeout);
                break;
            case PYTHON3:
                //sourceSh = python3Executor.genSubmitScriptFile(testCases, source, tmpName, timeout);
                break;
            case GOLANG:
                //sourceSh = golangExecutor.genSubmitScriptFile(testCases, source, tmpName, timeout);
                break;
            default:
                sourceSh = null;
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(TEMPDIR + tmpName+"/"+tmpName+".sh"));
        writer.write(sourceSh);
        writer.close();
    }

    public void removeDir(String dirName){
        FileSystemUtils.deleteRecursively(new File(TEMPDIR + dirName));
    }


    class RmTempDirTheard extends Thread{
        public void run(){
            String dirName;
            while(true){
                while ((dirName = concurrentLinkedQueue.poll()) != null){
//                    System.out.println("rm dir " + dirName);
                    FileSystemUtils.deleteRecursively(new File(TEMPDIR + dirName));
                }
            }
        }
    }
}
