package edu.wvup.cs460;

import edu.wvup.cs460.configuration.AppProperties;
import edu.wvup.cs460.dataaccess.DataStorage;
import edu.wvup.cs460.db.DBContext;
import edu.wvup.cs460.http.HttpServerPipelineFactory;
import edu.wvup.cs460.transform.CourseImportJob;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
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

    private int     _boundPort = 80;

    private String  _boundAddress;

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
        writeInfoDialog();
    }

    private void writeInfoDialog(){
        String serverInfo = "http:/"+_boundAddress+":"+_boundPort;
        JOptionPane.showMessageDialog(null, "This server is running on: "+serverInfo,"Server Information", JOptionPane.INFORMATION_MESSAGE);
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
            final InetSocketAddress inetSocketAddress = new InetSocketAddress(_boundPort);
            bootstrap.bind(inetSocketAddress);
        } catch (Throwable t) {
            LOG.error("Unable to bind to port 80, falling back to 8080.");
            _boundPort = 8080;
            final InetSocketAddress inetSocketAddress = new InetSocketAddress(_boundPort);
            bootstrap.bind(inetSocketAddress);
        }
        setBoundInterface();
    }

    private void setBoundInterface(){
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(networkInterfaces.hasMoreElements()){
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                for(InterfaceAddress ia : interfaceAddresses){
                    String s = ia.getAddress().toString();
                    if((ia.getAddress() instanceof Inet4Address) && (!s.startsWith("127.")) ){
                        _boundAddress = s;
                        return;//first non-localhost
                    }
                }
            }
        } catch (Throwable e) {
            LOG.warn("Unable to fetch Socket address.", e);
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
