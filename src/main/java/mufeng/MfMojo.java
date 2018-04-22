package mufeng;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @goal introduce
 */
@Mojo(name ="introduce", defaultPhase = LifecyclePhase.PACKAGE)
public class MfMojo extends AbstractMojo{

    /**
     * @parameter expression="${msg}"
     * @required
     */
    private String msg;

    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("神保佑你 插件:" + msg);
    }
}
