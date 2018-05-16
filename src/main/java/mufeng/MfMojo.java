package mufeng;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * maven 代码统计插件
 *
 * project.build.sourceDirectory ： \src\main\java
 * project.build.testSourceDirectory ： \src\test\java
 * project.build.resources ： \src\main\resources
 * project.build.testResources ： \src\test\resources
 *
 * project.basedir  basedir.getAbsolutePath() : D:\github\design-pattern
 * @goal count
 */
//@Mojo(name ="count", defaultPhase = LifecyclePhase.PACKAGE)  这个注解无效，运行插件会报NullPointException问题
public class MfMojo extends AbstractMojo{
    private Log logger = getLog();
    /**
     * default includes
     * 约定配置
     */
    private static final String[] INCLUDES_DEFAULT = {"java", "xml", "properties"};

    /**
     * @parameter expression = "${project.basedir}"
     * @required
     * @readonly
     */
    private File basedir;

    /**
     * @parameter expression = "${project.build.sourceDirectory}"
     * @required
     * @readonly
     */
    private File sourceDirectory;

    /**
     * @parameter expression = "${project.build.testSourceDirectory}"
     * @required
     * @readonly
     */
    private File testSourceDirectory;

    /**
     * @parameter expression = "${project.build.resources}"
     * @required
     * @readonly
     */
    private List<Resource> resources;

    /**
     * @parameter expression = "${project.build.testResources}"
     * @required
     * @readonly
     */
    private List<Resource> testResources;

    /**
     * file types which will be included for counting
     * @parameter
     */
    private String[] includes;

    /**
     * execute
     * @throws MojoExecutionException MojoExecutionException
     * @throws MojoFailureException MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if(includes == null || includes.length == 0){
            includes = INCLUDES_DEFAULT;
        }
        try{
            countDir(sourceDirectory);
            countDir(testSourceDirectory);
            for(Resource resource : resources){
                countDir(new File(resource.getDirectory()));
            }
            for(Resource testResource : testResources){
                countDir(new File(testResource.getDirectory()));
            }
        }catch (Exception e){
            throw new MojoExecutionException("count failed:", e);
        }
    }

    /**
     * 统计某个目录下文件的代码行
     * @param dir 目录
     * @throws IOException 文件异常
     */
    private void countDir (File dir) throws IOException{
        if(!dir.exists()){
            return;
        }
        List<File> collected = new ArrayList<File>();
        collectFiles(collected, dir);
        int lines = 0;
        for(File sourceFile : collected){
            lines += countLine(sourceFile);
        }
        getLog().info(basedir.getAbsolutePath() + ":length : " + basedir.getAbsolutePath().length());
        String path = dir.getAbsolutePath().substring(basedir.getAbsolutePath().length());
        getLog().info(path + ": " + lines + " lines of code in " + collected.size() + "files");
    }

    /**
     * 递归获取文件列表
     * @param collected 文件列表list
     * @param file 文件
     */
    private void collectFiles(List<File> collected, File file){
        if(file.isFile()){
            for(String include : includes){
                if(file.getName().endsWith("." + include)){
                    collected.add(file);
                    break;
                }
            }
        }else{
            for(File sub : file.listFiles()){
                collectFiles(collected, sub);
            }
        }
    }

    /**
     * 读取文件的行数
     * @param file 文件对象
     * @return line
     * @throws IOException 文件操作异常
     */
    private int countLine(File file) throws IOException{
        int line = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String s = reader.readLine().trim();
                if (s.length() > 0) {
                    boolean flag = !s.startsWith("//") && !(s.startsWith("<!--") && s.endsWith("-->"));
                    if (flag) {
                        line++;
                    }
                }
            }
        }
        return  line;
    }
}
