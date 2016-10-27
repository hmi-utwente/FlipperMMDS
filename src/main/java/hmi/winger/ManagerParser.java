package hmi.winger;

import hmi.winger.managers.Manager;
import hmi.winger.managers.SimpleManager;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import hmi.winger.flipperextensions.ManageableBehaviourClass;
import hmi.winger.flipperextensions.ManageableFunction;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Siewart van Wingerden
 */
public class ManagerParser {
    
    protected DefaultRecord informationState;
    protected HashMap<String,Integer> errors = new HashMap<>();

    public ManagerParser(DefaultRecord informationState){
        this.informationState = informationState;
    }
    
    public ManagerParser(){
        this(new DefaultRecord());
    }
    
    public HashMap<String,Integer> getErrors()
    {
        return errors;
    }
     
    public ArrayList<Manager> parseFolder(String pathName) {
        return parseFolder("manager.xsd", pathName);
    }
    
    public DefaultRecord getIS(){
        return informationState;
    }
    
    public ArrayList<Manager> parseFolder(String xsdFileName, String pathName) {
        
        ArrayList<Manager> managers = new ArrayList<>();
        File folder = new File(pathName);
        
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                try {
                    ArrayList<Manager> newManagers = parseFolder(xsdFileName, fileEntry.getCanonicalPath());
                    if(newManagers != null){
                        managers.addAll(newManagers);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ManagerParser.class.getName()).log(Level.WARNING, null, ex);
                    
                }
            } else {
               String filename = "";
                try {
                    filename = fileEntry.getCanonicalPath();
                    Collection<Manager> newManagers = parseFile(xsdFileName, filename);
                    if(newManagers != null){
                        managers.addAll(newManagers);
                    }
                } catch (IOException | ParserConfigurationException | SAXException ex){
                    System.out.println("Could not parse: " + filename + ": "+ ex.getClass().getSimpleName() +" --- " + ex.getMessage() );
                }                
            }
        }
        return managers;
    }   
    public ArrayList<Manager> parseFile( String fileName ) throws ParserConfigurationException,SAXException,IOException
    {
        errors.clear();
        return  parseFile("managers.xsd", fileName);
    }


    public ArrayList<Manager> parseFile( String xsdFileName, String fileName ) throws ParserConfigurationException,SAXException,IOException
    {
        /* Find the file */
        //this.fileName = fileName;
        errors.clear();
        
        InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        
        if( inStream == null ) {
            
            inStream = new FileInputStream(fileName);
            if(inStream == null){
                throw new IOException("File '"+fileName+"' does not exists");
            }
        }

        URL xsdFile = this.getClass().getClassLoader().getResource(xsdFileName);
        if(xsdFile == null){
            throw new IOException("File '"+xsdFileName+"' does not exists");
        }

        /* Create a new Document-builder, based on the specified XSD */
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", ""+xsdFile);
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        docBuilder.setErrorHandler( new XSDErrorHandler(this, fileName) );
        Document doc = docBuilder.parse(inStream);
        ArrayList<Manager> mans = parseDocument( doc, fileName );
        return mans;
    }

    public ArrayList<Manager> parseDocument( Document doc, String filePath )
    {
        ArrayList<Manager> managers = new ArrayList<>();

        /* Find all <template>-Elements, and parse those */
        NodeList managerNodeList = doc.getElementsByTagName("manager");
        for( int i=0; i<managerNodeList.getLength(); i++ ) {
            Element managerElement = (Element)managerNodeList.item(i);
            try {
                Manager manager;
                manager = parseManagerElement( managerElement, filePath );
                if(manager != null){
                    managers.add(manager);
                }
                System.out.println("Loaded "+ manager.getName() + "("+manager.getID()+")");
            }catch( Exception e ) {
                System.err.println("Manager Parse Error in '"+filePath + " : "+ e.getClass().getSimpleName());
                e.printStackTrace(System.err);
                errors.put("Error: " + e.getMessage(), -1 );
            }
        }
        return managers;  
    }
    
    Manager parseManagerElement(Element manager, String filePath){
        NodeList classList = manager.getElementsByTagName("class");
        Manager m = null;
        String name = manager.getAttribute("name");
        String id = manager.getAttribute("id");
        DefaultRecord is = informationState;
        if(classList.getLength() > 0){
            if(classList.getLength() > 1){
                System.out.println("More than one class node added in manager " + name + " (" +id+"). Using first!");
            }
            Node firstManager = classList.item(0);
            NamedNodeMap attribs = firstManager.getAttributes();
            Node className = attribs.getNamedItem("classname");
            m = getManagerInstance(className.getNodeValue(), is);
            if(m == null){
                System.err.println("Manager "+ className +" in manager " + name + " (" +id+") could not be instantated. Make sure its folder is added in the classpath and its dependencies are in the same folder! (run.bat default: '/modules/' and '/modules/lib/)");
                return null;
            }
            m.setName(name);
            m.setID(id);
            NodeList paramNodes = firstManager.getChildNodes();
            
            HashMap<String, String> parameters = new HashMap<>();
            HashMap<String, String[]> paramList = new HashMap<>();
            
            for (int i = 0; i < paramNodes.getLength(); i++) {
                Node current = paramNodes.item(i);
                if(current.getNodeName().equals("parameter")){
                    NamedNodeMap ats = current.getAttributes();
                    if(ats.getNamedItem("path").getNodeValue() != null && ats.getNamedItem("path").getNodeValue().equals("true")){
                        String path = ats.getNamedItem("value").getNodeValue(); 
                        File a = new File(path);
                        if(!a.isAbsolute()){
                            File parentFolder = new File(new File(filePath).getParent());
                            File b = new File(parentFolder, path);
                            try{
                                path = b.getCanonicalPath();
                            }catch(IOException ex){
                                System.err.println("Could not resolve '"+path+"' to an absolute path.");
                                return null;
                            }
                        }
                        parameters.put(ats.getNamedItem("name").getNodeValue(), path); //todo: nullcheck
                    }else{
                        parameters.put(ats.getNamedItem("name").getNodeValue(), ats.getNamedItem("value").getNodeValue()); //todo: nullcheck
                    }
                }else if(current.getNodeName().equals("parameterarray")){
                    NodeList currentChilds = current.getChildNodes();
                    ArrayList<String> vals = new ArrayList<>();
                    for (int j = 0; j < currentChilds.getLength(); j++) {
                        Node currentChild = currentChilds.item(j);
                        
                        if(currentChild.getNodeName().equals("item")){
                             NamedNodeMap childAts = currentChild.getAttributes();
                             if(childAts.getNamedItem("path").getNodeValue() != null && childAts.getNamedItem("path").getNodeValue().equals("true")){
                                String path = childAts.getNamedItem("value").getNodeValue(); 
                                File a = new File(path);
                                if(!a.isAbsolute()){
                                    File parentFolder = new File(new File(filePath).getParent());
                                    File b = new File(parentFolder, path);
                                    try{
                                        path = b.getCanonicalPath();
                                    }catch(IOException ex){
                                        System.err.println("Could not resolve '"+path+"' to an absolute path.");
                                        return null;
                                    }
                                }
                                vals.add(path);
                             }else{
                                vals.add(childAts.getNamedItem("value").getNodeValue());//todo: nullcheck
                             }
                             
                        }
                    }

                    NamedNodeMap ats = current.getAttributes();
                    paramList.put(ats.getNamedItem("name").getNodeValue(), vals.toArray(new String[vals.size()]));//todo: nullcheck
                }
            }
            System.out.println("Setting params: "+ m.getName());
            m.setParams(parameters, paramList);
            System.out.println("Set params: "+ m.getName());
        }
        else
        {
            m = new SimpleManager(is); 
            m.setName(name);
            m.setID(id);
        }  
        
        

        try{
            m.setInterval(Integer.parseInt(manager.getAttribute("interval")));
        }catch(NumberFormatException e){
            
        }
       
        NodeList isList = manager.getElementsByTagName("informationstate"); 
        if(isList.getLength() > 0) 
        {
            if(isList.getLength() > 1)
            {
                 System.out.println("More than one informationstate node added in manager " + name + " (" +id+"). Using first!");
            }
            Node firstIS = isList.item(0);
            NodeList children = firstIS.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node current = children.item(i);
                if(current.getNodeName().equals("state")){
                    NamedNodeMap attr = current.getAttributes();
                    boolean override = !(attr.getNamedItem("override") == null || ( attr.getNamedItem("override").getNodeValue().equals("false") ));
                    if(override || is.getValueOfPath(attr.getNamedItem("name").getNodeValue()) == null){
                        is.set(attr.getNamedItem("name").getNodeValue(),  (Object) attr.getNamedItem("value").getNodeValue());
                    }
                }
            }
        }
        
        NodeList behavioursList = manager.getElementsByTagName("behaviours");
        if(behavioursList.getLength() > 0) 
        {
            if(behavioursList.getLength() > 1)
            {
                System.out.println("More than one behaviours node added in manager " + name + " (" +id+"). Using first!");
            }
            Node firstBehaviour = behavioursList.item(0);
            NodeList children = firstBehaviour.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node current = children.item(i);
                if(current.getNodeName().equals("behaviour")){
                    NamedNodeMap attr = current.getAttributes();
                    String className = attr.getNamedItem("classname").getNodeValue();
                    ManageableBehaviourClass c = getBehaviourClassInstance(className);
                    if(c == null){
                        System.err.println("BehaviourClass "+ className +" in manager " + name + " (" +id+") could not be instantated. Make sure its folder is added in the classpath and its dependencies are in the same folder! (run.bat default: '/modules/' and '/modules/lib/)");
                    }else{
                        c.setManager(m);
                        m.addGlobalBehaviour(className, c);
                    }
                }
            }
        }
        NodeList templatesList = manager.getElementsByTagName("templates");
        if(templatesList.getLength() > 0) 
        {
            if(templatesList.getLength() > 1)
            {
                System.out.println("More than one templates node added in manager " + name + " (" +id+"). Using first!");
            }
            Node firstTemplate = templatesList.item(0);
            NodeList children = firstTemplate.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node current = children.item(i);
                if(current.getNodeName().equals("template")){
                    NamedNodeMap attr = current.getAttributes();
                    String templPath = attr.getNamedItem("path").getNodeValue();
                    File a = new File(templPath);
                    if(a.isAbsolute()){
                        m.addTemplateFile(templPath);
                        
                    }else{
                        File parentFolder = new File(new File(filePath).getParent());
                        File b = new File(parentFolder, templPath);
                        try{
                            String absolute = b.getCanonicalPath();
                            m.addTemplateFile(absolute);
                        }catch(IOException ex){
                            System.err.println("Could not resolve '"+templPath+"' to an absolute path.");
                            return null;
                        }
                    }
                    
                }
            }
        }
        NodeList functionsList = manager.getElementsByTagName("functions");
        if(functionsList.getLength() > 0) 
        {
            if(functionsList.getLength() > 1)
            {
                System.out.println("More than one functions node added in manager " + name + " (" +id+"). Using first!");
            }
            Node firstFunc = functionsList.item(0);
            NodeList children = firstFunc.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node current = children.item(i);
                if(current.getNodeName().equals("function")){
                    NamedNodeMap attr = current.getAttributes();
                    String className = attr.getNamedItem("classname").getNodeValue();
                    ManageableFunction func = getFunctionInstance(className);   

                    if(func == null){
                        System.err.println("Function "+ className +" in manager " + name + " (" +id+") could not be instantated. Make sure its folder is added in the classpath and its dependencies are in the same folder! (run.bat default: '/modules/' and '/modules/lib/)");
                    } else {
                        func.setManager(m);
                        m.addFunction(func);
                    }
                }
            }
        }
        
        return m;
    }
    
    Manager getManagerInstance(String className, DefaultRecord is){
        try {
            Class<?> c = getClass(className);
            if(c == null){
                return null;
            }
            return (Manager) c.getDeclaredConstructor(DefaultRecord.class).newInstance(is);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ManagerParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    ManageableFunction getFunctionInstance(String className){
        try {
            Class<?> c = getClass(className);
            if(c == null){
                return null;
            }
            return (ManageableFunction) c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ManagerParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null; 
   }
    
    ManageableBehaviourClass getBehaviourClassInstance(String className){
        try {
            Class<?> c = getClass(className);
            if(c == null){
                return null;
            }
            return (ManageableBehaviourClass) c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ManagerParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    //URLClassLoader classLoader;
    ClassLoader classLoader;
    Class getClass(String className){
        try {
            /*String folderName = new File(new File(pathName).getParent()).getCanonicalPath();
            if(!folderName.endsWith("/") && !folderName.endsWith("\\")){
                folderName+=File.separator;
            }
            
            //jarFile = new JarFile(pathName);
            //Enumeration<JarEntry> e = jarFile.entries();
            URL url =  new URL("jar:file:"+pathName+"!/");
            ArrayList<URL> urlList = new ArrayList<>();
            urlList.add(url);
            File libs = new File(folderName+"lib");
            
            for (final File fileEntry : libs.listFiles()) {
                if(fileEntry.isFile() && fileEntry.getName().endsWith(".jar")){
                    
                    urlList.add( new URL("jar:file:"+fileEntry.getPath()+"!/"));
                }
            }
            

            if(classLoader == null){
                URL[] urls = new URL[urlList.size()];
                urls = urlList.toArray(urls);
                classLoader = URLClassLoader.newInstance(urls);
            }
            else{

                for(URL urlLoop : classLoader.getURLs()){
                    urlList.remove(urlLoop);
                }
                if(urlList.size() > 0){
                    URL[] urls = new URL[urlList.size()];
                    urls = urlList.toArray(urls);
                    classLoader = URLClassLoader.newInstance(urls, classLoader);
                }
                
            }*/
            if(classLoader == null){
                classLoader = ClassLoader.getSystemClassLoader();
            }
            return classLoader.loadClass(className);
        } catch (/*IOException | */ClassNotFoundException ex) {
            Logger.getLogger(ManagerParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }
    
    /**
     * Inner Class to handle XSD-Errors
     * @author mark
     * @version 0.1
     */
    public class XSDErrorHandler implements ErrorHandler
    {
        private final ManagerParser parser;
        String fileName;
        public XSDErrorHandler(ManagerParser p, String fileName) {
            parser = p;
            this.fileName = fileName;
        }
        
        @Override
        public void error(SAXParseException exception) {
            System.err.println("XSD Error in file '"+fileName+"' (r. "+exception.getLineNumber()+"): "+ exception.getMessage());
            parser.errors.put(exception.getMessage(),exception.getLineNumber());
        }
        
        @Override
        public void fatalError(SAXParseException exception) {
            System.err.println("XSD Error in file '"+fileName+"' (r. "+exception.getLineNumber()+"): "+ exception.getMessage());
            parser.errors.put(exception.getMessage(),exception.getLineNumber());
        }
        
        @Override
        public void warning(SAXParseException exception) {
            System.err.println("XSD Error in file '"+fileName+"' (r. "+exception.getLineNumber()+"): "+ exception.getMessage());
            parser.errors.put(exception.getMessage(),exception.getLineNumber());
        }
    }    
}
