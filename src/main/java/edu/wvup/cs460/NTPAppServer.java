package edu.wvup.cs460;

import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.db.DBContext;
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

    private DataStorage   _storageService;

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
        _properties.initPropertiesFromCommandLine(args);
        initDataStorage();
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

    protected void initDataStorage(){
        DBContext context = new DBContext(_properties);
        _storageService = new DataStorage(context);

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
