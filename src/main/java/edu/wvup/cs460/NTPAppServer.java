package edu.wvup.cs460;

import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.http.HttpServerPipelineFactory;
import edu.wvup.cs460.util.PropertiesHelper;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.Executors;

public class NTPAppServer {


    //----------------------------------------------Static Members
    private static final Logger LOG = LoggerFactory.getLogger(NTPAppServer.class);

    private static final NTPAppServer INSTANCE = new NTPAppServer();


    //-------------------------------------------------Instance Members
    private final AppProperties _properties = new AppProperties();

    private final DataStorage   _storageService = new DataStorage();

    protected NTPAppServer(){
        LOG.info("Creating NTPAppServer.");
    }

    public static NTPAppServer getInstance(){
        return INSTANCE;
    }

    //--------------------------------------------------Private Methods

    private void initialize(String[] args){
        LOG.info("Initializing App Server.");
        checkFileEncoding();
        initProperties(args);
        initAppServer();
    }
    //--------------------------------------------------Protected Methods
    protected void checkFileEncoding(){
        //check that file encoding is set to UTF8, and blow up otherwise.
        final Charset UTF8 = Charset.forName("UTF-8");
        if(!Charset.defaultCharset().equals(UTF8)){
            throw new RuntimeException("App Server must be set to UTF8 as default encoding. Try adding -Dfile.encoding=UTF-8 to your JVM args.");
        }
    }

    protected void initProperties(String[] args){
        //look for properties
        Properties cliProps = PropertiesHelper.parsePropsFromCommandLine(args);

        final String mainPropsFilePath = cliProps.getProperty("main.properties");
        if(null != mainPropsFilePath){
            File mainPropsFile = new File(mainPropsFilePath);
            if(mainPropsFile.exists()){
                //read in main props
                try {
                    final Properties properties = PropertiesHelper.readPropsFile(mainPropsFile);
                    _properties.mergeProperties(properties);//merge to main.
                } catch (IOException e) {
                    LOG.error("Unable to read properties file.", e);
                }
            }
        }
        System.getProperties().getProperty("someprops.propfield");

    }


    protected void initAppServer(){
        // Configure the server.

        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpServerPipelineFactory());

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(8080));

    }

    //--------------------------------------------------Public Methods

    public DataStorage getStorageService(){
        return _storageService;
    }



    //--------------------------------------------------Main Method
    public static void main(String[] args) {
        NTPAppServer instance = NTPAppServer.getInstance();
        instance.initialize(args);
    }
}
