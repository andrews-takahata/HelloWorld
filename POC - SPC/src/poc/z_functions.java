package poc;
//////////////////////////////////////////////////////////////////////////////
// Arquivo: 	z_functions.java
// Autor: 		Andrews Takahata
// Criação: 	08/10/2012
// Editado por:	
// Dt. Edição:	
//////////////////////////////////////////////////////////////////////////////
// Descrição: Biblioteca de funções facilitadoras de chamadas ao WebService
//
//////////////////////////////////////////////////////////////////////////////

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.ca.www.UnicenterServicePlus.ServiceDesk.ArrayOfString;
import com.ca.www.UnicenterServicePlus.ServiceDesk.ListResult;
import com.ca.www.UnicenterServicePlus.ServiceDesk.USD_WebServiceLocator;
import com.ca.www.UnicenterServicePlus.ServiceDesk.USD_WebServiceSoap;


public class z_functions {

	static String SDM_Url = null;
	static String SDM_Username = null;
	static String SDM_Password = null;
	static String config_file = "config\\config_acesso.properties";
	static String error_message = "Componente SDM - Exception: ";

	public static void carregaConfigs() throws Exception{
		String server = null;
		String port = null;
		String user = null;
		String pass = null;
		
		//File file = new File(".");
		//for(String fileNames : file.list()) System.out.println(fileNames);
		
	    Properties props = new Properties();
	    try {
	      props.load(new FileInputStream(config_file));
	      Enumeration<?> elms = props.propertyNames();

	      server = props.getProperty((String)elms.nextElement());
	      port = props.getProperty((String)elms.nextElement());
	      user = props.getProperty((String)elms.nextElement());
	      pass = props.getProperty((String)elms.nextElement());
	     
	    }
	    catch (IOException e) {
	      e.printStackTrace();
	    }
		SDM_Url = "http://" + server + ":" + port + "/axis/services/USD_R11_WebService?wsdl";
		SDM_Username = user;
		SDM_Password = pass;
	}
    	
	public static USD_WebServiceSoap getSoap(){ 
	    try {
		    USD_WebServiceLocator ws = new USD_WebServiceLocator();
	        URL url = new URL(SDM_Url);
	        USD_WebServiceSoap usd = ws.getUSD_WebServiceSoap(url);
	        return usd;
	    }
	    catch (Exception ex) {
	    	print_error(ex);
		    return null;
		}
	}
	
	public static int getSession(USD_WebServiceSoap usd) throws Exception {    
	    try {
	        int sid = usd.login(SDM_Username, SDM_Password);
	        if (sid <= 0) {
	        	System.out.println("Falha no login =(");
	        	throw new Exception("OH GOD");
	        }
	        //System.out.println("Feito login no SDM =)");
	        return sid;
	    }
	    catch (Exception ex) {
	    	print_error(ex);
		    return 0;
		}
	}
	
	public static String getTicketPersidById (int sid, String obj, String ticketID) {
		try {
			ListResult ListaTicket = new ListResult();
			ArrayOfString attrNames = new ArrayOfString();
			
			switch(obj) {
				case "chg":
					ListaTicket = getSoap().doQuery(sid, obj, "chg_ref_num = '" + ticketID + "'");
					break;
				default:
					ListaTicket = getSoap().doQuery(sid, obj, "ref_num = '" + ticketID + "'");
					break;
			}
			
			int listaHandle = ListaTicket.getListHandle();
			attrNames.setString(new String[]{"persistent_id"});
			
			String lResult = getSoap().getListValues(sid, listaHandle, 0, 0, attrNames);   
			String ticketPersid = display_attribute_values(lResult);
			
			return ticketPersid;
		}
		catch (Exception ex) {
			print_error(ex);
		}
		return "";
	}
	
	public static String getContactPersidByLastName (int sid, String last_name) {
		try {
			ListResult ListaCnt = new ListResult();
			ArrayOfString attrNames = new ArrayOfString();

			ListaCnt = getSoap().doQuery(sid, "cnt", "last_name = '" + last_name + "'");
			
			int listaHandle = ListaCnt.getListHandle();
			if(ListaCnt.getListLength() < 1) logf("Contato com nome " + last_name + " nao existe.");
			if(ListaCnt.getListLength() > 1) logf("Contato com nome " + last_name + " ambiguo.");
			
			attrNames.setString(new String[]{"persistent_id"});
			
			String lResult = getSoap().getListValues(sid, listaHandle, 0, 0, attrNames);   
			String cntPersid = display_attribute_values(lResult);
			
			return cntPersid;
		}
		catch (Exception ex) {
			print_error(ex);
		}
		return "";
	}
	
