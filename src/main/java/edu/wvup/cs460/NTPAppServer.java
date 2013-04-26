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

/**
 * The main class and entry point of our NTP server.
 *
 */

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

    /**
     * Set up the course importer and make sure that the scheduler is running.
     */
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

    /**
     * Intialize our proeprties subsystem via the command line properties and the main properties file (if designated)
     * @param args
     */
    private void initProperties(String[] args) {
        //initialize the AppProperties
        _properties.initPropertiesFromCommandLine(args);
        _properties.setProperty("start.time", Long.toString(System.currentTimeMillis()));
        //initialize System properties for the Scheduler.
        final String instanceName = _properties.getProperty("org.quartz.scheduler.instanceName", "CourseRetrievalScheduler");
        System.setProperty("org.quartz.scheduler.instanceName", instanceName);
        final String threadCount = _properties.getProperty("org.quartz.threadPool.threadCount", "2");
        System.setProperty("org.quartz.threadPool.threadCount", threadCount);
        final String jobStoreClass = _properties.getProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        System.setProperty("org.quartz.jobStore.class", jobStoreClass);// keep in RAM, as we will start a new one when the server starts.


    }

    //--------------------------------------------------Protected Methods

    /**
     * We're going to insist that we are run in UTF8 mode, so if they didn't set it and it's not the default,
     * we're going to refuse to start.
     *
     */
    protected void checkFileEncoding() {
        //check that file encoding is set to UTF8, and blow up otherwise.
        final Charset UTF8 = Charset.forName("UTF-8");
        if (!Charset.defaultCharset().equals(UTF8)) {
            throw new RuntimeException("App Server must be set to UTF8 as default encoding. Try adding -Dfile.encoding=UTF-8 to your JVM args.");
        }
    }

    /**
     * Initalize our datastorage (and connection pool).
     */
    protected void initDataStorage() {
        DBContext context = new DBContext(_properties);
        _storageService = new DataStorage(context);

    }

    /**
     * Initialize the server, sockets and
     */
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
            bootstrap.bind(new InetSocketAddress(8080));
        }

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
