package edu.wvup.cs460;

import edu.wvup.cs460.configuration.AppProperties;
import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.db.DBContext;
import edu.wvup.cs460.http.HttpServerPipelineFactory;
import edu.wvup.cs460.transform.CourseImportJob;
import edu.wvup.cs460.util.PropertiesHelper;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
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

    private DataStorage _storageService;

    protected NTPAppServer() {
        LOG.info("Creating NTPAppServer.");
    }

    public static NTPAppServer getInstance() {
        return INSTANCE;
    }

    //--------------------------------------------------Private Methods

    private void initialize(String[] args) {
        LOG.info("Initializing App Server.");
        checkFileEncoding();
        initProperties(args);
        initDataStorage();
        initCourseRetrievalScheduler();
        initAppServer();
    }

    private void initCourseRetrievalScheduler() {
        try {
            // Grab the Scheduler instance from the Factory
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            scheduler.start();

            CourseImportJob.scheduleImportJob(scheduler);

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    private void initProperties(String[] args) {
        //initialize the AppProperties
        _properties.initPropertiesFromCommandLine(args);

        //initialize System properties for the Scheduler.

        System.setProperty("org.quartz.scheduler.instanceName", "CourseRetrievalScheduler");
        System.setProperty("org.quartz.threadPool.threadCount", "2");
        System.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");// keep in RAM, as we will start a new one when the server starts.

       /* org.quartz.scheduler.instanceName = MyScheduler
        org.quartz.threadPool.threadCount = 3
        org.quartz.jobStore.class = org.quartz.simpl.RAMJobStore  */
    }

    //--------------------------------------------------Protected Methods
    protected void checkFileEncoding() {
        //check that file encoding is set to UTF8, and blow up otherwise.
        final Charset UTF8 = Charset.forName("UTF-8");
        if (!Charset.defaultCharset().equals(UTF8)) {
            throw new RuntimeException("App Server must be set to UTF8 as default encoding. Try adding -Dfile.encoding=UTF-8 to your JVM args.");
        }
    }

    protected void initDataStorage() {
        DBContext context = new DBContext(_properties);
        _storageService = new DataStorage(context);

    }

    protected void initAppServer() {
        // Configure the server.

        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new HttpServerPipelineFactory());

        // Bind and start to accept incoming connections.
        try {
            bootstrap.bind(new InetSocketAddress(80));
        } catch (Throwable t) {
            LOG.error("Unable to bind to port 80, falling back to 8080.");
        }
        bootstrap.bind(new InetSocketAddress(8080));

    }

    //--------------------------------------------------Public Methods

    public DataStorage getStorageService() {
        return _storageService;
    }

    public AppProperties getAppProperties() {
        return _properties;
    }

    //--------------------------------------------------Main Method
    public static void main(String[] args) {
        NTPAppServer instance = NTPAppServer.getInstance();
        instance.initialize(args);
    }
}