	public static String getRequestAreaPersidSym (int sid, String sym) {
		try {
			ListResult ListaPcat = new ListResult();
			ArrayOfString attrNames = new ArrayOfString();

			ListaPcat = getSoap().doQuery(sid, "pcat", "sym = '" + sym + "'");
			
			int listaHandle = ListaPcat.getListHandle();
			if(ListaPcat.getListLength() < 1) logf("Request area " + sym + " nao existe.");
			if(ListaPcat.getListLength() > 1) logf("Request area " + sym + " ambiguo.");
			
			attrNames.setString(new String[]{"persistent_id"});
			
			String lResult = getSoap().getListValues(sid, listaHandle, 0, 0, attrNames);   
			String pcatPersid = display_attribute_values(lResult);
			
			return pcatPersid;
		}
		catch (Exception ex) {
			print_error(ex);
		}
		return "";
	}

	public static String[] getListWithQuery (int sid, String obj, String where_clause, String[] atributos) {
		String[] retorno = null;
		
		try {
			ListResult Lista = new ListResult();
			Lista = getSoap().doQuery(sid, obj, where_clause);
			int listaHandle = Lista.getListHandle();
			int listaLength = Lista.getListLength();
			   
			ArrayOfString attrNames = new ArrayOfString();
			attrNames.setString(atributos);
	
			String lResult = getSoap().getListValues(sid, listaHandle, 0, listaLength - 1, attrNames);   
			retorno = display_attribute_values(lResult).split(";");
		}
		catch (Exception ex) {
			print_error(ex);
		}
		return retorno;
	}
	
	public static void logf (String mensagem) {
		System.out.println("ALERTA! - " + mensagem );
		return;
	}
	
	public static void print_error(Exception ex){
		System.out.println(error_message + ex.getMessage());
	    ex.printStackTrace(System.out);
	}

 	public static String display_attribute_values(String result) {
 		String retorno = "";
	    try {
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db = dbf.newDocumentBuilder();
	    	Document doc = db.parse(new InputSource(new StringReader(result)));
	    	Element root = doc.getDocumentElement();
	    	NodeList udsObjectList = root.getElementsByTagName("UDSObject");
	    	int udsObjectListLength = udsObjectList.getLength();

	    	for (int j = 0; j < udsObjectListLength; ++j) {
	    		Element udsObjectElement = (Element)udsObjectList.item(j);

	    		NodeList attributesList = udsObjectElement.getElementsByTagName("Attributes");
	    		String outString = "";
	    		if (attributesList.getLength() <= 0)
	    			continue;
	    		Element attributesElement = (Element)attributesList.item(0);
	    		NodeList attributeList = attributesElement.getElementsByTagName("Attribute");

	    		for (int i = 0; i < attributeList.getLength(); ++i) {
	    			Element attributeElement = (Element)attributeList.item(i);
	    			NodeList attrNameList = attributeElement.getElementsByTagName("AttrName");

	    			Element attrNameElement = (Element)attrNameList.item(0);
	    			Text attrNameText = (Text)attrNameElement.getFirstChild();
	    			String attrNameString = attrNameText.getNodeValue();

	    			NodeList attrValueList = attributeElement.getElementsByTagName("AttrValue");
	    			Element attrValueElement = (Element)attrValueList.item(0);
	    			Text attrValueText = (Text)attrValueElement.getFirstChild();
	    			String attrValueString = "";
	    			
	    			if (attrValueText != null)
	    				attrValueString = attrValueText.getNodeValue();
	          
	    			if (i > 0)
	    				outString = outString + ", ";
	    			
	    			outString = outString + attrNameString + ": " + attrValueString;
	    				
	    			if (retorno != "")
	    				retorno = retorno + ";" + attrValueString;
	    			else
	    				retorno = attrValueString;
	    		}
	    	}
	    }
	    catch (Exception ex) {
	    	print_error(ex);
	    }
	    return retorno;
 	}
}