package eu.ariaagent.flipper;

import eu.ariaagent.managers.Manager;
import eu.ariaagent.managers.SimpleManager;
import hmi.flipper.behaviourselection.behaviours.BehaviourClass;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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
 * @author WaterschootJB
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
                    System.out.println("Could not parse: " + filename);
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
        boolean succes = true;
        for( int i=0; i<managerNodeList.getLength(); i++ ) {
            Element managerElement = (Element)managerNodeList.item(i);
            try {
                Manager manager;
                manager = parseManagerElement( managerElement, filePath );
                if(manager != null){
                    managers.add(manager);
                }
            }catch( Exception e ) {
                System.err.println("Manager Parse Error in '"+filePath+"': " + e.getMessage() );
                errors.put("Error: " + e.getMessage(), -1 );
                succes = false;
            }
        }
        if( succes ) return managers;
        else return null;   
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
            
            Node classPath = attribs.getNamedItem("path");
            Node className = attribs.getNamedItem("classname");
            

            if(classPath !=null && className != null){
                String path = classPath.getNodeValue();
                File a = new File(path);
                if(a.isAbsolute()){
                    m = getManagerInstance(path, className.getNodeValue(), is);
                }else{
                    File parentFolder = new File(new File(filePath).getParent());
                    File b = new File(parentFolder, path);
                    try{
                        String absolute = b.getCanonicalPath();
                        m = getManagerInstance(absolute, className.getNodeValue(), is);
                    }catch(IOException ex){
                        System.err.println("Could not resolve '"+path+"' to an absolute path.");
                        return null;
                    }
                }
            }else{
                System.err.println("classname and/or path attributes not given in manager node " + name + " (" +id+") but class node is set. Manager not added!");
                return null;
            }
            NodeList paramNodes = firstManager.getChildNodes();
            
            HashMap<String, String> parameters = new HashMap<>();
            HashMap<String, String[]> paramList = new HashMap<>();
            
            for (int i = 0; i < paramNodes.getLength(); i++) {
                Node current = paramNodes.item(i);
                if(current.getNodeName().equals("parameter")){
                    NamedNodeMap ats = current.getAttributes();
                    parameters.put(ats.getNamedItem("name").getNodeValue(), ats.getNamedItem("value").getNodeValue()); //todo: nullcheck
                }else if(current.getNodeName().equals("parameterarray")){
                    NodeList currentChilds = current.getChildNodes();
                    ArrayList<String> vals = new ArrayList<>();
                    for (int j = 0; j < currentChilds.getLength(); j++) {
                        Node currentChild = currentChilds.item(j);
                        
                        if(currentChild.getNodeName().equals("item")){
                             NamedNodeMap childAts = currentChild.getAttributes();
                             vals.add(childAts.getNamedItem("value").getNodeValue());//todo: nullcheck
                        }
                    }

                    NamedNodeMap ats = current.getAttributes();
                    paramList.put(ats.getNamedItem("name").getNodeValue(), vals.toArray(new String[vals.size()]));//todo: nullcheck
                }
            }
             m.setParams(parameters, paramList);
        }
        else
        {
            m = new SimpleManager(is);        
        }  
        m.setName(name);
        m.setID(id);
        

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
                    String behavPath = attr.getNamedItem("path").getNodeValue();
                    String className = attr.getNamedItem("classname").getNodeValue();
                    File a = new File(behavPath);
                    if(a.isAbsolute()){
                        m.addGlobalBehaviour(className, getBehaviourClassInstance(behavPath, className));

                    }else{
                        File parentFolder = new File(new File(filePath).getParent());
                        File b = new File(parentFolder, behavPath);
                        try{
                            String absolute = b.getCanonicalPath();
                            m.addGlobalBehaviour(className, getBehaviourClassInstance(absolute, className));
                        }catch(IOException ex){
                            System.err.println("Could not resolve '"+behavPath+"' to an absolute path.");
                            return null;
                        }
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
                    String classPath = attr.getNamedItem("path").getNodeValue();
                    String className = attr.getNamedItem("classname").getNodeValue();
                    Object func = null;   
                    File a = new File(classPath);
                    if(a.isAbsolute()){
                        func = getFunctionInstance(classPath, className);
                    }else{
                        File parentFolder = new File(new File(filePath).getParent());
                        File b = new File(parentFolder, classPath);
                        try{
                            String absolute = b.getCanonicalPath();
                            func = getFunctionInstance(absolute, className);
                        }catch(IOException ex){
                            System.err.println("Could not resolve '"+classPath+"' to an absolute path.");
                            return null;
                        }
                    }
                    if(func == null){
                        System.out.println("Function "+ className  +" at "+ classPath +" in manager " + name + " (" +id+") could not be instantated. Make sure it exists!");
                    }
                    m.addFunction(func);
                }
            }
        }
        
        return m;
    }
    
    Manager getManagerInstance(String pathName, String className, DefaultRecord is){
        try {
            Class c = getClass(pathName, className);
            if(c == null){
                return null;
            }
            return (Manager) c.getDeclaredConstructor(DefaultRecord.class).newInstance(is);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ManagerParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    Object getFunctionInstance(String pathName, String className){
        try {
            Class c = getClass(pathName, className);
            if(c == null){
                return null;
            }
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ManagerParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    BehaviourClass getBehaviourClassInstance(String pathName, String className){
        try {
            Class c = getClass(pathName, className);
            if(c == null){
                return null;
            }
            return (BehaviourClass) c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ManagerParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    Class getClass(String pathName, String className){ // todo: do some caching
        JarFile jarFile;
        try {
            jarFile = new JarFile(pathName);
            Enumeration<JarEntry> e = jarFile.entries();
            URL[] urls = { new URL("jar:file:"+pathName+"!/") };
            URLClassLoader cl = URLClassLoader.newInstance(urls);
            return cl.loadClass(className);
        } catch (IOException | ClassNotFoundException ex) {
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
